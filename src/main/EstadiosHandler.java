package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.EstadioDAO;
import model.Estadio;

public class EstadiosHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/estadios")) {
                    listarEstadios(exchange);
                } else if (path.startsWith("/estadios/novo")) {
                    mostrarFormularioNovoEstadio(exchange);
                } else if (path.startsWith("/estadios/editar/")) {
                    String id = path.substring(path.lastIndexOf("/") + 1);
                    mostrarFormularioEditarEstadio(exchange, Integer.parseInt(id));
                }
            } else if (method.equals("POST")) {
                String pathInfo = exchange.getRequestURI().getPath();
                if (pathInfo.equals("/estadios/criar")) {
                    criarEstadio(exchange);
                } else if (pathInfo.startsWith("/estadios/atualizar/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    atualizarEstadio(exchange, Integer.parseInt(id));
                } else if (pathInfo.startsWith("/estadios/excluir/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    excluirEstadio(exchange, Integer.parseInt(id));
                }
            }
        } catch (Exception e) {
            String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                    + "</p><a href='/estadios'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }

    private void listarEstadios(HttpExchange exchange) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        List<Estadio> estadios = dao.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Gerenciar Estádios</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append(
                "table{border-collapse:collapse;width:100%;margin:20px 0;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
        html.append("th{background-color:#f2f2f2;}");
        html.append(".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;}");
        html.append(".btn-primary{background:#3498db;}");
        html.append(".btn-success{background:#27ae60;}");
        html.append(".btn-warning{background:#f39c12;}");
        html.append(".btn-danger{background:#e74c3c;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append("</style></head><body>");
        html.append("<h1>🏟️ Gerenciar Estádios</h1>");
        html.append("<a href='/estadios/novo' class='btn btn-success'>➕ Novo Estádio</a>");
        html.append("<a href='/' class='btn btn-primary'>← Voltar</a>");

        html.append("<table>");
        html.append(
                "<tr><th>ID</th><th>Nome</th><th>Capacidade</th><th>Rua</th><th>Número</th><th>Bairro</th><th>Ações</th></tr>");

        for (Estadio e : estadios) {
            html.append("<tr>");
            html.append("<td>").append(e.getId_estadio()).append("</td>");
            html.append("<td>").append(e.getNome()).append("</td>");
            html.append("<td>").append(e.getCapacidade()).append("</td>");
            html.append("<td>").append(e.getRua()).append("</td>");
            html.append("<td>").append(e.getNumero()).append("</td>");
            html.append("<td>").append(e.getBairro()).append("</td>");
            html.append("<td>");
            html.append("<a href='/estadios/editar/").append(e.getId_estadio())
                    .append("' class='btn btn-warning'>Editar</a>");
            html.append("<form method='POST' action='/estadios/excluir/").append(e.getId_estadio()).append("' style='display:inline;'>");
            html.append("<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza que deseja excluir este estádio?\")'>Excluir</button>");
            html.append("</form>");
            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void mostrarFormularioNovoEstadio(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Novo Estádio</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>➕ Novo Estádio</h1>");
        html.append("<form method='POST' action='/estadios/criar'>");
        html.append("<label>Nome do Estádio:</label><br>");
        html.append("<input type='text' name='nome' required><br><br>");
        html.append("<label>Capacidade:</label><br>");
        html.append("<input type='number' name='capacidade' min='1000' required><br><br>");
        html.append("<label>Rua:</label><br>");
        html.append("<input type='text' name='rua' required><br><br>");
        html.append("<label>Número:</label><br>");
        html.append("<input type='number' name='numero' min='0'><br><br>");
        html.append("<label>Bairro:</label><br>");
        html.append("<input type='text' name='bairro' required><br><br>");
        html.append(
                "<input type='submit' value='Criar Estádio' style='background:#27ae60;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/estadios' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void mostrarFormularioEditarEstadio(HttpExchange exchange, int id) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        Estadio e = dao.buscarPorId(id);
        if (e == null) {
            String error = "<html><body><h1>Estádio não encontrado</h1><a href='/estadios'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Editar Estádio</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>✏️ Editar Estádio</h1>");
        html.append("<form method='POST' action='/estadios/atualizar/").append(id).append("'>");
        html.append("<label>Nome do Estádio:</label><br>");
        html.append("<input type='text' name='nome' value='").append(e.getNome()).append("' required><br><br>");
        html.append("<label>Capacidade:</label><br>");
        html.append("<input type='number' name='capacidade' value='").append(e.getCapacidade())
                .append("' min='1000' required><br><br>");
        html.append("<label>Rua:</label><br>");
        html.append("<input type='text' name='rua' value='").append(e.getRua()).append("' required><br><br>");
        html.append("<label>Número:</label><br>");
        html.append("<input type='number' name='numero' value='").append(e.getNumero()).append("' min='0'><br><br>");
        html.append("<label>Bairro:</label><br>");
        html.append("<input type='text' name='bairro' value='").append(e.getBairro()).append("' required><br><br>");
        html.append(
                "<input type='submit' value='Atualizar Estádio' style='background:#f39c12;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/estadios' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void criarEstadio(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Estadio e = new Estadio();
        e.setNome(params.get("nome"));
        e.setCapacidade(Integer.parseInt(params.get("capacidade")));
        e.setRua(params.get("rua"));
        e.setNumero(Integer.parseInt(params.get("numero")));
        e.setBairro(params.get("bairro"));

        EstadioDAO dao = new EstadioDAO();
        dao.inserir(e);

        exchange.getResponseHeaders().set("Location", "/estadios");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void atualizarEstadio(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Estadio e = new Estadio();
        e.setId_estadio(id);
        e.setNome(params.get("nome"));
        e.setCapacidade(Integer.parseInt(params.get("capacidade")));
        e.setRua(params.get("rua"));
        e.setNumero(Integer.parseInt(params.get("numero")));
        e.setBairro(params.get("bairro"));

        EstadioDAO dao = new EstadioDAO();
        dao.atualizar(e);

        exchange.getResponseHeaders().set("Location", "/estadios");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void excluirEstadio(HttpExchange exchange, int id) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        dao.excluir(id);

        exchange.getResponseHeaders().set("Location", "/estadios");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private Map<String, String> parseFormData(InputStream inputStream) throws IOException {
        Map<String, String> params = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] pairs = line.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(URLDecoder.decode(keyValue[0], "UTF-8"),
                            URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }
        }
        return params;
    }
}
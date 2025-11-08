package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.PresidenteDAO;
import model.Presidente;

public class PresidentesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/presidentes")) {
                    listarPresidentes(exchange);
                } else if (path.startsWith("/presidentes/novo")) {
                    mostrarFormularioNovoPresidente(exchange);
                } else if (path.startsWith("/presidentes/editar/")) {
                    String id = path.substring(path.lastIndexOf("/") + 1);
                    mostrarFormularioEditarPresidente(exchange, Integer.parseInt(id));
                }
            } else if (method.equals("POST")) {
                String pathInfo = exchange.getRequestURI().getPath();
                if (pathInfo.equals("/presidentes/criar")) {
                    criarPresidente(exchange);
                } else if (pathInfo.startsWith("/presidentes/atualizar/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    atualizarPresidente(exchange, Integer.parseInt(id));
                } else if (pathInfo.startsWith("/presidentes/excluir/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    excluirPresidente(exchange, Integer.parseInt(id));
                }
            }
        } catch (Exception e) {
            String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                    + "</p><a href='/presidentes'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }

    private void listarPresidentes(HttpExchange exchange) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        List<Presidente> presidentes = dao.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Gerenciar Presidentes</title>");
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
        html.append("<h1>👑 Gerenciar Presidentes</h1>");
        html.append("<a href='/presidentes/novo' class='btn btn-success'>➕ Novo Presidente</a>");
        html.append("<a href='/' class='btn btn-primary'>← Voltar</a>");

        html.append("<table>");
        html.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Títulos</th><th>Tempo de Cargo (meses)</th><th>Ações</th></tr>");

        for (Presidente p : presidentes) {
            html.append("<tr>");
            html.append("<td>").append(p.getId_presidente()).append("</td>");
            html.append("<td>").append(p.getNome()).append("</td>");
            html.append("<td>").append(p.getIdade()).append("</td>");
            html.append("<td>").append(p.getQuant_titulo()).append("</td>");
            html.append("<td>").append(p.getTempo_cargo()).append("</td>");
            html.append("<td>");
            html.append("<a href='/presidentes/editar/").append(p.getId_presidente())
                    .append("' class='btn btn-warning'>Editar</a>");
            html.append("<form method='POST' action='/presidentes/excluir/").append(p.getId_presidente()).append("' style='display:inline;'>");
            html.append("<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza que deseja excluir este presidente?\")'>Excluir</button>");
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

    private void mostrarFormularioNovoPresidente(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Novo Presidente</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>➕ Novo Presidente</h1>");
        html.append("<form method='POST' action='/presidentes/criar'>");
        html.append("<label>Nome do Presidente:</label><br>");
        html.append("<input type='text' name='nome' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' min='18' max='99' required><br><br>");
        html.append("<label>Títulos:</label><br>");
        html.append("<input type='number' name='quant_titulo' min='0' value='0' required><br><br>");
        html.append("<label>Tempo de Cargo (meses):</label><br>");
        html.append("<input type='number' name='tempo_cargo' min='0' value='1' required><br><br>");
        html.append(
                "<input type='submit' value='Criar Presidente' style='background:#27ae60;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/presidentes' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void mostrarFormularioEditarPresidente(HttpExchange exchange, int id) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        Presidente p = dao.buscarPorId(id);
        if (p == null) {
            String error = "<html><body><h1>Presidente não encontrado</h1><a href='/presidentes'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Editar Presidente</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>✏️ Editar Presidente</h1>");
        html.append("<form method='POST' action='/presidentes/atualizar/").append(id).append("'>");
        html.append("<label>Nome do Presidente:</label><br>");
        html.append("<input type='text' name='nome' value='").append(p.getNome()).append("' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' value='").append(p.getIdade())
                .append("' min='18' max='99' required><br><br>");
        html.append("<label>Títulos:</label><br>");
        html.append("<input type='number' name='quant_titulo' value='").append(p.getQuant_titulo())
                .append("' min='0' required><br><br>");
        html.append("<label>Tempo de Cargo (meses):</label><br>");
        html.append("<input type='number' name='tempo_cargo' value='").append(p.getTempo_cargo())
                .append("' min='0' required><br><br>");
        html.append(
                "<input type='submit' value='Atualizar Presidente' style='background:#f39c12;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/presidentes' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void criarPresidente(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Presidente p = new Presidente();
        p.setNome(params.get("nome"));
        p.setIdade(Integer.parseInt(params.get("idade")));
        p.setQuant_titulo(Integer.parseInt(params.get("quant_titulo")));
        p.setTempo_cargo(Integer.parseInt(params.get("tempo_cargo")));

        PresidenteDAO dao = new PresidenteDAO();
        dao.inserir(p);

        exchange.getResponseHeaders().set("Location", "/presidentes");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void atualizarPresidente(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Presidente p = new Presidente();
        p.setId_presidente(id);
        p.setNome(params.get("nome"));
        p.setIdade(Integer.parseInt(params.get("idade")));
        p.setQuant_titulo(Integer.parseInt(params.get("quant_titulo")));
        p.setTempo_cargo(Integer.parseInt(params.get("tempo_cargo")));

        PresidenteDAO dao = new PresidenteDAO();
        dao.atualizar(p);

        exchange.getResponseHeaders().set("Location", "/presidentes");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void excluirPresidente(HttpExchange exchange, int id) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        dao.excluir(id);

        exchange.getResponseHeaders().set("Location", "/presidentes");
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
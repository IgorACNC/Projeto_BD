package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.TecnicoDAO;
import model.Tecnico;

public class TecnicosHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/tecnicos")) {
                    listarTecnicos(exchange);
                } else if (path.startsWith("/tecnicos/novo")) {
                    mostrarFormularioNovoTecnico(exchange);
                } else if (path.startsWith("/tecnicos/editar/")) {
                    String id = path.substring(path.lastIndexOf("/") + 1);
                    mostrarFormularioEditarTecnico(exchange, Integer.parseInt(id));
                }
            } else if (method.equals("POST")) {
                String pathInfo = exchange.getRequestURI().getPath();
                if (pathInfo.equals("/tecnicos/criar")) {
                    criarTecnico(exchange);
                } else if (pathInfo.startsWith("/tecnicos/atualizar/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    atualizarTecnico(exchange, Integer.parseInt(id));
                } else if (pathInfo.startsWith("/tecnicos/excluir/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    excluirTecnico(exchange, Integer.parseInt(id));
                }
            }
        } catch (Exception e) {
            String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                    + "</p><a href='/tecnicos'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }

    private void listarTecnicos(HttpExchange exchange) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        List<Tecnico> tecnicos = dao.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Gerenciar Técnicos</title>");
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
        html.append("<h1>📋 Gerenciar Técnicos</h1>");
        html.append("<a href='/tecnicos/novo' class='btn btn-success'>➕ Novo Técnico</a>");
        html.append("<a href='/' class='btn btn-primary'>← Voltar</a>");

        html.append("<table>");
        html.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Times Treinados</th><th>Ações</th></tr>");

        for (Tecnico tecnico : tecnicos) {
            html.append("<tr>");
            html.append("<td>").append(tecnico.getId_tecnico()).append("</td>");
            html.append("<td>").append(tecnico.getNome()).append("</td>");
            html.append("<td>").append(tecnico.getIdade()).append("</td>");
            html.append("<td>").append(tecnico.getQuant_time_treinou()).append("</td>");
            html.append("<td>");
            html.append("<a href='/tecnicos/editar/").append(tecnico.getId_tecnico())
                    .append("' class='btn btn-warning'>Editar</a>");
            html.append("<form method='POST' action='/tecnicos/excluir/").append(tecnico.getId_tecnico()).append("' style='display:inline;'>");
            html.append("<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza que deseja excluir este técnico?\")'>Excluir</button>");
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

    private void mostrarFormularioNovoTecnico(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Novo Técnico</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>➕ Novo Técnico</h1>");
        html.append("<form method='POST' action='/tecnicos/criar'>");
        html.append("<label>Nome do Técnico:</label><br>");
        html.append("<input type='text' name='nome' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' min='18' max='80' required><br><br>");
        html.append("<label>Times Treinados:</label><br>");
        html.append("<input type='number' name='quant_time_treinou' min='0' value='1' required><br><br>");
        html.append(
                "<input type='submit' value='Criar Técnico' style='background:#27ae60;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/tecnicos' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void mostrarFormularioEditarTecnico(HttpExchange exchange, int id) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        Tecnico tecnico = dao.buscarPorId(id);
        if (tecnico == null) {
            String error = "<html><body><h1>Técnico não encontrado</h1><a href='/tecnicos'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
            return;
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Editar Técnico</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>✏️ Editar Técnico</h1>");
        html.append("<form method='POST' action='/tecnicos/atualizar/").append(id).append("'>");
        html.append("<label>Nome do Técnico:</label><br>");
        html.append("<input type='text' name='nome' value='").append(tecnico.getNome()).append("' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' value='").append(tecnico.getIdade())
                .append("' min='18' max='80' required><br><br>");
        html.append("<label>Times Treinados:</label><br>");
        html.append("<input type='number' name='quant_time_treinou' value='").append(tecnico.getQuant_time_treinou())
                .append("' min='0' required><br><br>");
        html.append(
                "<input type='submit' value='Atualizar Técnico' style='background:#f39c12;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/tecnicos' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void criarTecnico(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Tecnico tecnico = new Tecnico();
        tecnico.setNome(params.get("nome"));
        tecnico.setIdade(Integer.parseInt(params.get("idade")));
        tecnico.setQuant_time_treinou(Integer.parseInt(params.get("quant_time_treinou")));

        TecnicoDAO dao = new TecnicoDAO();
        dao.inserir(tecnico);

        exchange.getResponseHeaders().set("Location", "/tecnicos");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void atualizarTecnico(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Tecnico tecnico = new Tecnico();
        tecnico.setId_tecnico(id);
        tecnico.setNome(params.get("nome"));
        tecnico.setIdade(Integer.parseInt(params.get("idade")));
        tecnico.setQuant_time_treinou(Integer.parseInt(params.get("quant_time_treinou")));

        TecnicoDAO dao = new TecnicoDAO();
        dao.atualizar(tecnico);

        exchange.getResponseHeaders().set("Location", "/tecnicos");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void excluirTecnico(HttpExchange exchange, int id) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        dao.excluir(id);

        exchange.getResponseHeaders().set("Location", "/tecnicos");
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
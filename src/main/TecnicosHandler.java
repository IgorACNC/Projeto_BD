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
import main.Template; // Importe a nova classe

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
            String errorContent = "<h1>Erro Inesperado</h1><p>" + e.getMessage()
                    + "</p><a href='/tecnicos' class='btn btn-primary'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "tecnicos", errorContent);
            sendResponse(exchange, errorHtml, 500);
        }
    }

    private void listarTecnicos(HttpExchange exchange) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        List<Tecnico> tecnicos = dao.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>📋 Gerenciar Técnicos</h1>");
        content.append("<a href='/tecnicos/novo' class='btn btn-success'>➕ Novo Técnico</a>");

        content.append("<table>");
        content.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Times Treinados</th><th>Ações</th></tr>");

        for (Tecnico tecnico : tecnicos) {
            content.append("<tr>");
            content.append("<td>").append(tecnico.getId_tecnico()).append("</td>");
            content.append("<td>").append(tecnico.getNome()).append("</td>");
            content.append("<td>").append(tecnico.getIdade()).append("</td>");
            content.append("<td>").append(tecnico.getQuant_time_treinou()).append("</td>");
            content.append("<td>");
            content.append("<a href='/tecnicos/editar/").append(tecnico.getId_tecnico())
                    .append("' class='btn btn-warning'>Editar</a>");
            content.append("<form method='POST' action='/tecnicos/excluir/").append(tecnico.getId_tecnico())
                    .append("' style='display:inline;'>");
            content.append(
                    "<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</button>");
            content.append("</form>");
            content.append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String html = Template.render("Gerenciar Técnicos", "tecnicos", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioNovoTecnico(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder content = new StringBuilder();
        content.append("<h1>➕ Novo Técnico</h1>");
        content.append("<form method='POST' action='/tecnicos/criar'>");
        content.append("<label>Nome do Técnico:</label>");
        content.append("<input type='text' name='nome' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' min='18' max='80' required><br><br>");
        content.append("<label>Times Treinados:</label>");
        content.append("<input type='number' name='quant_time_treinou' min='0' value='1' required><br><br>");
        content.append("<input type='submit' value='Criar Técnico'>");
        content.append("<a href='/tecnicos' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Novo Técnico", "tecnicos", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioEditarTecnico(HttpExchange exchange, int id) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        Tecnico tecnico = dao.buscarPorId(id);
        if (tecnico == null) {
            String errorContent = "<h1>Técnico não encontrado</h1><a href='/tecnicos'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "tecnicos", errorContent);
            sendResponse(exchange, errorHtml, 404);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("<h1>✏️ Editar Técnico</h1>");
        content.append("<form method='POST' action='/tecnicos/atualizar/").append(id).append("'>");
        content.append("<label>Nome do Técnico:</label>");
        content.append("<input type='text' name='nome' value='").append(tecnico.getNome()).append("' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' value='").append(tecnico.getIdade())
                .append("' min='18' max='80' required><br><br>");
        content.append("<label>Times Treinados:</label>");
        content.append("<input type='number' name='quant_time_treinou' value='").append(tecnico.getQuant_time_treinou())
                .append("' min='0' required><br><br>");
        content.append(
                "<input type='submit' value='Atualizar Técnico' style='background:#f59e0b;'>");
        content.append("<a href='/tecnicos' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Editar Técnico", "tecnicos", content.toString());
        sendResponse(exchange, html, 200);
    }

    // --- Métodos de Ação (POST) ---
    private void criarTecnico(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Tecnico tecnico = new Tecnico();
        tecnico.setNome(params.get("nome"));
        tecnico.setIdade(Integer.parseInt(params.get("idade")));
        tecnico.setQuant_time_treinou(Integer.parseInt(params.get("quant_time_treinou")));

        TecnicoDAO dao = new TecnicoDAO();
        dao.inserir(tecnico);
        sendRedirect(exchange, "/tecnicos");
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
        sendRedirect(exchange, "/tecnicos");
    }

    private void excluirTecnico(HttpExchange exchange, int id) throws IOException, SQLException {
        TecnicoDAO dao = new TecnicoDAO();
        dao.excluir(id);
        sendRedirect(exchange, "/tecnicos");
    }

    // --- Métodos Utilitários (Helpers) ---
    private Map<String, String> parseFormData(InputStream inputStream) throws IOException {
        Map<String, String> params = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        if (line == null) return params;

        for (String pair : line.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], "UTF-8"),
                        URLDecoder.decode(keyValue[1], "UTF-8"));
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, String html, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, html.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes());
        }
    }

    private void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
}
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
import main.Template; // Importe a nova classe

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
            String errorContent = "<h1>Erro Inesperado</h1><p>" + e.getMessage()
                    + "</p><a href='/presidentes' class='btn btn-primary'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "presidentes", errorContent);
            sendResponse(exchange, errorHtml, 500);
        }
    }

    private void listarPresidentes(HttpExchange exchange) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        List<Presidente> presidentes = dao.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>👑 Gerenciar Presidentes</h1>");
        content.append("<a href='/presidentes/novo' class='btn btn-success'>➕ Novo Presidente</a>");

        content.append("<table>");
        content.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Títulos</th><th>Tempo de Cargo (meses)</th><th>Ações</th></tr>");

        for (Presidente p : presidentes) {
            content.append("<tr>");
            content.append("<td>").append(p.getId_presidente()).append("</td>");
            content.append("<td>").append(p.getNome()).append("</td>");
            content.append("<td>").append(p.getIdade()).append("</td>");
            content.append("<td>").append(p.getQuant_titulo()).append("</td>");
            content.append("<td>").append(p.getTempo_cargo()).append("</td>");
            content.append("<td>");
            content.append("<a href='/presidentes/editar/").append(p.getId_presidente())
                    .append("' class='btn btn-warning'>Editar</a>");
            content.append("<form method='POST' action='/presidentes/excluir/").append(p.getId_presidente())
                    .append("' style='display:inline;'>");
            content.append(
                    "<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</button>");
            content.append("</form>");
            content.append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String html = Template.render("Gerenciar Presidentes", "presidentes", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioNovoPresidente(HttpExchange exchange) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<h1>➕ Novo Presidente</h1>");
        content.append("<form method='POST' action='/presidentes/criar'>");
        content.append("<label>Nome do Presidente:</label>");
        content.append("<input type='text' name='nome' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' min='18' max='99' required><br><br>");
        content.append("<label>Títulos:</label>");
        content.append("<input type='number' name='quant_titulo' min='0' value='0' required><br><br>");
        content.append("<label>Tempo de Cargo (meses):</label>");
        content.append("<input type='number' name='tempo_cargo' min='0' value='1' required><br><br>");
        content.append("<input type='submit' value='Criar Presidente'>");
        content.append("<a href='/presidentes' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Novo Presidente", "presidentes", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioEditarPresidente(HttpExchange exchange, int id) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        Presidente p = dao.buscarPorId(id);
        if (p == null) {
            String errorContent = "<h1>Presidente não encontrado</h1><a href='/presidentes'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "presidentes", errorContent);
            sendResponse(exchange, errorHtml, 404);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("<h1>✏️ Editar Presidente</h1>");
        content.append("<form method='POST' action='/presidentes/atualizar/").append(id).append("'>");
        content.append("<label>Nome do Presidente:</label>");
        content.append("<input type='text' name='nome' value='").append(p.getNome()).append("' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' value='").append(p.getIdade())
                .append("' min='18' max='99' required><br><br>");
        content.append("<label>Títulos:</label>");
        content.append("<input type='number' name='quant_titulo' value='").append(p.getQuant_titulo())
                .append("' min='0' required><br><br>");
        content.append("<label>Tempo de Cargo (meses):</label>");
        content.append("<input type='number' name='tempo_cargo' value='").append(p.getTempo_cargo())
                .append("' min='0' required><br><br>");
        content.append(
                "<input type='submit' value='Atualizar Presidente' style='background:#f59e0b;'>");
        content.append("<a href='/presidentes' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Editar Presidente", "presidentes", content.toString());
        sendResponse(exchange, html, 200);
    }

    // --- Métodos de Ação (POST) ---
    private void criarPresidente(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Presidente p = new Presidente();
        p.setNome(params.get("nome"));
        p.setIdade(Integer.parseInt(params.get("idade")));
        p.setQuant_titulo(Integer.parseInt(params.get("quant_titulo")));
        p.setTempo_cargo(Integer.parseInt(params.get("tempo_cargo")));

        PresidenteDAO dao = new PresidenteDAO();
        dao.inserir(p);
        sendRedirect(exchange, "/presidentes");
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
        sendRedirect(exchange, "/presidentes");
    }

    private void excluirPresidente(HttpExchange exchange, int id) throws IOException, SQLException {
        PresidenteDAO dao = new PresidenteDAO();
        dao.excluir(id);
        sendRedirect(exchange, "/presidentes");
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
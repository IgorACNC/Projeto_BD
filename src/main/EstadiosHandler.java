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
import main.Template; // Importe a nova classe

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
            String errorContent = "<h1>Erro Inesperado</h1><p>" + e.getMessage()
                    + "</p><a href='/estadios' class='btn btn-primary'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "estadios", errorContent);
            sendResponse(exchange, errorHtml, 500);
        }
    }

    private void listarEstadios(HttpExchange exchange) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        List<Estadio> estadios = dao.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>🏟️ Gerenciar Estádios</h1>");
        content.append("<a href='/estadios/novo' class='btn btn-success'>➕ Novo Estádio</a>");

        content.append("<table>");
        content.append(
                "<tr><th>ID</th><th>Nome</th><th>Capacidade</th><th>Rua</th><th>Número</th><th>Bairro</th><th>Ações</th></tr>");

        for (Estadio e : estadios) {
            content.append("<tr>");
            content.append("<td>").append(e.getId_estadio()).append("</td>");
            content.append("<td>").append(e.getNome()).append("</td>");
            content.append("<td>").append(e.getCapacidade()).append("</td>");
            content.append("<td>").append(e.getRua()).append("</td>");
            content.append("<td>").append(e.getNumero()).append("</td>");
            content.append("<td>").append(e.getBairro()).append("</td>");
            content.append("<td>");
            content.append("<a href='/estadios/editar/").append(e.getId_estadio())
                    .append("' class='btn btn-warning'>Editar</a>");
            content.append("<form method='POST' action='/estadios/excluir/").append(e.getId_estadio())
                    .append("' style='display:inline;'>");
            content.append(
                    "<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</button>");
            content.append("</form>");
            content.append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String html = Template.render("Gerenciar Estádios", "estadios", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioNovoEstadio(HttpExchange exchange) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<h1>➕ Novo Estádio</h1>");
        content.append("<form method='POST' action='/estadios/criar'>");
        content.append("<label>Nome do Estádio:</label>");
        content.append("<input type='text' name='nome' required><br><br>");
        content.append("<label>Capacidade:</label>");
        content.append("<input type='number' name='capacidade' min='1000' required><br><br>");
        content.append("<label>Rua:</label>");
        content.append("<input type='text' name='rua' required><br><br>");
        content.append("<label>Número:</label>");
        content.append("<input type='number' name='numero' min='0'><br><br>");
        content.append("<label>Bairro:</label>");
        content.append("<input type='text' name='bairro' required><br><br>");
        content.append("<input type='submit' value='Criar Estádio'>");
        content.append("<a href='/estadios' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Novo Estádio", "estadios", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioEditarEstadio(HttpExchange exchange, int id) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        Estadio e = dao.buscarPorId(id);
        if (e == null) {
            String errorContent = "<h1>Estádio não encontrado</h1><a href='/estadios'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "estadios", errorContent);
            sendResponse(exchange, errorHtml, 404);
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("<h1>✏️ Editar Estádio</h1>");
        content.append("<form method='POST' action='/estadios/atualizar/").append(id).append("'>");
        content.append("<label>Nome do Estádio:</label>");
        content.append("<input type='text' name='nome' value='").append(e.getNome()).append("' required><br><br>");
        content.append("<label>Capacidade:</label>");
        content.append("<input type='number' name='capacidade' value='").append(e.getCapacidade())
                .append("' min='1000' required><br><br>");
        content.append("<label>Rua:</label>");
        content.append("<input type='text' name='rua' value='").append(e.getRua()).append("' required><br><br>");
        content.append("<label>Número:</label>");
        content.append("<input type='number' name='numero' value='").append(e.getNumero()).append("' min='0'><br><br>");
        content.append("<label>Bairro:</label>");
        content.append("<input type='text' name='bairro' value='").append(e.getBairro()).append("' required><br><br>");
        content.append(
                "<input type='submit' value='Atualizar Estádio' style='background:#f59e0b;'>");
        content.append("<a href='/estadios' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Editar Estádio", "estadios", content.toString());
        sendResponse(exchange, html, 200);
    }

    // --- Métodos de Ação (POST) ---
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
        sendRedirect(exchange, "/estadios");
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
        sendRedirect(exchange, "/estadios");
    }

    private void excluirEstadio(HttpExchange exchange, int id) throws IOException, SQLException {
        EstadioDAO dao = new EstadioDAO();
        dao.excluir(id);
        sendRedirect(exchange, "/estadios");
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
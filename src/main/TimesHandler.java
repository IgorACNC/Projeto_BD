package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.TimeDAO;
import dao.TecnicoDAO;
import dao.PresidenteDAO;
import dao.EstadioDAO;
import model.Time;
import model.Tecnico;
import model.Presidente;
import model.Estadio;
import main.Template;

public class TimesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/times")) {
                    listarTimes(exchange);
                } else if (path.startsWith("/times/novo")) {
                    mostrarFormularioNovoTime(exchange);
                } else if (path.startsWith("/times/editar/")) {
                    String id = path.substring(path.lastIndexOf("/") + 1);
                    mostrarFormularioEditarTime(exchange, Integer.parseInt(id));
                }
            } else if (method.equals("POST")) {
                String pathInfo = exchange.getRequestURI().getPath();
                if (pathInfo.equals("/times/criar")) {
                    criarTime(exchange);
                } else if (pathInfo.startsWith("/times/atualizar/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    atualizarTime(exchange, Integer.parseInt(id));
                } else if (pathInfo.startsWith("/times/excluir/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    excluirTime(exchange, Integer.parseInt(id));
                }
            }
        } catch (Exception e) {
            String errorContent = "<h1>Erro Inesperado</h1><p>" + e.getMessage()
                    + "</p><a href='/times' class='btn btn-primary'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "times", errorContent);
            sendResponse(exchange, errorHtml, 500);
        }
    }

    private void listarTimes(HttpExchange exchange) throws IOException, SQLException {
        TimeDAO dao = new TimeDAO();
        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        PresidenteDAO presidenteDAO = new PresidenteDAO();
        EstadioDAO estadioDAO = new EstadioDAO();

        List<Time> times = dao.listar();
        List<Tecnico> tecnicos = tecnicoDAO.listar();
        List<Presidente> presidentes = presidenteDAO.listar();
        List<Estadio> estadios = estadioDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>⚽ Gerenciar Times</h1>");
        content.append("<a href='/times/novo' class='btn btn-success'>➕ Novo Time</a>");

        content.append("<table>");
        content.append(
                "<tr><th>ID</th><th>Nome</th><th>Jogadores</th><th>Sócios</th><th>Técnico</th><th>Presidente</th><th>Estádio</th><th>Ações</th></tr>");

        for (Time time : times) {
            String tecnicoNome = getTecnicoNome(tecnicos, time.getFk_tecnico());
            String presidenteNome = getPresidenteNome(presidentes, time.getFk_presidente());
            String estadioNome = getEstadioNome(estadios, time.getFk_estadio());

            content.append("<tr>");
            content.append("<td>").append(time.getId_time()).append("</td>");
            content.append("<td>").append(time.getNome()).append("</td>");
            content.append("<td>").append(time.getQuant_jogadores()).append("</td>");
            content.append("<td>").append(time.getQuant_socios()).append("</td>");
            content.append("<td>").append(tecnicoNome).append("</td>");
            content.append("<td>").append(presidenteNome).append("</td>");
            content.append("<td>").append(estadioNome).append("</td>");
            content.append("<td>");
            content.append("<a href='/times/editar/").append(time.getId_time())
                    .append("' class='btn btn-warning'>Editar</a>");
            content.append("<form method='POST' action='/times/excluir/").append(time.getId_time())
                    .append("' style='display:inline;'>");
            content.append(
                    "<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</button>");
            content.append("</form>");
            content.append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");

        String html = Template.render("Gerenciar Times", "times", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioNovoTime(HttpExchange exchange) throws IOException, SQLException {
        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        PresidenteDAO presidenteDAO = new PresidenteDAO();
        EstadioDAO estadioDAO = new EstadioDAO();

        List<Tecnico> tecnicos = tecnicoDAO.listar();
        List<Presidente> presidentes = presidenteDAO.listar();
        List<Estadio> estadios = estadioDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>➕ Novo Time</h1>");
        content.append("<form method='POST' action='/times/criar'>");
        content.append("<label>Nome do Time:</label>");
        content.append("<input type='text' name='nome' required><br><br>");
        content.append("<label>Quantidade de Jogadores:</label>");
        content.append("<input type='number' name='quant_jogadores' value='11' min='1' max='50' required><br><br>");
        content.append("<label>Quantidade de Sócios:</label>");
        content.append("<input type='number' name='quant_socios' value='0' min='0' required><br><br>");
        content.append("<label>Técnico:</label>");
        content.append("<select name='fk_tecnico' required>");
        content.append("<option value=''>Selecione um técnico</option>");
        for (Tecnico tecnico : tecnicos) {
            content.append("<option value='").append(tecnico.getId_tecnico()).append("'>").append(tecnico.getNome())
                    .append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<label>Presidente:</label>");
        content.append("<select name='fk_presidente' required>");
        content.append("<option value=''>Selecione um presidente</option>");
        for (Presidente presidente : presidentes) {
            content.append("<option value='").append(presidente.getId_presidente()).append("'>")
                    .append(presidente.getNome()).append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<label>Estádio:</label>");
        content.append("<select name='fk_estadio' required>");
        content.append("<option value=''>Selecione um estádio</option>");
        for (Estadio estadio : estadios) {
            content.append("<option value='").append(estadio.getId_estadio()).append("'>").append(estadio.getNome())
                    .append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<input type='submit' value='Criar Time'>");
        content.append("<a href='/times' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Novo Time", "times", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioEditarTime(HttpExchange exchange, int id) throws IOException, SQLException {
        TimeDAO dao = new TimeDAO();
        Time time = dao.buscarPorId(id);
        if (time == null) {
            String errorContent = "<h1>Time não encontrado</h1><a href='/times'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "times", errorContent);
            sendResponse(exchange, errorHtml, 404);
            return;
        }

        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        PresidenteDAO presidenteDAO = new PresidenteDAO();
        EstadioDAO estadioDAO = new EstadioDAO();
        List<Tecnico> tecnicos = tecnicoDAO.listar();
        List<Presidente> presidentes = presidenteDAO.listar();
        List<Estadio> estadios = estadioDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>✏️ Editar Time</h1>");
        content.append("<form method='POST' action='/times/atualizar/").append(id).append("'>");
        content.append("<label>Nome do Time:</label>");
        content.append("<input type='text' name='nome' value='").append(time.getNome()).append("' required><br><br>");
        content.append("<label>Quantidade de Jogadores:</label>");
        content.append("<input type='number' name='quant_jogadores' value='").append(time.getQuant_jogadores())
                .append("' min='1' max='50' required><br><br>");
        content.append("<label>Quantidade de Sócios:</label>");
        content.append("<input type='number' name='quant_socios' value='").append(time.getQuant_socios())
                .append("' min='0' required><br><br>");
        content.append("<label>Técnico:</label>");
        content.append("<select name='fk_tecnico' required>");
        for (Tecnico tecnico : tecnicos) {
            String selected = (tecnico.getId_tecnico() == time.getFk_tecnico()) ? "selected" : "";
            content.append("<option value='").append(tecnico.getId_tecnico()).append("' ").append(selected).append(">")
                    .append(tecnico.getNome()).append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<label>Presidente:</label>");
        content.append("<select name='fk_presidente' required>");
        for (Presidente presidente : presidentes) {
            String selected = (presidente.getId_presidente() == time.getFk_presidente()) ? "selected" : "";
            content.append("<option value='").append(presidente.getId_presidente()).append("' ").append(selected)
                    .append(">").append(presidente.getNome()).append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<label>Estádio:</label>");
        content.append("<select name='fk_estadio' required>");
        for (Estadio estadio : estadios) {
            String selected = (estadio.getId_estadio() == time.getFk_estadio()) ? "selected" : "";
            content.append("<option value='").append(estadio.getId_estadio()).append("' ").append(selected).append(">")
                    .append(estadio.getNome()).append("</option>");
        }
        content.append("</select><br><br>");
        content.append(
                "<input type='submit' value='Atualizar Time' style='background:#f59e0b;'>");
        content.append("<a href='/times' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Editar Time", "times", content.toString());
        sendResponse(exchange, html, 200);
    }

    // --- Métodos de Ação (POST) ---
    private void criarTime(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Time time = new Time();
        time.setNome(params.get("nome"));
        time.setQuant_jogadores(Integer.parseInt(params.get("quant_jogadores")));
        time.setQuant_socios(Integer.parseInt(params.get("quant_socios")));
        time.setFk_tecnico(Integer.parseInt(params.get("fk_tecnico")));
        time.setFk_presidente(Integer.parseInt(params.get("fk_presidente")));
        time.setFk_estadio(Integer.parseInt(params.get("fk_estadio")));

        TimeDAO dao = new TimeDAO();
        dao.inserir(time);
        sendRedirect(exchange, "/times");
    }

    private void atualizarTime(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Time time = new Time();
        time.setId_time(id);
        time.setNome(params.get("nome"));
        time.setQuant_jogadores(Integer.parseInt(params.get("quant_jogadores")));
        time.setQuant_socios(Integer.parseInt(params.get("quant_socios")));
        time.setFk_tecnico(Integer.parseInt(params.get("fk_tecnico")));
        time.setFk_presidente(Integer.parseInt(params.get("fk_presidente")));
        time.setFk_estadio(Integer.parseInt(params.get("fk_estadio")));

        TimeDAO dao = new TimeDAO();
        dao.atualizar(time);
        sendRedirect(exchange, "/times");
    }

    private void excluirTime(HttpExchange exchange, int id) throws IOException, SQLException {
        TimeDAO dao = new TimeDAO();
        dao.excluir(id);
        sendRedirect(exchange, "/times");
    }

    // --- Métodos Utilitários (Helpers) ---
    private String getTecnicoNome(List<Tecnico> tecnicos, int id) {
        return tecnicos.stream().filter(t -> t.getId_tecnico() == id).map(Tecnico::getNome).findFirst().orElse("N/A");
    }

    private String getPresidenteNome(List<Presidente> presidentes, int id) {
        return presidentes.stream().filter(p -> p.getId_presidente() == id).map(Presidente::getNome).findFirst()
                .orElse("N/A");
    }

    private String getEstadioNome(List<Estadio> estadios, int id) {
        return estadios.stream().filter(e -> e.getId_estadio() == id).map(Estadio::getNome).findFirst().orElse("N/A");
    }

    private Map<String, String> parseFormData(InputStream inputStream) throws IOException {
        Map<String, String> params = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine(); // POST data está em uma linha
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
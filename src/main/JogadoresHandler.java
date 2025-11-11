package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.JogadorDAO;
import dao.TimeDAO;
import model.Jogador;
import model.Time;
import main.Template; // Importe a nova classe

public class JogadoresHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/jogadores")) {
                    listarJogadores(exchange);
                } else if (path.startsWith("/jogadores/novo")) {
                    mostrarFormularioNovoJogador(exchange);
                } else if (path.startsWith("/jogadores/editar/")) {
                    String id = path.substring(path.lastIndexOf("/") + 1);
                    mostrarFormularioEditarJogador(exchange, Integer.parseInt(id));
                }
            } else if (method.equals("POST")) {
                String pathInfo = exchange.getRequestURI().getPath();
                if (pathInfo.equals("/jogadores/criar")) {
                    criarJogador(exchange);
                } else if (pathInfo.startsWith("/jogadores/atualizar/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    atualizarJogador(exchange, Integer.parseInt(id));
                } else if (pathInfo.startsWith("/jogadores/excluir/")) {
                    String id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                    excluirJogador(exchange, Integer.parseInt(id));
                }
            }
        } catch (Exception e) {
            String errorContent = "<h1>Erro Inesperado</h1><p>" + e.getMessage()
                    + "</p><a href='/jogadores' class='btn btn-primary'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "jogadores", errorContent);
            sendResponse(exchange, errorHtml, 500);
        }
    }

    private void listarJogadores(HttpExchange exchange) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        TimeDAO timeDAO = new TimeDAO();

        List<Jogador> jogadores = dao.listar();
        List<Time> times = timeDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>👤 Gerenciar Jogadores</h1>");
        content.append("<a href='/jogadores/novo' class='btn btn-success'>➕ Novo Jogador</a>");

        content.append("<div style='overflow-x: auto;'>"); // Para tabelas largas
        content.append("<table>");
        content.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Altura</th><th>Número</th><th>Times</th><th>Gols</th><th>Pé</th><th>Pênalti</th><th>Cabeça</th><th>Peso</th><th>Posição</th><th>Nacionalidade</th><th>Assist.</th><th>Time</th><th>Ações</th></tr>");

        for (Jogador jogador : jogadores) {
            String timeNome = getTimeNome(times, jogador.getFk_time());
            content.append("<tr>");
            content.append("<td>").append(jogador.getId_jogador()).append("</td>");
            content.append("<td>").append(jogador.getNome()).append("</td>");
            content.append("<td>").append(jogador.getIdade()).append("</td>");
            content.append("<td>").append(jogador.getAltura()).append("</td>");
            content.append("<td>").append(jogador.getNumero_camisa()).append("</td>");
            content.append("<td>").append(jogador.getQuant_times_jogados()).append("</td>");
            content.append("<td>").append(jogador.getGols_temporada_jogador()).append("</td>");
            content.append("<td>").append(jogador.getPe_dominante()).append("</td>");
            content.append("<td>").append(jogador.getGols_penalti()).append("</td>");
            content.append("<td>").append(jogador.getGols_cabeca()).append("</td>");
            content.append("<td>").append(jogador.getPeso()).append("</td>");
            content.append("<td>").append(jogador.getPosicao_jogador()).append("</td>");
            content.append("<td>").append(jogador.getNacionalidade()).append("</td>");
            content.append("<td>").append(jogador.getAssistencias()).append("</td>");
            content.append("<td>").append(timeNome).append("</td>");
            content.append("<td>");
            content.append("<a href='/jogadores/editar/").append(jogador.getId_jogador())
                    .append("' class='btn btn-warning'>Editar</a>");
            content.append("<form method='POST' action='/jogadores/excluir/").append(jogador.getId_jogador())
                    .append("' style='display:inline;'>");
            content.append(
                    "<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</button>");
            content.append("</form>");
            content.append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        content.append("</div>");

        String html = Template.render("Gerenciar Jogadores", "jogadores", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioNovoJogador(HttpExchange exchange) throws IOException, SQLException {
        TimeDAO timeDAO = new TimeDAO();
        List<Time> times = timeDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>➕ Novo Jogador</h1>");
        content.append("<form method='POST' action='/jogadores/criar'>");
        
        content.append("<label>Nome do Jogador:</label>");
        content.append("<input type='text' name='nome' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' min='16' max='50' required><br><br>");
        content.append("<label>Altura (m):</label>");
        content.append("<input type='number' name='altura' step='0.01' min='1.50' max='2.20' required><br><br>");
        content.append("<label>Número da Camisa:</label>");
        content.append("<input type='number' name='numero_camisa' min='1' max='99' required><br><br>");
        content.append("<label>Posição:</label>");
        content.append("<select name='posicao_jogador' required>");
        content.append("<option value=''>Selecione uma posição</option>");
        content.append("<option value='Goleiro'>Goleiro</option>");
        content.append("<option value='Lateral'>Lateral</option>");
        content.append("<option value='Zagueiro'>Zagueiro</option>");
        content.append("<option value='Volante'>Volante</option>");
        content.append("<option value='Meia'>Meia</option>");
        content.append("<option value='Atacante'>Atacante</option>");
        content.append("</select><br><br>");
        content.append("<label>Pé Dominante:</label>");
        content.append("<select name='pe_dominante' required>");
        content.append("<option value=''>Selecione</option>");
        content.append("<option value='destro'>Destro</option>");
        content.append("<option value='canhoto'>Canhoto</option>");
        content.append("</select><br><br>");
        content.append("<label>Peso (kg):</label>");
        content.append("<input type='number' name='peso' step='0.1' min='50' max='120' required><br><br>");
        content.append("<label>Nacionalidade:</label>");
        content.append("<input type='text' name='nacionalidade' required><br><br>");
        content.append("<label>Quantidade de Jogos:</label>");
        content.append("<input type='number' name='quant_jogos' min='0' required><br><br>");
        content.append("<label>Gols na Temporada:</label>");
        content.append("<input type='number' name='gols_temporada_jogador' min='0' value='0'><br><br>");
        content.append("<label>Assistências:</label>");
        content.append("<input type='number' name='assistencias' min='0' value='0'><br><br>");
        content.append("<label>Gols de Pênalti:</label>");
        content.append("<input type='number' name='gols_penalti' min='0' value='0'><br><br>");
        content.append("<label>Gols de Cabeça:</label>");
        content.append("<input type='number' name='gols_cabeca' min='0' value='0'><br><br>");
        content.append("<label>Quantidade de Times Jogados:</label>");
        content.append("<input type='number' name='quant_times_jogados' min='1' value='1'><br><br>");
        content.append("<label>Time:</label>");
        content.append("<select name='fk_time' required>");
        content.append("<option value=''>Selecione um time</option>");
        for (Time time : times) {
            content.append("<option value='").append(time.getId_time()).append("'>").append(time.getNome())
                    .append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<input type='submit' value='Criar Jogador'>");
        content.append("<a href='/jogadores' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Novo Jogador", "jogadores", content.toString());
        sendResponse(exchange, html, 200);
    }

    private void mostrarFormularioEditarJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        Jogador jogador = dao.buscarPorId(id);
        if (jogador == null) {
            String errorContent = "<h1>Jogador não encontrado</h1><a href='/jogadores'>← Voltar</a>";
            String errorHtml = Template.render("Erro", "jogadores", errorContent);
            sendResponse(exchange, errorHtml, 404);
            return;
        }

        TimeDAO timeDAO = new TimeDAO();
        List<Time> times = timeDAO.listar();

        StringBuilder content = new StringBuilder();
        content.append("<h1>✏️ Editar Jogador</h1>");
        content.append("<form method='POST' action='/jogadores/atualizar/").append(id).append("'>");
        
        content.append("<label>Nome do Jogador:</label>");
        content.append("<input type='text' name='nome' value='").append(jogador.getNome()).append("' required><br><br>");
        content.append("<label>Idade:</label>");
        content.append("<input type='number' name='idade' value='").append(jogador.getIdade())
                .append("' min='16' max='50' required><br><br>");
        content.append("<label>Altura (m):</label>");
        content.append("<input type='number' name='altura' step='0.01' value='").append(jogador.getAltura())
                .append("' min='1.50' max='2.20' required><br><br>");
        content.append("<label>Número da Camisa:</label>");
        content.append("<input type='number' name='numero_camisa' value='").append(jogador.getNumero_camisa())
                .append("' min='1' max='99' required><br><br>");
        content.append("<label>Posição:</label>");
        content.append("<select name='posicao_jogador' required>");
        String[] posicoes = { "Goleiro", "Lateral", "Zagueiro", "Volante", "Meia", "Atacante" };
        for (String pos : posicoes) {
            String selected = pos.equals(jogador.getPosicao_jogador()) ? "selected" : "";
            content.append("<option value='").append(pos).append("' ").append(selected).append(">").append(pos)
                    .append("</option>");
        }
        content.append("</select><br><br>");
        content.append("<label>Pé Dominante:</label>");
        content.append("<select name='pe_dominante' required>");
        String destroSelected = "destro".equals(jogador.getPe_dominante()) ? "selected" : "";
        String canhotoSelected = "canhoto".equals(jogador.getPe_dominante()) ? "selected" : "";
        content.append("<option value='destro' ").append(destroSelected).append(">Destro</option>");
        content.append("<option value='canhoto' ").append(canhotoSelected).append(">Canhoto</option>");
        content.append("</select><br><br>");
        content.append("<label>Peso (kg):</label>");
        content.append("<input type='number' name='peso' step='0.1' value='").append(jogador.getPeso())
                .append("' min='50' max='120' required><br><br>");
        content.append("<label>Nacionalidade:</label>");
        content.append("<input type='text' name='nacionalidade' value='").append(jogador.getNacionalidade())
                .append("' required><br><br>");
        content.append("<label>Quantidade de Jogos:</label>");
        content.append("<input type='number' name='quant_jogos' value='").append(jogador.getQuant_jogos())
                .append("' min='0' required><br><br>");
        content.append("<label>Gols na Temporada:</label>");
        content.append("<input type='number' name='gols_temporada_jogador' value='")
                .append(jogador.getGols_temporada_jogador()).append("' min='0'><br><br>");
        content.append("<label>Assistências:</label>");
        content.append("<input type='number' name='assistencias' value='").append(jogador.getAssistencias())
                .append("' min='0'><br><br>");
        content.append("<label>Gols de Pênalti:</label>");
        content.append("<input type='number' name='gols_penalti' value='").append(jogador.getGols_penalti())
                .append("' min='0'><br><br>");
        content.append("<label>Gols de Cabeça:</label>");
        content.append("<input type='number' name='gols_cabeca' value='").append(jogador.getGols_cabeca())
                .append("' min='0'><br><br>");
        content.append("<label>Quantidade de Times Jogados:</label>");
        content.append("<input type='number' name='quant_times_jogados' value='").append(jogador.getQuant_times_jogados())
                .append("' min='1'><br><br>");
        content.append("<label>Time:</label>");
        content.append("<select name='fk_time' required>");
        for (Time time : times) {
            String selected = (time.getId_time() == jogador.getFk_time()) ? "selected" : "";
            content.append("<option value='").append(time.getId_time()).append("' ").append(selected).append(">")
                    .append(time.getNome()).append("</option>");
        }
        content.append("</select><br><br>");
        content.append(
                "<input type='submit' value='Atualizar Jogador' style='background:#f59e0b;'>");
        content.append("<a href='/jogadores' class='btn btn-primary'>Cancelar</a>");
        content.append("</form>");

        String html = Template.render("Editar Jogador", "jogadores", content.toString());
        sendResponse(exchange, html, 200);
    }

    // --- Métodos de Ação (POST) ---
    private void criarJogador(HttpExchange exchange) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Jogador jogador = new Jogador();
        jogador.setNome(params.get("nome"));
        jogador.setIdade(Integer.parseInt(params.get("idade")));
        jogador.setAltura(Float.parseFloat(params.get("altura")));
        jogador.setNumero_camisa(Integer.parseInt(params.get("numero_camisa")));
        jogador.setPosicao_jogador(params.get("posicao_jogador"));
        jogador.setPe_dominante(params.get("pe_dominante"));
        jogador.setPeso(Float.parseFloat(params.get("peso")));
        jogador.setNacionalidade(params.get("nacionalidade"));
        jogador.setQuant_jogos(Integer.parseInt(params.get("quant_jogos")));
        jogador.setGols_temporada_jogador(Integer.parseInt(params.get("gols_temporada_jogador")));
        jogador.setAssistencias(Integer.parseInt(params.get("assistencias")));
        jogador.setGols_penalti(Integer.parseInt(params.get("gols_penalti")));
        jogador.setGols_cabeca(Integer.parseInt(params.get("gols_cabeca")));
        jogador.setQuant_times_jogados(Integer.parseInt(params.get("quant_times_jogados")));
        jogador.setFk_time(Integer.parseInt(params.get("fk_time")));

        JogadorDAO dao = new JogadorDAO();
        dao.inserir(jogador);
        sendRedirect(exchange, "/jogadores");
    }

    private void atualizarJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());
        Jogador jogador = new Jogador();
        jogador.setId_jogador(id);
        // (O resto dos SETs é idêntico ao criarJogador, exceto pelo ID)
        jogador.setNome(params.get("nome"));
        jogador.setIdade(Integer.parseInt(params.get("idade")));
        jogador.setAltura(Float.parseFloat(params.get("altura")));
        jogador.setNumero_camisa(Integer.parseInt(params.get("numero_camisa")));
        jogador.setPosicao_jogador(params.get("posicao_jogador"));
        jogador.setPe_dominante(params.get("pe_dominante"));
        jogador.setPeso(Float.parseFloat(params.get("peso")));
        jogador.setNacionalidade(params.get("nacionalidade"));
        jogador.setQuant_jogos(Integer.parseInt(params.get("quant_jogos")));
        jogador.setGols_temporada_jogador(Integer.parseInt(params.get("gols_temporada_jogador")));
        jogador.setAssistencias(Integer.parseInt(params.get("assistencias")));
        jogador.setGols_penalti(Integer.parseInt(params.get("gols_penalti")));
        jogador.setGols_cabeca(Integer.parseInt(params.get("gols_cabeca")));
        jogador.setQuant_times_jogados(Integer.parseInt(params.get("quant_times_jogados")));
        jogador.setFk_time(Integer.parseInt(params.get("fk_time")));

        JogadorDAO dao = new JogadorDAO();
        dao.atualizar(jogador);
        sendRedirect(exchange, "/jogadores");
    }

    private void excluirJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        dao.excluir(id);
        sendRedirect(exchange, "/jogadores");
    }

    // --- Métodos Utilitários (Helpers) ---
    private String getTimeNome(List<Time> times, int id) {
        return times.stream().filter(t -> t.getId_time() == id).map(Time::getNome).findFirst().orElse("N/A");
    }

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
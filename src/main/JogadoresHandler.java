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
            String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                    + "</p><a href='/jogadores'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }

    private void listarJogadores(HttpExchange exchange) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        TimeDAO timeDAO = new TimeDAO();

        List<Jogador> jogadores = dao.listar();
        List<Time> times = timeDAO.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Gerenciar Jogadores</title>");
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
        html.append("<h1>⚽ Gerenciar Jogadores</h1>");
        html.append("<a href='/jogadores/novo' class='btn btn-success'>➕ Novo Jogador</a>");
        html.append("<a href='/' class='btn btn-primary'>← Voltar</a>");

        html.append("<table>");
        html.append(
                "<tr><th>ID</th><th>Nome</th><th>Idade</th><th>Altura</th><th>Número</th><th>Times jogados</th><th>Gols</th><th>Pé dominante</th><th>Gols Penalti</th><th>Gols cabeça</th><th>Peso</th><th>Posição</th><th>Nacionalidade</th><th>Assistências</th><th>Time</th><th>Ações</th></tr>");

        for (Jogador jogador : jogadores) {
            String timeNome = getTimeNome(times, jogador.getFk_time());

            html.append("<tr>");
            html.append("<td>").append(jogador.getId_jogador()).append("</td>");
            html.append("<td>").append(jogador.getNome()).append("</td>");
            html.append("<td>").append(jogador.getIdade()).append("</td>");
            html.append("<td>").append(jogador.getAltura()).append("</td>");
            html.append("<td>").append(jogador.getNumero_camisa()).append("</td>");
            html.append("<td>").append(jogador.getQuant_times_jogados()).append("</td>");
            html.append("<td>").append(jogador.getGols_temporada_jogador()).append("</td>");
            html.append("<td>").append(jogador.getPe_dominante()).append("</td>");
            html.append("<td>").append(jogador.getGols_penalti()).append("</td>");
            html.append("<td>").append(jogador.getGols_cabeca()).append("</td>");
            html.append("<td>").append(jogador.getPeso()).append("</td>");
            html.append("<td>").append(jogador.getPosicao_jogador()).append("</td>");
            html.append("<td>").append(jogador.getNacionalidade()).append("</td>");
            html.append("<td>").append(jogador.getAssistencias()).append("</td>");
            html.append("<td>").append(timeNome).append("</td>");
            html.append("<td>");
            html.append("<a href='/jogadores/editar/").append(jogador.getId_jogador())
                    .append("' class='btn btn-warning'>Editar</a>");
            html.append("<form method='POST' action='/jogadores/excluir/").append(jogador.getId_jogador()).append("' style='display:inline;'>");
            html.append("<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza que deseja excluir este jogador?\")'>Excluir</button>");
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

    private void mostrarFormularioNovoJogador(HttpExchange exchange) throws IOException, SQLException {
        TimeDAO timeDAO = new TimeDAO();
        List<Time> times = timeDAO.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Novo Jogador</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>➕ Novo Jogador</h1>");
        html.append("<form method='POST' action='/jogadores/criar'>");
        html.append("<label>Nome do Jogador:</label><br>");
        html.append("<input type='text' name='nome' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' min='16' max='50' required><br><br>");
        html.append("<label>Altura (m):</label><br>");
        html.append("<input type='number' name='altura' step='0.01' min='1.50' max='2.20' required><br><br>");
        html.append("<label>Número da Camisa:</label><br>");
        html.append("<input type='number' name='numero_camisa' min='1' max='99' required><br><br>");
        html.append("<label>Posição:</label><br>");
        html.append("<select name='posicao_jogador' required>");
        html.append("<option value=''>Selecione uma posição</option>");
        html.append("<option value='Goleiro'>Goleiro</option>");
        html.append("<option value='Lateral'>Lateral</option>");
        html.append("<option value='Zagueiro'>Zagueiro</option>");
        html.append("<option value='Volante'>Volante</option>");
        html.append("<option value='Meia'>Meia</option>");
        html.append("<option value='Atacante'>Atacante</option>");
        html.append("</select><br><br>");
        html.append("<label>Pé Dominante:</label><br>");
        html.append("<select name='pe_dominante' required>");
        html.append("<option value=''>Selecione</option>");
        html.append("<option value='destro'>Destro</option>");
        html.append("<option value='canhoto'>Canhoto</option>");
        html.append("</select><br><br>");
        html.append("<label>Peso (kg):</label><br>");
        html.append("<input type='number' name='peso' step='0.1' min='50' max='120' required><br><br>");
        html.append("<label>Nacionalidade:</label><br>");
        html.append("<input type='text' name='nacionalidade' required><br><br>");
        html.append("<label>Quantidade de Jogos:</label><br>");
        html.append("<input type='number' name='quant_jogos' min='0' required><br><br>");
        html.append("<label>Gols na Temporada:</label><br>");
        html.append("<input type='number' name='gols_temporada_jogador' min='0' value='0'><br><br>");
        html.append("<label>Assistências:</label><br>");
        html.append("<input type='number' name='assistencias' min='0' value='0'><br><br>");
        html.append("<label>Gols de Pênalti:</label><br>");
        html.append("<input type='number' name='gols_penalti' min='0' value='0'><br><br>");
        html.append("<label>Gols de Cabeça:</label><br>");
        html.append("<input type='number' name='gols_cabeca' min='0' value='0'><br><br>");
        html.append("<label>Quantidade de Times Jogados:</label><br>");
        html.append("<input type='number' name='quant_times_jogados' min='1' value='1'><br><br>");
        html.append("<label>Time:</label><br>");
        html.append("<select name='fk_time' required>");
        html.append("<option value=''>Selecione um time</option>");
        for (Time time : times) {
            html.append("<option value='").append(time.getId_time()).append("'>").append(time.getNome())
                    .append("</option>");
        }
        html.append("</select><br><br>");
        html.append(
                "<input type='submit' value='Criar Jogador' style='background:#27ae60;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/jogadores' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

    private void mostrarFormularioEditarJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        TimeDAO timeDAO = new TimeDAO();

        Jogador jogador = dao.buscarPorId(id);
        if (jogador == null) {
            String error = "<html><body><h1>Jogador não encontrado</h1><a href='/jogadores'>← Voltar</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
            return;
        }

        List<Time> times = timeDAO.listar();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Editar Jogador</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
        html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
        html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
        html.append(
                ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
        html.append("<h1>✏️ Editar Jogador</h1>");
        html.append("<form method='POST' action='/jogadores/atualizar/").append(id).append("'>");
        html.append("<label>Nome do Jogador:</label><br>");
        html.append("<input type='text' name='nome' value='").append(jogador.getNome()).append("' required><br><br>");
        html.append("<label>Idade:</label><br>");
        html.append("<input type='number' name='idade' value='").append(jogador.getIdade())
                .append("' min='16' max='50' required><br><br>");
        html.append("<label>Altura (m):</label><br>");
        html.append("<input type='number' name='altura' step='0.01' value='").append(jogador.getAltura())
                .append("' min='1.50' max='2.20' required><br><br>");
        html.append("<label>Número da Camisa:</label><br>");
        html.append("<input type='number' name='numero_camisa' value='").append(jogador.getNumero_camisa())
                .append("' min='1' max='99' required><br><br>");
        html.append("<label>Posição:</label><br>");
        html.append("<select name='posicao_jogador' required>");
        html.append("<option value=''>Selecione uma posição</option>");
        String[] posicoes = { "Goleiro", "Lateral", "Zagueiro", "Volante", "Meia", "Atacante" };
        for (String pos : posicoes) {
            String selected = pos.equals(jogador.getPosicao_jogador()) ? "selected" : "";
            html.append("<option value='").append(pos).append("' ").append(selected).append(">").append(pos)
                    .append("</option>");
        }
        html.append("</select><br><br>");
        html.append("<label>Pé Dominante:</label><br>");
        html.append("<select name='pe_dominante' required>");
        html.append("<option value=''>Selecione</option>");
        String destroSelected = "destro".equals(jogador.getPe_dominante()) ? "selected" : "";
        String canhotoSelected = "canhoto".equals(jogador.getPe_dominante()) ? "selected" : "";
        html.append("<option value='destro' ").append(destroSelected).append(">Destro</option>");
        html.append("<option value='canhoto' ").append(canhotoSelected).append(">Canhoto</option>");
        html.append("</select><br><br>");
        html.append("<label>Peso (kg):</label><br>");
        html.append("<input type='number' name='peso' step='0.1' value='").append(jogador.getPeso())
                .append("' min='50' max='120' required><br><br>");
        html.append("<label>Nacionalidade:</label><br>");
        html.append("<input type='text' name='nacionalidade' value='").append(jogador.getNacionalidade())
                .append("' required><br><br>");
        html.append("<label>Quantidade de Jogos:</label><br>");
        html.append("<input type='number' name='quant_jogos' value='").append(jogador.getQuant_jogos())
                .append("' min='0' required><br><br>");
        html.append("<label>Gols na Temporada:</label><br>");
        html.append("<input type='number' name='gols_temporada_jogador' value='")
                .append(jogador.getGols_temporada_jogador()).append("' min='0'><br><br>");
        html.append("<label>Assistências:</label><br>");
        html.append("<input type='number' name='assistencias' value='").append(jogador.getAssistencias())
                .append("' min='0'><br><br>");
        html.append("<label>Gols de Pênalti:</label><br>");
        html.append("<input type='number' name='gols_penalti' value='").append(jogador.getGols_penalti())
                .append("' min='0'><br><br>");
        html.append("<label>Gols de Cabeça:</label><br>");
        html.append("<input type='number' name='gols_cabeca' value='").append(jogador.getGols_cabeca())
                .append("' min='0'><br><br>");
        html.append("<label>Quantidade de Times Jogados:</label><br>");
        html.append("<input type='number' name='quant_times_jogados' value='").append(jogador.getQuant_times_jogados())
                .append("' min='1'><br><br>");
        html.append("<label>Time:</label><br>");
        html.append("<select name='fk_time' required>");
        html.append("<option value=''>Selecione um time</option>");
        for (Time time : times) {
            String selected = (time.getId_time() == jogador.getFk_time()) ? "selected" : "";
            html.append("<option value='").append(time.getId_time()).append("' ").append(selected).append(">")
                    .append(time.getNome()).append("</option>");
        }
        html.append("</select><br><br>");
        html.append(
                "<input type='submit' value='Atualizar Jogador' style='background:#f39c12;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
        html.append("<a href='/jogadores' class='btn'>Cancelar</a>");
        html.append("</form></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }

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

        exchange.getResponseHeaders().set("Location", "/jogadores");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void atualizarJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        Map<String, String> params = parseFormData(exchange.getRequestBody());

        Jogador jogador = new Jogador();
        jogador.setId_jogador(id);
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

        exchange.getResponseHeaders().set("Location", "/jogadores");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void excluirJogador(HttpExchange exchange, int id) throws IOException, SQLException {
        JogadorDAO dao = new JogadorDAO();
        dao.excluir(id);

        exchange.getResponseHeaders().set("Location", "/jogadores");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private String getTimeNome(List<Time> times, int id) {
        for (Time time : times) {
            if (time.getId_time() == id) {
                return time.getNome();
            }
        }
        return "N/A";
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

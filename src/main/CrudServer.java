package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import dao.TimeDAO;
import dao.JogadorDAO;
import dao.TecnicoDAO;
import dao.EstadioDAO;
import dao.PresidenteDAO;
import dao.RelatorioDAO;
import model.Time;
import model.Jogador;
import model.Tecnico;
import model.Estadio;
import model.Presidente;
import main.TecnicosHandler;
import main.PresidentesHandler;
import main.EstadiosHandler;
import main.Template; // Importe a nova classe

public class CrudServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Iniciando servidor CRUD na porta " + PORT + "...");

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Rotas principais
        server.createContext("/", new HomeHandler());
        server.createContext("/times", new TimesHandler());
        server.createContext("/jogadores", new JogadoresHandler());
        server.createContext("/relatorios", new RelatoriosHandler());
        server.createContext("/public/", new StaticFileHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/tecnicos", new TecnicosHandler());
        server.createContext("/presidentes", new PresidentesHandler());
        server.createContext("/estadios", new EstadiosHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor CRUD iniciado com sucesso!");
        System.out.println("🌐 Acesse: http://localhost:" + PORT);
        System.out.println("⏹️  Para parar, pressione Ctrl+C");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            TimeDAO timeDAO = new TimeDAO();
            JogadorDAO jogadorDAO = new JogadorDAO();
            TecnicoDAO tecnicoDAO = new TecnicoDAO();
            EstadioDAO estadioDAO = new EstadioDAO();
            PresidenteDAO presidenteDAO = new PresidenteDAO();

            int totalTimes = 0, totalJogadores = 0, totalTecnicos = 0, totalEstadios = 0, totalPresidentes = 0, artilheiroGols = 0;
            String artilheiroNome = "N/A";
            List<Jogador> jogadoresRecentes = new ArrayList<>();
            List<Time> timesRecentes = new ArrayList<>();

            try {
                totalTimes = timeDAO.listar().size();
                totalJogadores = jogadorDAO.listar().size();
                totalTecnicos = tecnicoDAO.listar().size();
                totalEstadios = estadioDAO.listar().size();
                totalPresidentes = presidenteDAO.listar().size();
                List<String[]> artilheiros = jogadorDAO.buscarArtilheiros();
                if (!artilheiros.isEmpty()) {
                    artilheiroNome = artilheiros.get(0)[0];
                    artilheiroGols = Integer.parseInt(artilheiros.get(0)[1]);
                }
                List<Jogador> todosJogadores = jogadorDAO.listar();
                jogadoresRecentes = todosJogadores.subList(0, Math.min(2, todosJogadores.size()));
                List<Time> todosTimes = timeDAO.listar();
                timesRecentes = todosTimes.subList(0, Math.min(1, todosTimes.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // O CSS foi movido para a classe Template.java
            // O HTML da sidebar também.
            // Vamos apenas construir o CONTEÚDO principal.
            String content = """
                    <div class="header">
                        <h1>Dashboard <span>Brasileirão</span></h1>
                        <p>Visão geral do sistema de gestão do campeonato</p>
                    </div>

                    <div class="stats-grid">
                        <div class="stat-card green">
                            <div class="stat-info">
                                <h3>Times Cadastrados</h3>
                                <div class="number">""" + totalTimes + """
</div>
                                <div class="subtitle">Clubes da Série A</div>
                            </div>
                            <div class="stat-icon">🏆</div>
                        </div>
                        <div class="stat-card blue">
                            <div class="stat-info">
                                <h3>Jogadores Ativos</h3>
                                <div class="number">""" + totalJogadores + """
</div>
                                <div class="subtitle">Atletas profissionais</div>
                            </div>
                            <div class="stat-icon">👥</div>
                        </div>
                        <div class="stat-card purple">
                            <div class="stat-info">
                                <h3>Técnicos</h3>
                                <div class="number">""" + totalTecnicos + """
</div>
                                <div class="subtitle">Treinadores cadastrados</div>
                            </div>
                            <div class="stat-icon">📋</div>
                        </div>
                        <div class="stat-card orange">
                            <div class="stat-info">
                                <h3>Estádios</h3>
                                <div class="number">""" + totalEstadios + """
</div>
                                <div class="subtitle">Arenas e estádios</div>
                            </div>
                            <div class="stat-icon">🏟️</div>
                        </div>
                        <div class="stat-card yellow">
                            <div class="stat-info">
                                <h3>Presidentes</h3>
                                <div class="number">""" + totalPresidentes + """
</div>
                                <div class="subtitle">Dirigentes ativos</div>
                            </div>
                            <div class="stat-icon">👑</div>
                        </div>
                        <div class="stat-card red">
                            <div class="stat-info">
                                <h3>Artilheiro</h3>
                                <div class="number">""" + artilheiroGols + """
                                <div class="subtitle">""" + artilheiroNome + """
</div>
                            </div>
                            <div class="stat-icon">🎯</div>
                        </div>
                    </div>

                    <div class="activity-section">
                        <h2>Atividade Recente</h2>
                        """;

            // Adiciona atividades recentes (ESTE CÓDIGO TEM QUE VIRAR UMA STRING)
            StringBuilder contentBuilder = new StringBuilder(content);
            for (Jogador j : jogadoresRecentes) {
                contentBuilder.append("""
                                <div class="activity-item">
                                    <div class="activity-icon jogador">👤</div>
                                    <div class="activity-info">
                                        <h4>""" + j.getNome() + """
</h4>
                                        <span class="activity-badge jogador">Jogador</span>
                                        <span class="activity-date">26/09/2025 às 12:25</span>
                                    </div>
                                </div>
                                """);
            }
            for (Time t : timesRecentes) {
                contentBuilder.append("""
                                <div class="activity-item">
                                    <div class="activity-icon time">⚽</div>
                                    <div class="activity-info">
                                        <h4>""" + t.getNome() + """
</h4>
                                        <span class="activity-badge time">Time</span>
                                        <span class="activity-date">26/09/2025 às 12:25</span>
                                    </div>
                                </div>
                                """);
            }
            contentBuilder.append("</div>"); // Fecha activity-section

            // Usa o Template para renderizar a página completa
            String html = Template.render("Dashboard", "home", contentBuilder.toString());

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }
    }

    static class TimesHandler implements HttpHandler {
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

    static class RelatoriosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = new HashMap<>();
                if (query != null) {
                    for (String param : query.split("&")) {
                        String[] entry = param.split("=");
                        if (entry.length > 1) {
                            params.put(entry[0], entry[1]);
                        } else {
                            params.put(entry[0], "");
                        }
                    }
                }

                String relatorioType = params.getOrDefault("type", "");
                if (relatorioType.isEmpty()) {
                    mostrarMenuRelatorios(exchange);
                    return;
                }

                RelatorioDAO dao = new RelatorioDAO();
                List<String[]> dadosRelatorio;
                String titulo = "";
                String[] cabecalhos = {};

                switch (relatorioType) {
                    case "times_estatisticas":
                        titulo = "Relatório: Times com Estatísticas";
                        cabecalhos = new String[] { "Time", "Total de Jogadores", "Total de Gols", "Idade Média", "Técnico" };
                        dadosRelatorio = dao.relatorioTimesComJogadores();
                        break;
                    case "estatisticas_posicao":
                        titulo = "Relatório: Estatísticas por Posição";
                        cabecalhos = new String[] { "Posição", "Total de Jogadores", "Idade Média", "Altura Média",
                                "Total de Gols", "Total de Assistências" };
                        dadosRelatorio = dao.relatorioEstatisticasPorPosicao();
                        break;
                    case "times_estadios":
                        titulo = "Relatório: Times e seus Estádios";
                        cabecalhos = new String[] { "Time", "Estádio", "Capacidade", "Bairro", "Endereço", "Presidente" };
                        dadosRelatorio = dao.relatorioTimesComEstadios();
                        break;
                    case "tecnicos_experiencia":
                        titulo = "Relatório: Técnicos por Experiência";
                        cabecalhos = new String[] { "Técnico", "Idade", "Times Treinados", "Time Atual", "Presidente" };
                        dadosRelatorio = dao.relatorioTecnicosExperiencia();
                        break;
                    default:
                        mostrarMenuRelatorios(exchange);
                        return;
                }

                StringBuilder content = new StringBuilder();
                content.append("<h1>").append(titulo).append("</h1>");
                content.append("<table>");
                content.append("<thead><tr>");
                for (String cabecalho : cabecalhos) {
                    content.append("<th>").append(cabecalho).append("</th>");
                }
                content.append("</tr></thead>");

                content.append("<tbody>");
                if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
                    for (String[] linha : dadosRelatorio) {
                        content.append("<tr>");
                        for (String campo : linha) {
                            content.append("<td>").append(campo).append("</td>");
                        }
                        content.append("</tr>");
                    }
                } else {
                    content.append("<tr><td colspan='").append(cabecalhos.length)
                            .append("'>Nenhum dado encontrado.</td></tr>");
                }
                content.append("</tbody></table>");

                String html = Template.render(titulo, "relatorios", content.toString());
                sendResponse(exchange, html, 200);

            } catch (SQLException e) {
                String error = "Erro no banco de dados: " + e.getMessage();
                e.printStackTrace();
                sendResponse(exchange, Template.render("Erro", "relatorios", "<h1>Erro 500</h1><p>" + error + "</p>"), 500);
            }
        }

        private void mostrarMenuRelatorios(HttpExchange exchange) throws IOException {
            String content = """
                    <h1>📊 Menu de Relatórios</h1>
                    <p>Selecione um dos relatórios abaixo para visualizar os dados consolidados.</p>
                    <div class='menu-relatorios' style='margin-top: 20px;'>
                        <a href='/relatorios?type=times_estatisticas' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times com Estatísticas</a>
                        <a href='/relatorios?type=estatisticas_posicao' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Estatísticas por Posição</a>
                        <a href='/relatorios?type=times_estadios' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times e seus Estádios</a>
                        <a href='/relatorios?type=tecnicos_experiencia' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Técnicos por Experiência</a>
                        <a href='/dashboard' class='btn btn-success' style='display:block; margin-bottom:10px;'>Gráficos de Estatística (Dashboard)</a>
                    </div>
                    """;
            String html = Template.render("Menu de Relatórios", "relatorios", content);
            sendResponse(exchange, html, 200);
        }

        private void sendResponse(HttpExchange exchange, String html, int statusCode) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uriPath = exchange.getRequestURI().getPath();
            String filePath = "." + uriPath;
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                String contentType = "application/octet-stream";
                if (filePath.endsWith(".png")) contentType = "image/png";
                else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (filePath.endsWith(".css")) contentType = "text/css";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                    final byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}
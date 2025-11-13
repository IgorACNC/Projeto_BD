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
import main.FuncoesProcedimentosHandler;
import main.ViewsConsultasHandler;
import main.Template;

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
        server.createContext("/funcoes-procedimentos", new FuncoesProcedimentosHandler());
        server.createContext("/views-consultas", new ViewsConsultasHandler());
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
                String submenu = params.getOrDefault("submenu", "");
                
                // Redirecionar para funções/procedimentos ou views/consultas
                if ("funcoes".equals(submenu)) {
                    FuncoesProcedimentosHandler handler = new FuncoesProcedimentosHandler();
                    handler.handle(exchange);
                    return;
                } else if ("consultas".equals(submenu)) {
                    ViewsConsultasHandler handler = new ViewsConsultasHandler();
                    handler.handle(exchange);
                    return;
                }
                
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
            StringBuilder content = new StringBuilder();
            content.append("<div class=\"header\">\n");
            content.append("    <h1>📊 Menu de Relatórios</h1>\n");
            content.append("    <p>Selecione uma das opções abaixo para visualizar relatórios, consultas e funcionalidades do banco de dados.</p>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Relatórios Básicos</h2>\n");
            content.append("    <div class='menu-relatorios' style='margin-top: 20px;'>\n");
            content.append("        <a href='/relatorios?type=times_estatisticas' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times com Estatísticas</a>\n");
            content.append("        <a href='/relatorios?type=estatisticas_posicao' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Estatísticas por Posição</a>\n");
            content.append("        <a href='/relatorios?type=times_estadios' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times e seus Estádios</a>\n");
            content.append("        <a href='/relatorios?type=tecnicos_experiencia' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Técnicos por Experiência</a>\n");
            content.append("        <a href='/dashboard' class='btn btn-success' style='display:block; margin-bottom:10px;'>Gráficos de Estatística (Dashboard)</a>\n");
            content.append("    </div>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Funções, Procedimentos e Triggers</h2>\n");
            content.append("    <p style=\"color: #64748b; margin-bottom: 15px;\">Execute funções, procedimentos e visualize os efeitos dos triggers do banco de dados.</p>\n");
            content.append("    <a href='/relatorios?submenu=funcoes' class='btn btn-primary' style='display:block; margin-bottom:10px;'>⚙️ Funções, Procedimentos e Triggers</a>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Views e Consultas</h2>\n");
            content.append("    <p style=\"color: #64748b; margin-bottom: 15px;\">Visualize views e execute consultas com filtros e gráficos.</p>\n");
            content.append("    <a href='/relatorios?submenu=consultas' class='btn btn-primary' style='display:block; margin-bottom:10px;'>📊 Views e Consultas</a>\n");
            content.append("</div>\n");
            
            String html = Template.render("Menu de Relatórios", "relatorios", content.toString());
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
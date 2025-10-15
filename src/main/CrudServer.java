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

        int totalTimes = 0;
        int totalJogadores = 0;
        int totalTecnicos = 0;
        int totalEstadios = 0;

        try {
            totalTimes = timeDAO.listar().size();
            totalJogadores = jogadorDAO.listar().size();
            totalTecnicos = tecnicoDAO.listar().size();
            totalEstadios = estadioDAO.listar().size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Brasileirão Série A - Dashboard</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: #333;
                            min-height: 100vh;
                            padding: 20px;
                        }
                        .container {
                            max-width: 1400px;
                            margin: 0 auto;
                        }
                        header {
                            text-align: center;
                            margin-bottom: 50px;
                            padding-top: 30px;
                        }
                        header h1 {
                            font-size: 3em;
                            color: white;
                            margin-bottom: 10px;
                            font-weight: 700;
                            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
                        }
                        header p {
                            font-size: 1.2em;
                            color: rgba(255,255,255,0.9);
                            font-weight: 400;
                        }
                        .stats-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                            gap: 25px;
                            margin-bottom: 40px;
                        }
                        .stat-card {
                            background: white;
                            border-radius: 16px;
                            padding: 30px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
                            transition: transform 0.3s ease, box-shadow 0.3s ease;
                            position: relative;
                            overflow: hidden;
                        }
                        .stat-card::before {
                            content: '';
                            position: absolute;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 4px;
                            background: linear-gradient(90deg, #667eea, #764ba2);
                        }
                        .stat-card:hover {
                            transform: translateY(-8px);
                            box-shadow: 0 15px 40px rgba(0,0,0,0.2);
                        }
                        .stat-icon {
                            width: 60px;
                            height: 60px;
                            border-radius: 12px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 28px;
                            margin-bottom: 20px;
                        }
                        .stat-card.times .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
                        .stat-card.jogadores .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
                        .stat-card.tecnicos .stat-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
                        .stat-card.estadios .stat-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }

                        .stat-label {
                            font-size: 0.95em;
                            color: #7f8c8d;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            font-weight: 600;
                            margin-bottom: 8px;
                        }
                        .stat-value {
                            font-size: 3em;
                            font-weight: 700;
                            color: #2c3e50;
                            line-height: 1;
                        }

                        .actions-section {
                            background: white;
                            border-radius: 16px;
                            padding: 40px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
                        }
                        .actions-section h2 {
                            font-size: 1.8em;
                            color: #2c3e50;
                            margin-bottom: 30px;
                            text-align: center;
                        }
                        .actions-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                            gap: 20px;
                        }
                        .action-card {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 30px;
                            border-radius: 12px;
                            text-decoration: none;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            text-align: center;
                            transition: transform 0.3s ease, box-shadow 0.3s ease;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                        }
                        .action-card:hover {
                            transform: translateY(-5px);
                            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
                        }
                        .action-card.times { background: linear-gradient(135deg, #667eea, #764ba2); }
                        .action-card.jogadores { background: linear-gradient(135deg, #f093fb, #f5576c); }
                        .action-card.relatorios { background: linear-gradient(135deg, #4facfe, #00f2fe); }
                        .action-card.graficos { background: linear-gradient(135deg, #43e97b, #38f9d7); }

                        .action-icon {
                            font-size: 48px;
                            margin-bottom: 15px;
                        }
                        .action-title {
                            font-size: 1.3em;
                            font-weight: 700;
                            margin-bottom: 8px;
                        }
                        .action-description {
                            font-size: 0.9em;
                            opacity: 0.9;
                            line-height: 1.5;
                        }

                        @media (max-width: 768px) {
                            header h1 { font-size: 2em; }
                            .stats-grid { grid-template-columns: 1fr; }
                            .actions-grid { grid-template-columns: 1fr; }
                            .stat-value { font-size: 2.5em; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>Brasileirão Série A</h1>
                            <p>Sistema de Gerenciamento - 2025</p>
                        </header>

                        <div class="stats-grid">
                            <div class="stat-card times">
                                <div class="stat-icon">⚽</div>
                                <div class="stat-label">Times</div>
                                <div class="stat-value">""" + totalTimes + """
</div>
                            </div>

                            <div class="stat-card jogadores">
                                <div class="stat-icon">👤</div>
                                <div class="stat-label">Jogadores</div>
                                <div class="stat-value">""" + totalJogadores + """
</div>
                            </div>

                            <div class="stat-card tecnicos">
                                <div class="stat-icon">📋</div>
                                <div class="stat-label">Técnicos</div>
                                <div class="stat-value">""" + totalTecnicos + """
</div>
                            </div>

                            <div class="stat-card estadios">
                                <div class="stat-icon">🏟️</div>
                                <div class="stat-label">Estádios</div>
                                <div class="stat-value">""" + totalEstadios + """
</div>
                            </div>
                        </div>

                        <div class="actions-section">
                            <h2>Ações Rápidas</h2>
                            <div class="actions-grid">
                                <a href="/times" class="action-card times">
                                    <div class="action-icon">⚽</div>
                                    <div class="action-title">Gerenciar Times</div>
                                    <div class="action-description">Visualizar, adicionar e editar times do campeonato</div>
                                </a>

                                <a href="/jogadores" class="action-card jogadores">
                                    <div class="action-icon">👥</div>
                                    <div class="action-title">Gerenciar Jogadores</div>
                                    <div class="action-description">Controlar elencos e estatísticas dos atletas</div>
                                </a>

                                <a href="/relatorios" class="action-card relatorios">
                                    <div class="action-icon">📊</div>
                                    <div class="action-title">Relatórios</div>
                                    <div class="action-description">Acessar análises e dados detalhados</div>
                                </a>

                                <a href="/dashboard" class="action-card graficos">
                                    <div class="action-icon">📈</div>
                                    <div class="action-title">Dashboard Gráfico</div>
                                    <div class="action-description">Visualizar gráficos e estatísticas visuais</div>
                                </a>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;

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
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/times'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
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

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Gerenciar Times</title>");
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
            html.append("<h1>📋 Gerenciar Times</h1>");
            html.append("<a href='/times/novo' class='btn btn-success'>➕ Novo Time</a>");
            html.append("<a href='/' class='btn btn-primary'>← Voltar</a>");

            html.append("<table>");
            html.append(
                    "<tr><th>ID</th><th>Nome</th><th>Jogadores</th><th>Sócios</th><th>Técnico</th><th>Presidente</th><th>Estádio</th><th>Ações</th></tr>");

            for (Time time : times) {
                String tecnicoNome = getTecnicoNome(tecnicos, time.getFk_tecnico());
                String presidenteNome = getPresidenteNome(presidentes, time.getFk_presidente());
                String estadioNome = getEstadioNome(estadios, time.getFk_estadio());

                html.append("<tr>");
                html.append("<td>").append(time.getId_time()).append("</td>");
                html.append("<td>").append(time.getNome()).append("</td>");
                html.append("<td>").append(time.getQuant_jogadores()).append("</td>");
                html.append("<td>").append(time.getQuant_socios()).append("</td>");
                html.append("<td>").append(tecnicoNome).append("</td>");
                html.append("<td>").append(presidenteNome).append("</td>");
                html.append("<td>").append(estadioNome).append("</td>");
                html.append("<td>");
                html.append("<a href='/times/editar/").append(time.getId_time())
                        .append("' class='btn btn-warning'>Editar</a>");
                html.append("<form method='POST' action='/times/excluir/").append(time.getId_time()).append("' style='display:inline;'>");
                html.append("<button type='submit' class='btn btn-danger' onclick='return confirm(\"Tem certeza que deseja excluir este time?\")'>Excluir</button>");
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

        private void mostrarFormularioNovoTime(HttpExchange exchange) throws IOException, SQLException {
            TecnicoDAO tecnicoDAO = new TecnicoDAO();
            PresidenteDAO presidenteDAO = new PresidenteDAO();
            EstadioDAO estadioDAO = new EstadioDAO();

            List<Tecnico> tecnicos = tecnicoDAO.listar();
            List<Presidente> presidentes = presidenteDAO.listar();
            List<Estadio> estadios = estadioDAO.listar();

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Novo Time</title>");
            html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
            html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
            html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
            html.append(
                    ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
            html.append("<h1>➕ Novo Time</h1>");
            html.append("<form method='POST' action='/times/criar'>");
            html.append("<label>Nome do Time:</label><br>");
            html.append("<input type='text' name='nome' required><br><br>");
            html.append("<label>Quantidade de Jogadores:</label><br>");
            html.append("<input type='number' name='quant_jogadores' value='11' min='1' max='50' required><br><br>");
            html.append("<label>Quantidade de Sócios:</label><br>");
            html.append("<input type='number' name='quant_socios' value='0' min='0' required><br><br>");
            html.append("<label>Técnico:</label><br>");
            html.append("<select name='fk_tecnico' required>");
            html.append("<option value=''>Selecione um técnico</option>");
            for (Tecnico tecnico : tecnicos) {
                html.append("<option value='").append(tecnico.getId_tecnico()).append("'>").append(tecnico.getNome())
                        .append("</option>");
            }
            html.append("</select><br><br>");
            html.append("<label>Presidente:</label><br>");
            html.append("<select name='fk_presidente' required>");
            html.append("<option value=''>Selecione um presidente</option>");
            for (Presidente presidente : presidentes) {
                html.append("<option value='").append(presidente.getId_presidente()).append("'>")
                        .append(presidente.getNome()).append("</option>");
            }
            html.append("</select><br><br>");
            html.append("<label>Estádio:</label><br>");
            html.append("<select name='fk_estadio' required>");
            html.append("<option value=''>Selecione um estádio</option>");
            for (Estadio estadio : estadios) {
                html.append("<option value='").append(estadio.getId_estadio()).append("'>").append(estadio.getNome())
                        .append("</option>");
            }
            html.append("</select><br><br>");
            html.append(
                    "<input type='submit' value='Criar Time' style='background:#27ae60;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
            html.append("<a href='/times' class='btn'>Cancelar</a>");
            html.append("</form></body></html>");

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.toString().getBytes());
            os.close();
        }

        private void mostrarFormularioEditarTime(HttpExchange exchange, int id) throws IOException, SQLException {
            TimeDAO dao = new TimeDAO();
            TecnicoDAO tecnicoDAO = new TecnicoDAO();
            PresidenteDAO presidenteDAO = new PresidenteDAO();
            EstadioDAO estadioDAO = new EstadioDAO();

            Time time = dao.buscarPorId(id);
            if (time == null) {
                String error = "<html><body><h1>Time não encontrado</h1><a href='/times'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(404, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
                return;
            }

            List<Tecnico> tecnicos = tecnicoDAO.listar();
            List<Presidente> presidentes = presidenteDAO.listar();
            List<Estadio> estadios = estadioDAO.listar();

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Editar Time</title>");
            html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
            html.append("form{background:#f8f9fa;padding:20px;border-radius:5px;margin:20px 0;}");
            html.append("input,select{padding:8px;margin:5px;border:1px solid #ddd;border-radius:3px;width:200px;}");
            html.append(
                    ".btn{padding:8px 15px;margin:5px;text-decoration:none;border-radius:3px;color:white;background:#3498db;}</style></head><body>");
            html.append("<h1>✏️ Editar Time</h1>");
            html.append("<form method='POST' action='/times/atualizar/").append(id).append("'>");
            html.append("<label>Nome do Time:</label><br>");
            html.append("<input type='text' name='nome' value='").append(time.getNome()).append("' required><br><br>");
            html.append("<label>Quantidade de Jogadores:</label><br>");
            html.append("<input type='number' name='quant_jogadores' value='").append(time.getQuant_jogadores())
                    .append("' min='1' max='50' required><br><br>");
            html.append("<label>Quantidade de Sócios:</label><br>");
            html.append("<input type='number' name='quant_socios' value='").append(time.getQuant_socios())
                    .append("' min='0' required><br><br>");
            html.append("<label>Técnico:</label><br>");
            html.append("<select name='fk_tecnico' required>");
            html.append("<option value=''>Selecione um técnico</option>");
            for (Tecnico tecnico : tecnicos) {
                String selected = (tecnico.getId_tecnico() == time.getFk_tecnico()) ? "selected" : "";
                html.append("<option value='").append(tecnico.getId_tecnico()).append("' ").append(selected).append(">")
                        .append(tecnico.getNome()).append("</option>");
            }
            html.append("</select><br><br>");
            html.append("<label>Presidente:</label><br>");
            html.append("<select name='fk_presidente' required>");
            html.append("<option value=''>Selecione um presidente</option>");
            for (Presidente presidente : presidentes) {
                String selected = (presidente.getId_presidente() == time.getFk_presidente()) ? "selected" : "";
                html.append("<option value='").append(presidente.getId_presidente()).append("' ").append(selected)
                        .append(">").append(presidente.getNome()).append("</option>");
            }
            html.append("</select><br><br>");
            html.append("<label>Estádio:</label><br>");
            html.append("<select name='fk_estadio' required>");
            html.append("<option value=''>Selecione um estádio</option>");
            for (Estadio estadio : estadios) {
                String selected = (estadio.getId_estadio() == time.getFk_estadio()) ? "selected" : "";
                html.append("<option value='").append(estadio.getId_estadio()).append("' ").append(selected).append(">")
                        .append(estadio.getNome()).append("</option>");
            }
            html.append("</select><br><br>");
            html.append(
                    "<input type='submit' value='Atualizar Time' style='background:#f39c12;color:white;padding:10px 20px;border:none;border-radius:3px;'>");
            html.append("<a href='/times' class='btn'>Cancelar</a>");
            html.append("</form></body></html>");

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.toString().getBytes());
            os.close();
        }

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

            exchange.getResponseHeaders().set("Location", "/times");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
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

            exchange.getResponseHeaders().set("Location", "/times");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }

        private void excluirTime(HttpExchange exchange, int id) throws IOException, SQLException {
            TimeDAO dao = new TimeDAO();
            dao.excluir(id);

            exchange.getResponseHeaders().set("Location", "/times");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        }

        private String getTecnicoNome(List<Tecnico> tecnicos, int id) {
            for (Tecnico tecnico : tecnicos) {
                if (tecnico.getId_tecnico() == id) {
                    return tecnico.getNome();
                }
            }
            return "N/A";
        }

        private String getPresidenteNome(List<Presidente> presidentes, int id) {
            for (Presidente presidente : presidentes) {
                if (presidente.getId_presidente() == id) {
                    return presidente.getNome();
                }
            }
            return "N/A";
        }

        private String getEstadioNome(List<Estadio> estadios, int id) {
            for (Estadio estadio : estadios) {
                if (estadio.getId_estadio() == id) {
                    return estadio.getNome();
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
            RelatorioDAO dao = new RelatorioDAO();
            List<String[]> dadosRelatorio;
            String titulo = "";
            String[] cabecalhos = {}; 

            switch (relatorioType) {
                case "times_estatisticas":
                    titulo = "Relatorio: Times com Estatisticas";
                    cabecalhos = new String[]{"Time", "Total de Jogadores", "Total de Gols", "Idade Media", "Tecnico"};
                    dadosRelatorio = dao.relatorioTimesComJogadores();
                    break;
                case "estatisticas_posicao":
                    titulo = "Relatorio: Estatisticas por Posicao";
                    cabecalhos = new String[]{"Posicao", "Total de Jogadores", "Idade Media", "Altura Media", "Total de Gols", "Total de Assistencias"};
                    dadosRelatorio = dao.relatorioEstatisticasPorPosicao();
                    break;
        
                case "times_estadios":
                    titulo = "Relatorio: Times e seus Estadios";
                    cabecalhos = new String[]{"Time", "Estadio", "Capacidade", "Bairro", "Endereco", "Presidente"};
                    dadosRelatorio = dao.relatorioTimesComEstadios();
                    break;
                
                case "tecnicos_experiencia":
                    titulo = "Relatorio: Tecnicos por Experiencia";
                    cabecalhos = new String[]{"Tecnico", "Idade", "Times Treinados", "Time Atual", "Presidente"};
                    dadosRelatorio = dao.relatorioTecnicosExperiencia();
                    break;
                default:
                    mostrarMenuRelatorios(exchange);
                    return;
            }

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>").append(titulo).append("</title>");
            html.append("<style>body{font-family:Arial,sans-serif;margin:20px; background-color:#f5f5f5;} .container{max-width:1100px; margin:0 auto; background:white; padding:20px; border-radius:8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);} h1{color:#2c3e50;} table{border-collapse:collapse;width:100%;} th,td{border:1px solid #ddd;padding:8px;} th{background-color:#f2f2f2;} .btn{display:inline-block;padding:10px 20px;margin-top:20px;text-decoration:none;border-radius:5px;color:white;} .btn-primary{background:#3498db;}</style>");
            html.append("</head><body><div class='container'>");
            html.append("<h1>").append(titulo).append("</h1>");
            html.append("<table>");
            
            html.append("<thead><tr>");
            for (String cabecalho : cabecalhos) {
                html.append("<th>").append(cabecalho).append("</th>");
            }
            html.append("</tr></thead>");

            html.append("<tbody>");
            if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
                for (String[] linha : dadosRelatorio) {
                    html.append("<tr>");
                    for (String campo : linha) {
                        html.append("<td>").append(campo).append("</td>");
                    }
                    html.append("</tr>");
                }
            } else {
                html.append("<tr><td colspan='").append(cabecalhos.length).append("'>Nenhum dado encontrado.</td></tr>");
            }
            html.append("</tbody></table>");
            
            html.append("<a href='/relatorios' class='btn btn-primary'>Voltar para o Menu</a>");
            
            html.append("</div></body></html>");

            // Envia a resposta HTML
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.toString().getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.toString().getBytes());
            }

        } catch (SQLException e) {
            String error = "Erro no banco de dados: " + e.getMessage();
            e.printStackTrace(); // Ajuda a debugar
            exchange.sendResponseHeaders(500, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }

    private void mostrarMenuRelatorios(HttpExchange exchange) throws IOException {
        String menuHtml = "<html>" +
            "<head>" +
                "<title>Menu de Relatorios</title>" +
                "<style>" +
                    "body{font-family:Arial,sans-serif; margin:20px; background-color:#f5f5f5;}" +
                    "h1{color:#2c3e50; text-align:center;}" +
                    ".container{max-width:800px; margin:0 auto; background:white; padding:20px; border-radius:8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);}" +
                    ".menu a{display:block; text-decoration:none; margin-bottom:10px;}" +
                    ".btn{display:block; padding:15px; background:#3498db; color:white; text-decoration:none; border-radius:5px; text-align:center; transition:background-color 0.3s; font-size: 16px;}" +
                    ".btn:hover{background:#2980b9;}" +
                    ".btn-secondary{background:#7f8c8d;}" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class='container'>" +
                    "<h1>Menu de Relatorios</h1>" +
                    "<div class='menu'>" +
                        "<a href='/relatorios?type=times_estatisticas' class='btn'>Times com Estatisticas</a>" +
                        "<a href='/relatorios?type=estatisticas_posicao' class='btn'>Estatisticas por Posicao</a>" +
                        // --- NOVOS LINKS ADICIONADOS AQUI ---
                        "<a href='/relatorios?type=times_estadios' class='btn'>Times e seus Estadios</a>" +
                        "<a href='/relatorios?type=tecnicos_experiencia' class='btn'>Tecnicos por Experiencia</a>" +
                        
                        "<a href='/' class='btn btn-secondary' style='margin-top:20px;'>Voltar para Home</a>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, menuHtml.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(menuHtml.getBytes());
        }
    }
}

static class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();
        // Mapeia a URL para um arquivo local (ex: /public/images/meu-grafico.png -> .\public\images\meu-grafico.png)
        String filePath = "." + uriPath;
        File file = new File(filePath);

        if (file.exists() && !file.isDirectory()) {
            // Determina o tipo de arquivo para o navegador
            String contentType = "application/octet-stream"; // Tipo padrao
            if (filePath.endsWith(".png")) {
                contentType = "image/png";
            } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filePath.endsWith(".css")) {
                contentType = "text/css";
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (OutputStream os = exchange.getResponseBody();
                 FileInputStream fis = new FileInputStream(file)) {
                final byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } else {
            // Se o arquivo nao for encontrado, envia um erro 404
            String response = "404 (Not Found)\n";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

}

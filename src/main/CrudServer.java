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
        PresidenteDAO presidenteDAO = new PresidenteDAO();

        int totalTimes = 0;
        int totalJogadores = 0;
        int totalTecnicos = 0;
        int totalEstadios = 0;
        int totalPresidentes = 0;
        String artilheiroNome = "N/A";
        int artilheiroGols = 0;
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

        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Dashboard Brasileirão</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                            background: #f5f7fa;
                            display: flex;
                            min-height: 100vh;
                        }

                        .sidebar {
                            width: 250px;
                            background: linear-gradient(180deg, #16a34a 0%, #15803d 100%);
                            color: white;
                            padding: 0;
                            position: fixed;
                            height: 100vh;
                            overflow-y: auto;
                        }

                        .logo-section {
                            padding: 30px 20px;
                            border-bottom: 1px solid rgba(255,255,255,0.1);
                            display: flex;
                            align-items: center;
                            gap: 12px;
                        }

                        .logo-icon {
                            width: 48px;
                            height: 48px;
                            background: #fbbf24;
                            border-radius: 8px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 28px;
                        }

                        .logo-text h2 {
                            font-size: 1.2em;
                            font-weight: 700;
                            margin-bottom: 2px;
                        }

                        .logo-text p {
                            font-size: 0.75em;
                            opacity: 0.9;
                        }

                        .nav-section {
                            padding: 20px 0;
                        }

                        .nav-title {
                            padding: 10px 20px;
                            font-size: 0.7em;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            opacity: 0.7;
                            font-weight: 600;
                        }

                        .nav-item {
                            padding: 12px 20px;
                            display: flex;
                            align-items: center;
                            gap: 12px;
                            color: white;
                            text-decoration: none;
                            transition: background 0.2s;
                            cursor: pointer;
                        }

                        .nav-item:hover {
                            background: rgba(255,255,255,0.1);
                        }

                        .nav-item.active {
                            background: rgba(251, 191, 36, 0.2);
                            border-left: 3px solid #fbbf24;
                        }

                        .nav-icon {
                            font-size: 18px;
                            width: 20px;
                            text-align: center;
                        }

                        .main-content {
                            margin-left: 250px;
                            flex: 1;
                            padding: 40px;
                        }

                        .header {
                            margin-bottom: 40px;
                        }

                        .header h1 {
                            font-size: 2.2em;
                            color: #1e293b;
                            margin-bottom: 8px;
                        }

                        .header h1 span {
                            color: #16a34a;
                        }

                        .header p {
                            color: #64748b;
                            font-size: 1em;
                        }

                        .stats-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                            gap: 20px;
                            margin-bottom: 40px;
                        }

                        .stat-card {
                            background: white;
                            border-radius: 12px;
                            padding: 24px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                            transition: transform 0.2s, box-shadow 0.2s;
                        }

                        .stat-card:hover {
                            transform: translateY(-4px);
                            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                        }

                        .stat-info h3 {
                            font-size: 0.85em;
                            color: #64748b;
                            font-weight: 600;
                            margin-bottom: 8px;
                        }

                        .stat-info .number {
                            font-size: 2.5em;
                            font-weight: 700;
                            color: #1e293b;
                            line-height: 1;
                        }

                        .stat-info .subtitle {
                            font-size: 0.85em;
                            color: #94a3b8;
                            margin-top: 4px;
                        }

                        .stat-icon {
                            width: 64px;
                            height: 64px;
                            border-radius: 12px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 28px;
                        }

                        .stat-card.green .stat-icon { background: linear-gradient(135deg, #16a34a, #22c55e); }
                        .stat-card.blue .stat-icon { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
                        .stat-card.purple .stat-icon { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }
                        .stat-card.orange .stat-icon { background: linear-gradient(135deg, #f97316, #fb923c); }
                        .stat-card.yellow .stat-icon { background: linear-gradient(135deg, #eab308, #facc15); }
                        .stat-card.red .stat-icon { background: linear-gradient(135deg, #ef4444, #f87171); }

                        .activity-section {
                            background: white;
                            border-radius: 12px;
                            padding: 30px;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                        }

                        .activity-section h2 {
                            font-size: 1.4em;
                            color: #1e293b;
                            margin-bottom: 25px;
                            font-weight: 700;
                        }

                        .activity-item {
                            display: flex;
                            align-items: center;
                            gap: 16px;
                            padding: 16px 0;
                            border-bottom: 1px solid #f1f5f9;
                        }

                        .activity-item:last-child {
                            border-bottom: none;
                        }

                        .activity-icon {
                            width: 48px;
                            height: 48px;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 20px;
                            flex-shrink: 0;
                        }

                        .activity-icon.jogador {
                            background: #dbeafe;
                            color: #3b82f6;
                        }

                        .activity-icon.time {
                            background: #dcfce7;
                            color: #16a34a;
                        }

                        .activity-info {
                            flex: 1;
                        }

                        .activity-info h4 {
                            font-size: 1em;
                            color: #1e293b;
                            margin-bottom: 4px;
                            font-weight: 600;
                        }

                        .activity-badge {
                            display: inline-block;
                            padding: 4px 12px;
                            border-radius: 12px;
                            font-size: 0.75em;
                            font-weight: 600;
                        }

                        .activity-badge.jogador {
                            background: #dbeafe;
                            color: #2563eb;
                        }

                        .activity-badge.time {
                            background: #dcfce7;
                            color: #16a34a;
                        }

                        .activity-date {
                            font-size: 0.85em;
                            color: #94a3b8;
                        }

                        @media (max-width: 768px) {
                            .sidebar { display: none; }
                            .main-content { margin-left: 0; padding: 20px; }
                            .stats-grid { grid-template-columns: 1fr; }
                        }
                    </style>
                </head>
                <body>
                    <div class="sidebar">
                        <div class="logo-section">
                            <div class="logo-icon">🏆</div>
                            <div class="logo-text">
                                <h2>Brasileirão</h2>
                                <p>Sistema de Gestão</p>
                            </div>
                        </div>

                        <nav class="nav-section">
                            <div class="nav-title">NAVEGAÇÃO PRINCIPAL</div>
                            <a href="/" class="nav-item active">
                                <span class="nav-icon">🏠</span>
                                <span>Dashboard</span>
                            </a>
                            <a href="/times" class="nav-item">
                                <span class="nav-icon">⚽</span>
                                <span>Times</span>
                            </a>
                            <a href="/jogadores" class="nav-item">
                                <span class="nav-icon">👤</span>
                                <span>Jogadores</span>
                            </a>
                            <a href="/times" class="nav-item">
                                <span class="nav-icon">📋</span>
                                <span>Técnicos</span>
                            </a>
                            <a href="/times" class="nav-item">
                                <span class="nav-icon">🏟️</span>
                                <span>Estádios</span>
                            </a>
                            <a href="/times" class="nav-item">
                                <span class="nav-icon">👑</span>
                                <span>Presidentes</span>
                            </a>
                            <a href="/relatorios" class="nav-item">
                                <span class="nav-icon">📊</span>
                                <span>Relatórios</span>
                            </a>
                        </nav>
                    </div>

                    <div class="main-content">
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

        for (Jogador j : jogadoresRecentes) {
            html += """
                            <div class="activity-item">
                                <div class="activity-icon jogador">👤</div>
                                <div class="activity-info">
                                    <h4>""" + j.getNome() + """
</h4>
                                    <span class="activity-badge jogador">Jogador</span>
                                    <span class="activity-date">26/09/2025 às 12:25</span>
                                </div>
                            </div>
                            """;
        }

        for (Time t : timesRecentes) {
            html += """
                            <div class="activity-item">
                                <div class="activity-icon time">⚽</div>
                                <div class="activity-info">
                                    <h4>""" + t.getNome() + """
</h4>
                                    <span class="activity-badge time">Time</span>
                                    <span class="activity-date">26/09/2025 às 12:25</span>
                                </div>
                            </div>
                            """;
        }

        html += """
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
                        "<a href='/dashboard' class='btn'>Gráficos de estatística</a>" +
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

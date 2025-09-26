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
//import dao.JogadorDAO;
import dao.TecnicoDAO;
import dao.EstadioDAO;
import dao.PresidenteDAO;
import dao.RelatorioDAO;
import model.Time;
//import model.Jogador;
import model.Tecnico;
import model.Estadio;
import model.Presidente;
//import main.JogadoresHandler;

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
        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor CRUD iniciado com sucesso!");
        System.out.println("🌐 Acesse: http://localhost:" + PORT);
        System.out.println("⏹️  Para parar, pressione Ctrl+C");
    }

    // Dentro do arquivo CrudServer.java, substitua o HomeHandler antigo por este

static class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Brasileirão Série A - Dashboard</title>
                    <style>
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                            margin: 0; 
                            background-color: #f4f7f9; 
                            color: #333;
                        }
                        .container { 
                            max-width: 1100px; 
                            margin: 30px auto; 
                            padding: 20px;
                        }
                        header {
                            text-align: center;
                            margin-bottom: 40px;
                        }
                        header h1 {
                            font-size: 2.8em;
                            color: #2c3e50;
                            margin-bottom: 5px;
                        }
                        header p {
                            font-size: 1.2em;
                            color: #7f8c8d;
                        }
                        .menu-principal {
                            text-align: center;
                            margin-bottom: 40px;
                        }
                        .btn {
                            display: inline-block;
                            padding: 12px 28px;
                            margin: 5px;
                            border: none;
                            border-radius: 50px; /* Botoes arredondados */
                            cursor: pointer;
                            text-decoration: none;
                            font-size: 16px;
                            font-weight: 600;
                            transition: transform 0.2s, box-shadow 0.2s;
                        }
                        .btn:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                        }
                        .btn-times { background-color: #3498db; color: white; }
                        .btn-jogadores { background-color: #27ae60; color: white; }
                        .btn-relatorios { background-color: #8e44ad; color: white; }
                        
                        .grafico-card {
                            background: white;
                            padding: 30px;
                            border-radius: 12px;
                            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
                        }
                        .grafico-card h2 {
                            margin-top: 0;
                            color: #2c3e50;
                        }
                        .grafico-card .descricao {
                            color: #555;
                            font-size: 1.1em;
                            line-height: 1.6;
                        }
                        .grafico-imagem {
                            margin-top: 20px;
                            text-align: center;
                        }
                        .grafico-imagem img {
                            max-width: 100%;
                            height: auto;
                            border-radius: 8px;
                            border: 1px solid #eee;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>Futebol Brasileiro 2025</h1>
                            <p>Visualize e analise dados estatísticos do Brasileirão.</p>
                        </header>

                        <nav class="menu-principal">
                            <a href="/times" class="btn btn-times">Gerenciar Times</a>
                            <a href="/jogadores" class="btn btn-jogadores">Gerenciar Jogadores</a>
                            <a href="/relatorios" class="btn btn-relatorios">Ver Relatórios</a>
                        </nav>

                        <main>
                            <section class="grafico-card">
                                <h2>Estatísticas por Posição de Jogador</h2>
                                <p class="descricao">
                                    Este gráfico analisa a distribuição e performance média dos jogadores agrupados por sua posição em campo. Podemos observar a contagem de atletas, a média de gols, assistências e outras métricas chave, fornecendo insights sobre onde se concentram os talentos ofensivos e defensivos do campeonato.
                                </p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/meu-grafico.png" alt="Gráfico de estatísticas por posição">
                                </div>
                            </section>
                        </main>
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
            // Pega o parametro 'type' da URL, ex: /relatorios?type=times_estatisticas
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
            String[] cabecalhos = {}; // Array para guardar os nomes das colunas

            // Decide qual metodo do DAO chamar com base no parametro
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
                // --- NOVO RELATORIO 1 ---
                case "times_estadios":
                    titulo = "Relatorio: Times e seus Estadios";
                    cabecalhos = new String[]{"Time", "Estadio", "Capacidade", "Bairro", "Endereco", "Presidente"};
                    dadosRelatorio = dao.relatorioTimesComEstadios();
                    break;
                // --- NOVO RELATORIO 2 ---
                case "tecnicos_experiencia":
                    titulo = "Relatorio: Tecnicos por Experiencia";
                    cabecalhos = new String[]{"Tecnico", "Idade", "Times Treinados", "Time Atual", "Presidente"};
                    dadosRelatorio = dao.relatorioTecnicosExperiencia();
                    break;
                default:
                    // Se nenhum tipo for especificado, mostre a pagina de menu
                    mostrarMenuRelatorios(exchange);
                    return;
            }

            // Monta o HTML com os dados do relatorio
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

    // Este metodo substitui o antigo para incluir os novos links
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

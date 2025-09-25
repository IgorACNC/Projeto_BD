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

import model.Time;
import model.Jogador;
import model.Tecnico;
import model.Estadio;
import model.Presidente;
import main.JogadoresHandler;

public class CrudServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Iniciando servidor CRUD na porta " + PORT + "...");

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Rotas principais
        server.createContext("/", new HomeHandler());
        server.createContext("/times", new TimesHandler());
        server.createContext("/jogadores", new JogadoresHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor CRUD iniciado com sucesso!");
        System.out.println("🌐 Acesse: http://localhost:" + PORT);
        System.out.println("⏹️  Para parar, pressione Ctrl+C");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html>" +
                    "<html lang='pt-BR'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<title>Brasileirão Série A - CRUD</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }" +
                    ".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }"
                    +
                    "h1 { color: #2c3e50; text-align: center; }" +
                    ".menu { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; margin-top: 30px; }"
                    +
                    ".btn { display: block; padding: 20px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; text-align: center; transition: background-color 0.3s; font-size: 18px; }"
                    +
                    ".btn:hover { background: #2980b9; }" +
                    ".btn.success { background: #27ae60; }" +
                    ".btn.warning { background: #f39c12; }" +
                    ".stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin: 20px 0; }"
                    +
                    ".stat { text-align: center; padding: 15px; background: #ecf0f1; border-radius: 5px; }" +
                    ".stat .number { font-size: 2em; font-weight: bold; color: #3498db; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>🏆 Brasileirão Série A</h1>" +
                    "<p style='text-align: center; color: #7f8c8d;'>Sistema CRUD - Gerenciamento Completo</p>" +
                    "<div class='stats'>" +
                    "<div class='stat'><div class='number'>30</div><div>Times</div></div>" +
                    "<div class='stat'><div class='number'>900+</div><div>Jogadores</div></div>" +
                    "<div class='stat'><div class='number'>30</div><div>Técnicos</div></div>" +
                    "<div class='stat'><div class='number'>30</div><div>Estádios</div></div>" +
                    "</div>" +
                    "<div class='menu'>" +
                    "<a href='/times' class='btn'>📋 Gerenciar Times<br><small>CRUD Completo</small></a>" +
                    "<a href='/jogadores' class='btn success'>⚽ Gerenciar Jogadores<br><small>CRUD Completo</small></a>"
                    +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
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
                html.append("<a href='/times/excluir/").append(time.getId_time())
                        .append("' class='btn btn-danger' onclick='return confirm(\"Tem certeza?\")'>Excluir</a>");
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
}

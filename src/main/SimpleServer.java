package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

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

public class SimpleServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Iniciando servidor web na porta " + PORT + "...");

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Rota principal
        server.createContext("/", new HomeHandler());

        // Rotas da API
        server.createContext("/api/times", new TimesHandler());
        server.createContext("/api/jogadores", new JogadoresHandler());
        server.createContext("/api/tecnicos", new TecnicosHandler());
        server.createContext("/api/estadios", new EstadiosHandler());
        server.createContext("/api/presidentes", new PresidentesHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor iniciado com sucesso!");
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
                    "<title>Brasileirão Série A</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }" +
                    ".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }"
                    +
                    "h1 { color: #2c3e50; text-align: center; }" +
                    ".menu { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 30px; }"
                    +
                    ".btn { display: block; padding: 15px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; text-align: center; transition: background-color 0.3s; }"
                    +
                    ".btn:hover { background: #2980b9; }" +
                    ".stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin: 20px 0; }"
                    +
                    ".stat { text-align: center; padding: 15px; background: #ecf0f1; border-radius: 5px; }" +
                    ".stat .number { font-size: 2em; font-weight: bold; color: #3498db; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>🏆 Brasileirão Série A</h1>" +
                    "<p style='text-align: center; color: #7f8c8d;'>Sistema de Gerenciamento de Dados</p>" +
                    "<div class='stats'>" +
                    "<div class='stat'><div class='number'>30</div><div>Times</div></div>" +
                    "<div class='stat'><div class='number'>900+</div><div>Jogadores</div></div>" +
                    "<div class='stat'><div class='number'>30</div><div>Técnicos</div></div>" +
                    "<div class='stat'><div class='number'>30</div><div>Estádios</div></div>" +
                    "</div>" +
                    "<div class='menu'>" +
                    "<a href='/api/times' class='btn'>📋 Listar Times</a>" +
                    "<a href='/api/jogadores' class='btn'>⚽ Listar Jogadores</a>" +
                    "<a href='/api/tecnicos' class='btn'>👨‍💼 Listar Técnicos</a>" +
                    "<a href='/api/estadios' class='btn'>🏟️ Listar Estádios</a>" +
                    "<a href='/api/presidentes' class='btn'>👔 Listar Presidentes</a>" +
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
            try {
                TimeDAO dao = new TimeDAO();
                List<Time> times = dao.listar();

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Times</title>");
                html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
                html.append(
                        "table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
                html.append("th{background-color:#f2f2f2;}</style></head><body>");
                html.append("<h1>📋 Lista de Times</h1>");
                html.append(
                        "<table><tr><th>ID</th><th>Nome</th><th>Jogadores</th><th>Sócios</th><th>Técnico ID</th><th>Presidente ID</th><th>Estádio ID</th></tr>");

                for (Time time : times) {
                    html.append("<tr>");
                    html.append("<td>").append(time.getId_time()).append("</td>");
                    html.append("<td>").append(time.getNome()).append("</td>");
                    html.append("<td>").append(time.getQuant_jogadores()).append("</td>");
                    html.append("<td>").append(time.getQuant_socios()).append("</td>");
                    html.append("<td>").append(time.getFk_tecnico()).append("</td>");
                    html.append("<td>").append(time.getFk_presidente()).append("</td>");
                    html.append("<td>").append(time.getFk_estadio()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table><br><a href='/'>← Voltar</a></body></html>");

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes());
                os.close();

            } catch (Exception e) {
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class JogadoresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                JogadorDAO dao = new JogadorDAO();
                List<Jogador> jogadores = dao.listar();

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Jogadores</title>");
                html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
                html.append(
                        "table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
                html.append("th{background-color:#f2f2f2;}</style></head><body>");
                html.append("<h1>⚽ Lista de Jogadores</h1>");
                html.append(
                        "<table><tr><th>ID</th><th>Nome</th><th>Posição</th><th>Número</th><th>Gols</th><th>Assistências</th><th>Time ID</th></tr>");

                for (Jogador jogador : jogadores) {
                    html.append("<tr>");
                    html.append("<td>").append(jogador.getId_jogador()).append("</td>");
                    html.append("<td>").append(jogador.getNome()).append("</td>");
                    html.append("<td>").append(jogador.getPosicao_jogador()).append("</td>");
                    html.append("<td>").append(jogador.getNumero_camisa()).append("</td>");
                    html.append("<td>").append(jogador.getGols_temporada_jogador()).append("</td>");
                    html.append("<td>").append(jogador.getAssistencias()).append("</td>");
                    html.append("<td>").append(jogador.getFk_time()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table><br><a href='/'>← Voltar</a></body></html>");

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes());
                os.close();

            } catch (Exception e) {
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class TecnicosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                TecnicoDAO dao = new TecnicoDAO();
                List<Tecnico> tecnicos = dao.listar();

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Técnicos</title>");
                html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
                html.append(
                        "table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
                html.append("th{background-color:#f2f2f2;}</style></head><body>");
                html.append("<h1>👨‍💼 Lista de Técnicos</h1>");
                html.append("<table><tr><th>ID</th><th>Nome</th><th>Idade</th><th>Times Treinados</th></tr>");

                for (Tecnico tecnico : tecnicos) {
                    html.append("<tr>");
                    html.append("<td>").append(tecnico.getId_tecnico()).append("</td>");
                    html.append("<td>").append(tecnico.getNome()).append("</td>");
                    html.append("<td>").append(tecnico.getIdade()).append("</td>");
                    html.append("<td>").append(tecnico.getQuant_time_treinou()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table><br><a href='/'>← Voltar</a></body></html>");

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes());
                os.close();

            } catch (Exception e) {
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class EstadiosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                EstadioDAO dao = new EstadioDAO();
                List<Estadio> estadios = dao.listar();

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Estádios</title>");
                html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
                html.append(
                        "table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
                html.append("th{background-color:#f2f2f2;}</style></head><body>");
                html.append("<h1>🏟️ Lista de Estádios</h1>");
                html.append(
                        "<table><tr><th>ID</th><th>Nome</th><th>Capacidade</th><th>Rua</th><th>Número</th><th>Bairro</th></tr>");

                for (Estadio estadio : estadios) {
                    html.append("<tr>");
                    html.append("<td>").append(estadio.getId_estadio()).append("</td>");
                    html.append("<td>").append(estadio.getNome()).append("</td>");
                    html.append("<td>").append(estadio.getCapacidade()).append("</td>");
                    html.append("<td>").append(estadio.getRua()).append("</td>");
                    html.append("<td>").append(estadio.getNumero()).append("</td>");
                    html.append("<td>").append(estadio.getBairro()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table><br><a href='/'>← Voltar</a></body></html>");

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes());
                os.close();

            } catch (Exception e) {
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class PresidentesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                PresidenteDAO dao = new PresidenteDAO();
                List<Presidente> presidentes = dao.listar();

                StringBuilder html = new StringBuilder();
                html.append("<html><head><title>Presidentes</title>");
                html.append("<style>body{font-family:Arial,sans-serif;margin:20px;}");
                html.append(
                        "table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;padding:8px;text-align:left;}");
                html.append("th{background-color:#f2f2f2;}</style></head><body>");
                html.append("<h1>👔 Lista de Presidentes</h1>");
                html.append(
                        "<table><tr><th>ID</th><th>Nome</th><th>Idade</th><th>Títulos</th><th>Tempo no Cargo</th></tr>");

                for (Presidente presidente : presidentes) {
                    html.append("<tr>");
                    html.append("<td>").append(presidente.getId_presidente()).append("</td>");
                    html.append("<td>").append(presidente.getNome()).append("</td>");
                    html.append("<td>").append(presidente.getIdade()).append("</td>");
                    html.append("<td>").append(presidente.getQuant_titulo()).append("</td>");
                    html.append("<td>").append(presidente.getTempo_cargo()).append("</td>");
                    html.append("</tr>");
                }

                html.append("</table><br><a href='/'>← Voltar</a></body></html>");

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes());
                os.close();

            } catch (Exception e) {
                String error = "<html><body><h1>Erro</h1><p>" + e.getMessage()
                        + "</p><a href='/'>← Voltar</a></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }
}

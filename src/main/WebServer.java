package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
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

import java.util.List;

public class WebServer {
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
        server.createContext("/api/relatorios", new RelatoriosHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor iniciado com sucesso!");
        System.out.println("🌐 Acesse: http://localhost:" + PORT);
        System.out.println("⏹️  Para parar, pressione Ctrl+C");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Brasileirão Série A - Sistema de Gerenciamento</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                            .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                            h1 { color: #2c3e50; text-align: center; }
                            .menu { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 30px; }
                            .btn { display: block; padding: 15px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; text-align: center; transition: background-color 0.3s; }
                            .btn:hover { background: #2980b9; }
                            .btn.success { background: #27ae60; }
                            .btn.warning { background: #f39c12; }
                            .btn.danger { background: #e74c3c; }
                            .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin: 20px 0; }
                            .stat { text-align: center; padding: 15px; background: #ecf0f1; border-radius: 5px; }
                            .stat .number { font-size: 2em; font-weight: bold; color: #3498db; }
                            .api-section { margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 5px; }
                            .api-link { display: inline-block; margin: 5px; padding: 10px 15px; background: #6c757d; color: white; text-decoration: none; border-radius: 3px; font-size: 14px; }
                            .api-link:hover { background: #5a6268; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>🏆 Brasileirão Série A</h1>
                            <p style="text-align: center; color: #7f8c8d;">Sistema de Gerenciamento de Dados</p>

                            <div class="stats">
                                <div class="stat">
                                    <div class="number">30</div>
                                    <div>Times</div>
                                </div>
                                <div class="stat">
                                    <div class="number">900+</div>
                                    <div>Jogadores</div>
                                </div>
                                <div class="stat">
                                    <div class="number">30</div>
                                    <div>Técnicos</div>
                                </div>
                                <div class="stat">
                                    <div class="number">30</div>
                                    <div>Estádios</div>
                                </div>
                            </div>

                            <div class="menu">
                                <a href="/api/times" class="btn">📋 Listar Times</a>
                                <a href="/api/jogadores" class="btn">⚽ Listar Jogadores</a>
                                <a href="/api/tecnicos" class="btn">👨‍💼 Listar Técnicos</a>
                                <a href="/api/estadios" class="btn">🏟️ Listar Estádios</a>
                                <a href="/api/presidentes" class="btn">👔 Listar Presidentes</a>
                                <a href="/api/relatorios" class="btn success">📊 Relatórios</a>
                            </div>

                            <div class="api-section">
                                <h3>🔗 Links da API (JSON)</h3>
                                <a href="/api/times" class="api-link">Times</a>
                                <a href="/api/jogadores" class="api-link">Jogadores</a>
                                <a href="/api/tecnicos" class="api-link">Técnicos</a>
                                <a href="/api/estadios" class="api-link">Estádios</a>
                                <a href="/api/presidentes" class="api-link">Presidentes</a>
                                <a href="/api/relatorios?type=times" class="api-link">Relatório Times</a>
                                <a href="/api/relatorios?type=artilheiros" class="api-link">Artilheiros</a>
                            </div>
                        </div>
                    </body>
                    </html>
                    """;

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
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                for (int i = 0; i < times.size(); i++) {
                    Time time = times.get(i);
                    json.append("    {\\n");
                    json.append("      \\"id\\": ").append(time.getId_time()).append(",\\n");
                    json.append("      \\"nome\\": \\"").append(time.getNome()).append("\\",\\n");
                    json.append("      \\"quant_jogadores\\": ").append(time.getQuant_jogadores()).append(",\\n");
                    json.append("      \\"quant_socios\\": ").append(time.getQuant_socios()).append(",\\n");
                    json.append("      \\"fk_tecnico\\": ").append(time.getFk_tecnico()).append(",\\n");
                    json.append("      \\"fk_presidente\\": ").append(time.getFk_presidente()).append(",\\n");
                    json.append("      \\"fk_estadio\\": ").append(time.getFk_estadio()).append("\\n");
                    json.append("    }");
                    if (i < times.size() - 1) json.append(",");
                    json.append("\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
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
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                for (int i = 0; i < jogadores.size(); i++) {
                    Jogador jogador = jogadores.get(i);
                    json.append("    {\\n");
                    json.append("      \\"id\\": ").append(jogador.getId_jogador()).append(",\\n");
                    json.append("      \\"nome\\": \\"").append(jogador.getNome()).append("\\",\\n");
                    json.append("      \\"posicao\\": \\"").append(jogador.getPosicao_jogador()).append("\\",\\n");
                    json.append("      \\"numero_camisa\\": ").append(jogador.getNumero_camisa()).append(",\\n");
                    json.append("      \\"gols\\": ").append(jogador.getGols_temporada_jogador()).append(",\\n");
                    json.append("      \\"assistencias\\": ").append(jogador.getAssistencias()).append(",\\n");
                    json.append("      \\"time_id\\": ").append(jogador.getFk_time()).append("\\n");
                    json.append("    }");
                    if (i < jogadores.size() - 1) json.append(",");
                    json.append("\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
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
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                for (int i = 0; i < tecnicos.size(); i++) {
                    Tecnico tecnico = tecnicos.get(i);
                    json.append("    {\\n");
                    json.append("      \\"id\\": ").append(tecnico.getId_tecnico()).append(",\\n");
                    json.append("      \\"nome\\": \\"").append(tecnico.getNome()).append("\\",\\n");
                    json.append("      \\"idade\\": ").append(tecnico.getIdade()).append(",\\n");
                    json.append("      \\"quant_time_treinou\\": ").append(tecnico.getQuant_time_treinou()).append("\\n");
                    json.append("    }");
                    if (i < tecnicos.size() - 1) json.append(",");
                    json.append("\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
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
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                for (int i = 0; i < estadios.size(); i++) {
                    Estadio estadio = estadios.get(i);
                    json.append("    {\\n");
                    json.append("      \\"id\\": ").append(estadio.getId_estadio()).append(",\\n");
                    json.append("      \\"nome\\": \\"").append(estadio.getNome()).append("\\",\\n");
                    json.append("      \\"capacidade\\": ").append(estadio.getCapacidade()).append(",\\n");
                    json.append("      \\"rua\\": \\"").append(estadio.getRua()).append("\\",\\n");
                    json.append("      \\"numero\\": ").append(estadio.getNumero()).append(",\\n");
                    json.append("      \\"bairro\\": \\"").append(estadio.getBairro()).append("\\n");
                    json.append("    }");
                    if (i < estadios.size() - 1) json.append(",");
                    json.append("\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
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
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                for (int i = 0; i < presidentes.size(); i++) {
                    Presidente presidente = presidentes.get(i);
                    json.append("    {\\n");
                    json.append("      \\"id\\": ").append(presidente.getId_presidente()).append(",\\n");
                    json.append("      \\"nome\\": \\"").append(presidente.getNome()).append("\\",\\n");
                    json.append("      \\"idade\\": ").append(presidente.getIdade()).append(",\\n");
                    json.append("      \\"quant_titulo\\": ").append(presidente.getQuant_titulo()).append(",\\n");
                    json.append("      \\"tempo_cargo\\": ").append(presidente.getTempo_cargo()).append("\\n");
                    json.append("    }");
                    if (i < presidentes.size() - 1) json.append(",");
                    json.append("\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }

    static class RelatoriosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getQuery();
                RelatorioDAO dao = new RelatorioDAO();
                
                StringBuilder json = new StringBuilder();
                json.append("{\\n");
                json.append("  \\"success\\": true,\\n");
                json.append("  \\"data\\": [\\n");
                
                if (query != null && query.contains("type=times")) {
                    List<String[]> relatorio = dao.relatorioTimesComJogadores();
                    for (int i = 0; i < relatorio.size(); i++) {
                        String[] linha = relatorio.get(i);
                        json.append("    {\\n");
                        json.append("      \\"time\\": \\"").append(linha[0]).append("\\",\\n");
                        json.append("      \\"total_jogadores\\": ").append(linha[1]).append(",\\n");
                        json.append("      \\"total_gols\\": ").append(linha[2]).append(",\\n");
                        json.append("      \\"idade_media\\": ").append(linha[3]).append(",\\n");
                        json.append("      \\"tecnico\\": \\"").append(linha[4]).append("\\n");
                        json.append("    }");
                        if (i < relatorio.size() - 1) json.append(",");
                        json.append("\\n");
                    }
                } else if (query != null && query.contains("type=artilheiros")) {
                    JogadorDAO jogadorDAO = new JogadorDAO();
                    List<String[]> artilheiros = jogadorDAO.buscarArtilheiros();
                    for (int i = 0; i < artilheiros.size(); i++) {
                        String[] linha = artilheiros.get(i);
                        json.append("    {\\n");
                        json.append("      \\"jogador\\": \\"").append(linha[0]).append("\\",\\n");
                        json.append("      \\"gols\\": ").append(linha[1]).append(",\\n");
                        json.append("      \\"posicao\\": \\"").append(linha[2]).append("\\",\\n");
                        json.append("      \\"time\\": \\"").append(linha[3]).append("\\",\\n");
                        json.append("      \\"nacionalidade\\": \\"").append(linha[4]).append("\\n");
                        json.append("    }");
                        if (i < artilheiros.size() - 1) json.append(",");
                        json.append("\\n");
                    }
                } else {
                    json.append("    {\\n");
                    json.append("      \\"message\\": \\"Use ?type=times ou ?type=artilheiros para ver relatórios\\"\\n");
                    json.append("    }\\n");
                }
                
                json.append("  ]\\n");
                json.append("}\\n");
                
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(json.toString().getBytes());
                os.close();
                
            } catch (Exception e) {
                String error = "{\\"success\\": false, \\"error\\": \\"" + e.getMessage() + "\\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }
}

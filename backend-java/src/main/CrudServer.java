package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
//import java.net.URLDecoder;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

//import dao.TimeDAO;
//import dao.JogadorDAO;
//import dao.TecnicoDAO;
//import dao.EstadioDAO;
//import dao.PresidenteDAO;
//import dao.RelatorioDAO;
//import model.Time;
//import model.Jogador;
//import model.Tecnico;
//import model.Estadio;
//import model.Presidente;
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
                            display: flex; align-items: center; justify-content: center; height: 100vh;
                        }
                        .container { max-width: 1100px; text-align: center; }
                        header h1 { font-size: 3.5em; color: #2c3e50; margin-bottom: 10px; }
                        header p { font-size: 1.3em; color: #7f8c8d; margin-top: 0; margin-bottom: 40px; }
                        .menu-principal a {
                            display: inline-block; padding: 15px 35px; margin: 10px;
                            border: none; border-radius: 50px; cursor: pointer;
                            text-decoration: none; font-size: 18px; font-weight: 600;
                            transition: transform 0.2s, box-shadow 0.2s;
                        }
                        .btn:hover { transform: translateY(-3px); box-shadow: 0 6px 15px rgba(0,0,0,0.12); }
                        .btn-times { background-color: #3498db; color: white; }
                        .btn-jogadores { background-color: #27ae60; color: white; }
                        .btn-relatorios { background-color: #8e44ad; color: white; }
                        .btn-dashboard { background-color: #f39c12; color: white; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>Sistema de Gerenciamento</h1>
                            <p>Brasileirão Série A</p>
                        </header>

                        <nav class="menu-principal">
                            <a href="/times" class="btn btn-times">Gerenciar Times</a>
                            <a href="/jogadores" class="btn btn-jogadores">Gerenciar Jogadores</a>
                            <a href="/relatorios" class="btn btn-relatorios">Ver Relatórios</a>
                            <a href="/dashboard" class="btn btn-dashboard">Ver Dashboard de Gráficos</a>
                        </nav>
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

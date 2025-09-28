// Em: backend-java/src/main/JogadoresHandler.java
package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.JogadorDAO;
import model.Jogador;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class JogadoresHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        Gson gson = new Gson();

        try {
            JogadorDAO dao = new JogadorDAO();
            
            if ("/jogadores".equals(path) && "GET".equals(method)) {
                List<Jogador> jogadores = dao.listar();
                enviarRespostaJson(exchange, gson.toJson(jogadores), 200);
            } else if ("/jogadores".equals(path) && "POST".equals(method)) {
                Jogador jogador = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Jogador.class);
                dao.inserir(jogador);
                enviarRespostaJson(exchange, "{\"message\": \"Jogador criado com sucesso!\"}", 201);
            } else if (path.startsWith("/jogadores/atualizar/") && "PUT".equals(method)) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                Jogador jogador = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Jogador.class);
                jogador.setId_jogador(id);
                dao.atualizar(jogador);
                enviarRespostaJson(exchange, "{\"message\": \"Jogador atualizado com sucesso!\"}", 200);
            } else if (path.startsWith("/jogadores/excluir/") && "DELETE".equals(method)) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                dao.excluir(id);
                enviarRespostaJson(exchange, "{\"message\": \"Jogador excluido com sucesso!\"}", 200);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (SQLException e) {
            enviarRespostaErro(exchange, "Erro no banco de dados: " + e.getMessage());
        } catch (NumberFormatException e) {
            enviarRespostaErro(exchange, "ID invalido na URL.");
        }
    }
    
    private void enviarRespostaJson(HttpExchange exchange, String jsonResponse, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes("UTF-8").length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes("UTF-8"));
        }
    }
    
    private void enviarRespostaErro(HttpExchange exchange, String mensagem) throws IOException {
        String error = "{\"error\": \"" + mensagem + "\"}";
        enviarRespostaJson(exchange, error, 500);
    }
}
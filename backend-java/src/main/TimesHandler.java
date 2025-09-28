// Em: backend-java/src/main/TimesHandler.java
package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.TimeDAO;
import model.Time;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class TimesHandler implements HttpHandler {

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
            TimeDAO dao = new TimeDAO();

            if ("/times".equals(path) && "GET".equals(method)) {
                List<Time> times = dao.listar();
                enviarRespostaJson(exchange, gson.toJson(times), 200);
            } else if ("/times".equals(path) && "POST".equals(method)) {
                Time time = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Time.class);
                dao.inserir(time);
                enviarRespostaJson(exchange, "{\"message\": \"Time criado com sucesso!\"}", 201);
            } else if (path.startsWith("/times/atualizar/") && "PUT".equals(method)) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                Time time = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Time.class);
                time.setId_time(id);
                dao.atualizar(time);
                enviarRespostaJson(exchange, "{\"message\": \"Time atualizado com sucesso!\"}", 200);
            } else if (path.startsWith("/times/excluir/") && "DELETE".equals(method)) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                dao.excluir(id);
                enviarRespostaJson(exchange, "{\"message\": \"Time excluido com sucesso!\"}", 200);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            enviarRespostaErro(exchange, "Erro interno no servidor: " + e.getMessage());
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
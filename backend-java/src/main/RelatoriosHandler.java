// Em: backend-java/src/main/RelatoriosHandler.java
package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.RelatorioDAO;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class RelatoriosHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Habilita o CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        String query = exchange.getRequestURI().getQuery();
        String relatorioType = getQueryParam(query, "type");
        
        try {
            RelatorioDAO dao = new RelatorioDAO();
            List<String[]> dadosRelatorio;

            switch (relatorioType) {
                case "times_estatisticas": dadosRelatorio = dao.relatorioTimesComJogadores(); break;
                case "estatisticas_posicao": dadosRelatorio = dao.relatorioEstatisticasPorPosicao(); break;
                case "times_estadios": dadosRelatorio = dao.relatorioTimesComEstadios(); break;
                case "tecnicos_experiencia": dadosRelatorio = dao.relatorioTecnicosExperiencia(); break;
                case "jogadores_nacionalidade": dadosRelatorio = dao.relatorioJogadoresPorNacionalidade(); break;
                default:
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                    return;
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(dadosRelatorio);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }

        } catch (SQLException e) {
             String error = "{\"error\": \"Erro no banco de dados: " + e.getMessage() + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(500, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }

    private String getQueryParam(String query, String paramName) {
        if (query == null) return "";
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1 && entry[0].equals(paramName)) {
                return entry[1];
            }
        }
        return "";
    }
}
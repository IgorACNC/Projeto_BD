package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.*;
//import model.*;
import com.google.gson.Gson;

public class RelatoriosHandler implements HttpHandler {
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

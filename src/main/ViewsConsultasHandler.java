package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import dao.ViewConsultaDAO;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewsConsultasHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        
        Map<String, String> params = parseQuery(query);
        String view = params.getOrDefault("view", "");
        String consulta = params.getOrDefault("consulta", "");
        
        try {
            if ("POST".equals(method)) {
                // Processar filtros
                String body = readRequestBody(exchange);
                Map<String, String> formParams = parseFormData(body);
                String action = formParams.get("action");
                
                if ("filtrar-jogador-detalhado".equals(action)) {
                    mostrarJogadorDetalhado(exchange, formParams.get("filtro_nome"), formParams.get("filtro_time"));
                } else if ("filtrar-infraestrutura".equals(action)) {
                    mostrarInfraestruturaTime(exchange, formParams.get("filtro_time"));
                } else if ("filtrar-estadios-grandes".equals(action)) {
                    mostrarJogadoresEstadiosGrandes(exchange, formParams.get("capacidade_minima"));
                } else {
                    mostrarMenu(exchange);
                }
            } else {
                // GET requests
                if (view.equals("jogador-detalhado")) {
                    mostrarJogadorDetalhado(exchange, null, null);
                } else if (view.equals("infraestrutura")) {
                    mostrarInfraestruturaTime(exchange, null);
                } else if (consulta.equals("tecnicos-desempregados")) {
                    mostrarTecnicosDesempregados(exchange);
                } else if (consulta.equals("jogadores-estadios-grandes")) {
                    mostrarJogadoresEstadiosGrandes(exchange, null);
                } else if (consulta.equals("times-artilheiros")) {
                    mostrarTimesComArtilheiros(exchange);
                } else if (consulta.equals("estatisticas-posicao")) {
                    mostrarEstatisticasPorPosicao(exchange);
                } else if (consulta.equals("times-estatisticas")) {
                    mostrarTimesComEstatisticas(exchange);
                } else {
                    mostrarMenu(exchange);
                }
            }
        } catch (SQLException e) {
            String error = "Erro no banco de dados: " + e.getMessage();
            e.printStackTrace();
            sendResponse(exchange, Template.render("Erro", "relatorios", 
                "<h1>Erro</h1><p>" + error + "</p>"), 500);
        }
    }
    
    private void mostrarMenu(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>📊 Views e Consultas</h1>
                <p>Visualize views e execute consultas com filtros e gráficos</p>
            </div>
            
            <div style="margin: 30px 0;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Views Disponíveis</h2>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px;">
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">vw_JogadorDetalhado</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Visualize informações detalhadas dos jogadores com seus times, técnicos e presidentes</p>
                        <a href="/relatorios?submenu=consultas&view=jogador-detalhado" class="btn btn-primary">Ver View</a>
                    </div>
                    
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">vw_InfraestruturaTime</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Visualize a infraestrutura completa de cada time</p>
                        <a href="/relatorios?submenu=consultas&view=infraestrutura" class="btn btn-primary">Ver View</a>
                    </div>
                </div>
            </div>
            
            <div style="margin: 30px 0;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Consultas Disponíveis</h2>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px;">
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">Técnicos Desempregados</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Lista técnicos sem time atual</p>
                        <a href="/relatorios?submenu=consultas&consulta=tecnicos-desempregados" class="btn btn-primary">Ver Consulta</a>
                    </div>
                    
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">Jogadores em Estádios Grandes</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Jogadores de times com estádios de grande capacidade</p>
                        <a href="/relatorios?submenu=consultas&consulta=jogadores-estadios-grandes" class="btn btn-primary">Ver Consulta</a>
                    </div>
                    
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">Times com Artilheiros</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Lista todos os times e seus artilheiros</p>
                        <a href="/relatorios?submenu=consultas&consulta=times-artilheiros" class="btn btn-primary">Ver Consulta</a>
                    </div>
                    
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">Estatísticas por Posição</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Estatísticas detalhadas com gráficos</p>
                        <a href="/relatorios?submenu=consultas&consulta=estatisticas-posicao" class="btn btn-primary">Ver Consulta</a>
                    </div>
                    
                    <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h3 style="color: #1e293b; margin-bottom: 10px;">Times com Estatísticas</h3>
                        <p style="color: #64748b; margin-bottom: 15px;">Estatísticas completas dos times com gráficos</p>
                        <a href="/relatorios?submenu=consultas&consulta=times-estatisticas" class="btn btn-primary">Ver Consulta</a>
                    </div>
                </div>
            </div>
        """);
        
        String html = Template.render("Views e Consultas", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarJogadorDetalhado(HttpExchange exchange, String filtroNome, String filtroTime) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.buscarJogadorDetalhado(filtroNome, filtroTime);
        
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"header\">\n");
        content.append("    <h1>👤 View: Jogador Detalhado</h1>\n");
        content.append("    <p>Informações completas dos jogadores</p>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;\">\n");
        content.append("    <h2 style=\"color: #1e293b; margin-bottom: 15px;\">Filtros</h2>\n");
        content.append("    <form method=\"POST\" action=\"/relatorios?submenu=consultas\">\n");
        content.append("        <input type=\"hidden\" name=\"action\" value=\"filtrar-jogador-detalhado\">\n");
        content.append("        <div style=\"display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 15px;\">\n");
        content.append("            <div>\n");
        content.append("                <label>Nome do Jogador:</label>\n");
        content.append("                <input type=\"text\" name=\"filtro_nome\" value=\"");
        content.append(filtroNome != null ? filtroNome : "");
        content.append("\">\n");
        content.append("            </div>\n");
        content.append("            <div>\n");
        content.append("                <label>Nome do Time:</label>\n");
        content.append("                <input type=\"text\" name=\"filtro_time\" value=\"");
        content.append(filtroTime != null ? filtroTime : "");
        content.append("\">\n");
        content.append("            </div>\n");
        content.append("        </div>\n");
        content.append("        <input type=\"submit\" value=\"Filtrar\" class=\"btn btn-primary\">\n");
        content.append("        <a href=\"/relatorios?submenu=consultas&view=jogador-detalhado\" class=\"btn btn-warning\">Limpar Filtros</a>\n");
        content.append("    </form>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
        content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Dados da View</h2>\n");
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum resultado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Jogador</th>
                            <th>Posição</th>
                            <th>Idade</th>
                            <th>Nacionalidade</th>
                            <th>Gols</th>
                            <th>Assistências</th>
                            <th>Time</th>
                            <th>Técnico</th>
                            <th>Presidente</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Jogador Detalhado", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarInfraestruturaTime(HttpExchange exchange, String filtroTime) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.buscarInfraestruturaTime(filtroTime);
        
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"header\">\n");
        content.append("    <h1>🏟️ View: Infraestrutura do Time</h1>\n");
        content.append("    <p>Informações completas sobre a infraestrutura dos times</p>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;\">\n");
        content.append("    <h2 style=\"color: #1e293b; margin-bottom: 15px;\">Filtros</h2>\n");
        content.append("    <form method=\"POST\" action=\"/relatorios?submenu=consultas\">\n");
        content.append("        <input type=\"hidden\" name=\"action\" value=\"filtrar-infraestrutura\">\n");
        content.append("        <div style=\"margin-bottom: 15px;\">\n");
        content.append("            <label>Nome do Time:</label>\n");
        content.append("            <input type=\"text\" name=\"filtro_time\" value=\"");
        content.append(filtroTime != null ? filtroTime : "");
        content.append("\">\n");
        content.append("        </div>\n");
        content.append("        <input type=\"submit\" value=\"Filtrar\" class=\"btn btn-primary\">\n");
        content.append("        <a href=\"/relatorios?submenu=consultas&view=infraestrutura\" class=\"btn btn-warning\">Limpar Filtros</a>\n");
        content.append("    </form>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
        content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Dados da View</h2>\n");
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum resultado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Jogadores</th>
                            <th>Sócios</th>
                            <th>Presidente</th>
                            <th>Meses no Cargo</th>
                            <th>Técnico</th>
                            <th>Times Treinados</th>
                            <th>Estádio</th>
                            <th>Capacidade</th>
                            <th>Bairro</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Infraestrutura", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarTecnicosDesempregados(HttpExchange exchange) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.consultarTecnicosDesempregados();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>👔 Técnicos Desempregados</h1>
                <p>Técnicos sem time atual</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
        """);
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum técnico desempregado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Técnico</th>
                            <th>Idade</th>
                            <th>Times Treinados</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Técnicos Desempregados", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarJogadoresEstadiosGrandes(HttpExchange exchange, String capacidadeMinima) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        int capacidade = capacidadeMinima != null && !capacidadeMinima.isEmpty() ? Integer.parseInt(capacidadeMinima) : 60000;
        List<String[]> dados = dao.consultarJogadoresEstadiosGrandes(capacidade);
        
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"header\">\n");
        content.append("    <h1>🏟️ Jogadores em Estádios Grandes</h1>\n");
        content.append("    <p>Jogadores de times com estádios de grande capacidade</p>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;\">\n");
        content.append("    <h2 style=\"color: #1e293b; margin-bottom: 15px;\">Filtros</h2>\n");
        content.append("    <form method=\"POST\" action=\"/relatorios?submenu=consultas\">\n");
        content.append("        <input type=\"hidden\" name=\"action\" value=\"filtrar-estadios-grandes\">\n");
        content.append("        <div style=\"margin-bottom: 15px;\">\n");
        content.append("            <label>Capacidade Mínima:</label>\n");
        content.append("            <input type=\"number\" name=\"capacidade_minima\" value=\"");
        content.append(capacidade);
        content.append("\">\n");
        content.append("        </div>\n");
        content.append("        <input type=\"submit\" value=\"Filtrar\" class=\"btn btn-primary\">\n");
        content.append("    </form>\n");
        content.append("</div>\n");
        content.append("<div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum resultado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Jogador</th>
                            <th>Posição</th>
                            <th>Time</th>
                            <th>Capacidade do Estádio</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Jogadores em Estádios Grandes", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarTimesComArtilheiros(HttpExchange exchange) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.consultarTimesComArtilheiros();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>⚽ Times com Artilheiros</h1>
                <p>Lista todos os times e seus artilheiros</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
        """);
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum resultado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Sócios</th>
                            <th>Artilheiro</th>
                            <th>Gols do Artilheiro</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Times com Artilheiros", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarEstatisticasPorPosicao(HttpExchange exchange) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.consultarEstatisticasPorPosicao();
        
        // Preparar dados para gráfico
        StringBuilder chartLabels = new StringBuilder();
        StringBuilder chartGols = new StringBuilder();
        StringBuilder chartAssistencias = new StringBuilder();
        
        for (String[] linha : dados) {
            if (chartLabels.length() > 0) {
                chartLabels.append(",");
                chartGols.append(",");
                chartAssistencias.append(",");
            }
            chartLabels.append("'").append(linha[0]).append("'");
            chartGols.append(linha[4]);
            chartAssistencias.append(linha[5]);
        }
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>📊 Estatísticas por Posição</h1>
                <p>Estatísticas detalhadas com visualizações gráficas</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Gráfico: Gols por Posição</h2>
                <canvas id="chartGols" style="max-height: 400px;"></canvas>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Gráfico: Assistências por Posição</h2>
                <canvas id="chartAssistencias" style="max-height: 400px;"></canvas>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Dados Detalhados</h2>
        """);
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum dado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Posição</th>
                            <th>Total Jogadores</th>
                            <th>Idade Média</th>
                            <th>Altura Média</th>
                            <th>Total Gols</th>
                            <th>Total Assistências</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
            
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            <script>
                const ctxGols = document.getElementById('chartGols').getContext('2d');
                new Chart(ctxGols, {
                    type: 'bar',
                    data: {
                        labels: [""");
        content.append(chartLabels.toString());
        content.append("],\n                        datasets: [{\n                            label: 'Total de Gols',\n                            data: [");
        content.append(chartGols.toString());
        content.append("],\n                            backgroundColor: 'rgba(59, 130, 246, 0.6)',\n                            borderColor: 'rgba(59, 130, 246, 1)',\n                            borderWidth: 1\n                        }]\n                    },\n                    options: {\n                        responsive: true,\n                        maintainAspectRatio: true,\n                        scales: {\n                            y: { beginAtZero: true }\n                        }\n                    }\n                });\n                \n                const ctxAssistencias = document.getElementById('chartAssistencias').getContext('2d');\n                new Chart(ctxAssistencias, {\n                    type: 'bar',\n                    data: {\n                        labels: [");
        content.append(chartLabels.toString());
        content.append("],\n                        datasets: [{\n                            label: 'Total de Assistências',\n                            data: [");
        content.append(chartAssistencias.toString());
        content.append("],\n                            backgroundColor: 'rgba(22, 163, 74, 0.6)',\n                            borderColor: 'rgba(22, 163, 74, 1)',\n                            borderWidth: 1\n                        }]\n                    },\n                    options: {\n                        responsive: true,\n                        maintainAspectRatio: true,\n                        scales: {\n                            y: { beginAtZero: true }\n                        }\n                    }\n                });\n            </script>");
        
        String html = Template.render("Estatísticas por Posição", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarTimesComEstatisticas(HttpExchange exchange) throws IOException, SQLException {
        ViewConsultaDAO dao = new ViewConsultaDAO();
        List<String[]> dados = dao.consultarTimesComEstatisticas();
        
        // Preparar dados para gráfico
        StringBuilder chartLabels = new StringBuilder();
        StringBuilder chartGols = new StringBuilder();
        StringBuilder chartJogadores = new StringBuilder();
        
        for (String[] linha : dados) {
            if (chartLabels.length() > 0) {
                chartLabels.append(",");
                chartGols.append(",");
                chartJogadores.append(",");
            }
            chartLabels.append("'").append(linha[0]).append("'");
            chartGols.append(linha[2]);
            chartJogadores.append(linha[1]);
        }
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>📊 Times com Estatísticas</h1>
                <p>Estatísticas completas dos times com visualizações gráficas</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Gráfico: Gols por Time</h2>
                <canvas id="chartGols" style="max-height: 400px;"></canvas>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Gráfico: Jogadores por Time</h2>
                <canvas id="chartJogadores" style="max-height: 400px;"></canvas>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Dados Detalhados</h2>
        """);
        
        if (dados.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum dado encontrado.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Total Jogadores</th>
                            <th>Total Gols</th>
                            <th>Idade Média</th>
                            <th>Técnico</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] linha : dados) {
                content.append("<tr>");
                for (String campo : linha) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=consultas" class="btn btn-primary">← Voltar</a>
            </div>
            
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            <script>
                const ctxGols = document.getElementById('chartGols').getContext('2d');
                new Chart(ctxGols, {
                    type: 'bar',
                    data: {
                        labels: [""");
        content.append(chartLabels.toString());
        content.append("],\n                        datasets: [{\n                            label: 'Total de Gols',\n                            data: [");
        content.append(chartGols.toString());
        content.append("],\n                            backgroundColor: 'rgba(59, 130, 246, 0.6)',\n                            borderColor: 'rgba(59, 130, 246, 1)',\n                            borderWidth: 1\n                        }]\n                    },\n                    options: {\n                        responsive: true,\n                        maintainAspectRatio: true,\n                        scales: {\n                            y: { beginAtZero: true }\n                        }\n                    }\n                });\n                \n                const ctxJogadores = document.getElementById('chartJogadores').getContext('2d');\n                new Chart(ctxJogadores, {\n                    type: 'bar',\n                    data: {\n                        labels: [");
        content.append(chartLabels.toString());
        content.append("],\n                        datasets: [{\n                            label: 'Total de Jogadores',\n                            data: [");
        content.append(chartJogadores.toString());
        content.append("],\n                            backgroundColor: 'rgba(22, 163, 74, 0.6)',\n                            borderColor: 'rgba(22, 163, 74, 1)',\n                            borderWidth: 1\n                        }]\n                    },\n                    options: {\n                        responsive: true,\n                        maintainAspectRatio: true,\n                        scales: {\n                            y: { beginAtZero: true }\n                        }\n                    }\n                });\n            </script>");
        
        String html = Template.render("Times com Estatísticas", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    try {
                        params.put(entry[0], URLDecoder.decode(entry[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        params.put(entry[0], entry[1]);
                    }
                } else {
                    params.put(entry[0], "");
                }
            }
        }
        return params;
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    private Map<String, String> parseFormData(String body) {
        Map<String, String> params = new HashMap<>();
        if (body != null) {
            for (String param : body.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    try {
                        params.put(entry[0], URLDecoder.decode(entry[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        params.put(entry[0], entry[1]);
                    }
                } else {
                    params.put(entry[0], "");
                }
            }
        }
        return params;
    }
    
    private void sendResponse(HttpExchange exchange, String html, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, html.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes());
        }
    }
}


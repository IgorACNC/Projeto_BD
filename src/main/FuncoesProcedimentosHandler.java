package main;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import dao.FuncaoProcedimentoDAO;
import dao.TimeDAO;
import dao.TecnicoDAO;
import model.Time;
import model.Tecnico;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncoesProcedimentosHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        
        Map<String, String> params = parseQuery(query);
        String action = params.getOrDefault("action", "");
        
        try {
            if ("POST".equals(method)) {
                // Processar formulários
                String body = readRequestBody(exchange);
                Map<String, String> formParams = parseFormData(body);
                
                String actionParam = formParams.get("action");
                if ("executar-funcao".equals(actionParam) || formParams.containsKey("funcao")) {
                    executarFuncao(exchange, formParams);
                } else if ("executar-procedimento".equals(actionParam) || formParams.containsKey("procedimento")) {
                    executarProcedimento(exchange, formParams);
                } else {
                    mostrarMenu(exchange);
                }
            } else {
                // GET requests
                if (action.equals("funcoes")) {
                    mostrarFuncoes(exchange);
                } else if (action.equals("procedimentos")) {
                    mostrarProcedimentos(exchange);
                } else if (action.equals("triggers")) {
                    mostrarTriggers(exchange);
                } else if (action.equals("logs")) {
                    mostrarLogs(exchange);
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
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>⚙️ Funções, Procedimentos e Triggers</h1>
                <p>Execute funções, procedimentos e visualize os efeitos dos triggers</p>
            </div>
            
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin: 30px 0;">
                <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <h2 style="color: #1e293b; margin-bottom: 15px;">🔧 Funções</h2>
                    <p style="color: #64748b; margin-bottom: 20px;">Execute funções do banco de dados</p>
                    <a href="/relatorios?submenu=funcoes&action=funcoes" class="btn btn-primary">Ver Funções</a>
                </div>
                
                <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <h2 style="color: #1e293b; margin-bottom: 15px;">📋 Procedimentos</h2>
                    <p style="color: #64748b; margin-bottom: 20px;">Execute procedimentos armazenados</p>
                    <a href="/relatorios?submenu=funcoes&action=procedimentos" class="btn btn-primary">Ver Procedimentos</a>
                </div>
                
                <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <h2 style="color: #1e293b; margin-bottom: 15px;">⚡ Triggers</h2>
                    <p style="color: #64748b; margin-bottom: 20px;">Visualize triggers e seus efeitos</p>
                    <a href="/relatorios?submenu=funcoes&action=triggers" class="btn btn-primary">Ver Triggers</a>
                </div>
                
                <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <h2 style="color: #1e293b; margin-bottom: 15px;">📝 Logs de Triggers</h2>
                    <p style="color: #64748b; margin-bottom: 20px;">Visualize os logs gerados pelos triggers</p>
                    <a href="/relatorios?submenu=funcoes&action=logs" class="btn btn-primary">Ver Logs</a>
                </div>
            </div>
        """);
        
        String html = Template.render("Funções e Procedimentos", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarFuncoes(HttpExchange exchange) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        List<String[]> funcoes = dao.listarFuncoes();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>🔧 Funções do Banco de Dados</h1>
                <p>Execute funções e visualize seus resultados</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 30px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Função: fn_ClassificarDesempenho</h2>
                <p style="color: #64748b; margin-bottom: 15px;">Classifica o desempenho de um jogador com base nos gols na temporada</p>
                <form method="POST" action="/relatorios?submenu=funcoes" style="display: flex; gap: 10px; align-items: end;">
                    <div style="flex: 1;">
                        <label>Número de Gols:</label>
                        <input type="number" name="gols" value="10" min="0" required>
                    </div>
                    <input type="hidden" name="funcao" value="classificar">
                    <input type="submit" value="Executar" class="btn btn-success">
                </form>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 30px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Função: fn_CalcularParticipacoesGols</h2>
                <p style="color: #64748b; margin-bottom: 15px;">Calcula o total de participações em gols (gols + assistências)</p>
                <form method="POST" action="/relatorios?submenu=funcoes" style="display: flex; gap: 10px; align-items: end;">
                    <div style="flex: 1;">
                        <label>Gols:</label>
                        <input type="number" name="gols" value="5" min="0" required>
                    </div>
                    <div style="flex: 1;">
                        <label>Assistências:</label>
                        <input type="number" name="assistencias" value="3" min="0" required>
                    </div>
                    <input type="hidden" name="funcao" value="participacoes">
                    <input type="submit" value="Executar" class="btn btn-success">
                </form>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Funções Cadastradas</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Tipo</th>
                            <th>Tipo de Retorno</th>
                        </tr>
                    </thead>
                    <tbody>
        """);
        
        for (String[] funcao : funcoes) {
            content.append("<tr>");
            content.append("<td>").append(funcao[0]).append("</td>");
            content.append("<td>").append(funcao[1]).append("</td>");
            content.append("<td>").append(funcao[2]).append("</td>");
            content.append("</tr>");
        }
        
        content.append("""
                    </tbody>
                </table>
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Funções", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarProcedimentos(HttpExchange exchange) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        TimeDAO timeDAO = new TimeDAO();
        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        List<String[]> procedimentos = dao.listarProcedimentos();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>📋 Procedimentos Armazenados</h1>
                <p>Execute procedimentos e visualize seus efeitos</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 30px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Procedimento: sp_ContratarTecnico</h2>
                <p style="color: #64748b; margin-bottom: 15px;">Atualiza o técnico de um time e incrementa o contador de times treinados</p>
                <form method="POST" action="/relatorios?submenu=funcoes">
        """);
        
        // Adicionar selects para times e técnicos
        try {
            content.append("<div style='margin-bottom: 15px;'><label>Time:</label><select name='time_id' required>");
            List<Time> times = timeDAO.listar();
            for (Time time : times) {
                content.append("<option value='").append(time.getId_time()).append("'>")
                       .append(time.getNome()).append("</option>");
            }
            content.append("</select></div>");
            
            content.append("<div style='margin-bottom: 15px;'><label>Novo Técnico:</label><select name='tecnico_id' required>");
            List<Tecnico> tecnicos = tecnicoDAO.listar();
            for (Tecnico tecnico : tecnicos) {
                content.append("<option value='").append(tecnico.getId_tecnico()).append("'>")
                       .append(tecnico.getNome()).append("</option>");
            }
            content.append("</select></div>");
        } catch (SQLException e) {
            content.append("<p>Erro ao carregar dados: ").append(e.getMessage()).append("</p>");
        }
        
        content.append("""
                    <input type="hidden" name="procedimento" value="contratar">
                    <input type="submit" value="Executar Procedimento" class="btn btn-success">
                </form>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 30px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Procedimento: sp_GerarListaElenco</h2>
                <p style="color: #64748b; margin-bottom: 15px;">Gera uma lista formatada do elenco de um time</p>
                <form method="POST" action="/relatorios?submenu=funcoes">
        """);
        
        try {
            content.append("<div style='margin-bottom: 15px;'><label>Time:</label><select name='time_id' required>");
            List<Time> times = timeDAO.listar();
            for (Time time : times) {
                content.append("<option value='").append(time.getId_time()).append("'>")
                       .append(time.getNome()).append("</option>");
            }
            content.append("</select></div>");
        } catch (SQLException e) {
            content.append("<p>Erro ao carregar dados: ").append(e.getMessage()).append("</p>");
        }
        
        content.append("""
                    <input type="hidden" name="procedimento" value="elenco">
                    <input type="submit" value="Gerar Lista" class="btn btn-success">
                </form>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Procedimentos Cadastrados</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Tipo</th>
                        </tr>
                    </thead>
                    <tbody>
        """);
        
        for (String[] proc : procedimentos) {
            content.append("<tr>");
            content.append("<td>").append(proc[0]).append("</td>");
            content.append("<td>").append(proc[1]).append("</td>");
            content.append("</tr>");
        }
        
        content.append("""
                    </tbody>
                </table>
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Procedimentos", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarTriggers(HttpExchange exchange) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        List<String[]> triggers = dao.listarTriggers();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>⚡ Triggers do Banco de Dados</h1>
                <p>Visualize os triggers cadastrados e seus efeitos</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Triggers Cadastrados</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Evento</th>
                            <th>Tabela</th>
                            <th>Ação (resumo)</th>
                        </tr>
                    </thead>
                    <tbody>
        """);
        
        for (String[] trigger : triggers) {
            content.append("<tr>");
            content.append("<td>").append(trigger[0]).append("</td>");
            content.append("<td>").append(trigger[1]).append("</td>");
            content.append("<td>").append(trigger[2]).append("</td>");
            content.append("<td>").append(trigger[3]).append("</td>");
            content.append("</tr>");
        }
        
        content.append("""
                    </tbody>
                </table>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-top: 30px;">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Como Testar os Triggers</h2>
                <ul style="color: #64748b; line-height: 1.8;">
                    <li><strong>trg_ProcessarDeleteJogador:</strong> Delete um jogador e veja o log em "Logs de Triggers"</li>
                    <li><strong>trg_ImpedirExclusaoTimeComJogadores:</strong> Tente deletar um time que possui jogadores</li>
                    <li><strong>trg_AtualizarQuantJogadoresAposInsert:</strong> Adicione um jogador e veja a contagem atualizar</li>
                </ul>
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Triggers", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void mostrarLogs(HttpExchange exchange) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        List<String[]> logs = dao.buscarLogsJogadoresDeletados();
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>📝 Logs de Triggers</h1>
                <p>Visualize os logs gerados pelos triggers quando jogadores são deletados</p>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #1e293b; margin-bottom: 20px;">Logs de Jogadores Deletados</h2>
                <p style="color: #64748b; margin-bottom: 15px;">Estes logs são gerados automaticamente pelo trigger trg_ProcessarDeleteJogador</p>
        """);
        
        if (logs.isEmpty()) {
            content.append("<p style='color: #64748b;'>Nenhum log encontrado. Os logs aparecerão aqui quando jogadores forem deletados.</p>");
        } else {
            content.append("""
                <table>
                    <thead>
                        <tr>
                            <th>ID Log</th>
                            <th>ID Jogador</th>
                            <th>Nome Jogador</th>
                            <th>ID Time Antigo</th>
                            <th>Data/Hora</th>
                        </tr>
                    </thead>
                    <tbody>
            """);
            
            for (String[] log : logs) {
                content.append("<tr>");
                for (String campo : log) {
                    content.append("<td>").append(campo != null ? campo : "N/A").append("</td>");
                }
                content.append("</tr>");
            }
            
            content.append("</tbody></table>");
        }
        
        content.append("""
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Logs de Triggers", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void executarFuncao(HttpExchange exchange, Map<String, String> params) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        String funcao = params.get("funcao");
        String resultado = "";
        
        if ("classificar".equals(funcao)) {
            int gols = Integer.parseInt(params.get("gols"));
            resultado = dao.classificarDesempenho(gols);
        } else if ("participacoes".equals(funcao)) {
            int gols = Integer.parseInt(params.get("gols"));
            int assistencias = Integer.parseInt(params.get("assistencias"));
            int total = dao.calcularParticipacoesGols(gols, assistencias);
            resultado = "Total de participações: " + total;
        }
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>✅ Função Executada</h1>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #16a34a; margin-bottom: 15px;">Resultado:</h2>
                <p style="font-size: 1.2em; color: #1e293b;">""").append(resultado).append("""
                </p>
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes&action=funcoes" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Resultado", "relatorios", content.toString());
        sendResponse(exchange, html, 200);
    }
    
    private void executarProcedimento(HttpExchange exchange, Map<String, String> params) throws IOException, SQLException {
        FuncaoProcedimentoDAO dao = new FuncaoProcedimentoDAO();
        String procedimento = params.get("procedimento");
        String resultado = "";
        
        if ("contratar".equals(procedimento)) {
            int timeId = Integer.parseInt(params.get("time_id"));
            int tecnicoId = Integer.parseInt(params.get("tecnico_id"));
            dao.contratarTecnico(timeId, tecnicoId);
            resultado = "Técnico contratado com sucesso! O contador de times treinados foi atualizado.";
        } else if ("elenco".equals(procedimento)) {
            int timeId = Integer.parseInt(params.get("time_id"));
            String lista = dao.gerarListaElenco(timeId);

            if (lista == null) {
                lista = "A procedure retornou NULL (verifique os dados no banco)";
            }
            resultado = "<pre style='background: #f1f5f9; padding: 15px; border-radius: 5px;'>" + lista + "</pre>";
        }
        
        StringBuilder content = new StringBuilder();
        content.append("""
            <div class="header">
                <h1>✅ Procedimento Executado</h1>
            </div>
            
            <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                <h2 style="color: #16a34a; margin-bottom: 15px;">Resultado:</h2>
                <div style="color: #1e293b;">""").append(resultado).append("""
                </div>
            </div>
            
            <div style="margin-top: 20px;">
                <a href="/relatorios?submenu=funcoes&action=procedimentos" class="btn btn-primary">← Voltar</a>
            </div>
        """);
        
        String html = Template.render("Resultado", "relatorios", content.toString());
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


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
import java.util.ArrayList;
import dao.TimeDAO;
import dao.JogadorDAO;
import dao.TecnicoDAO;
import dao.EstadioDAO;
import dao.PresidenteDAO;
import dao.RelatorioDAO;
import dao.DashboardEstatisticoDAO;
import model.Time;
import model.Jogador;
import model.Tecnico;
import model.Estadio;
import model.Presidente;
import main.TecnicosHandler;
import main.PresidentesHandler;
import main.EstadiosHandler;
import main.FuncoesProcedimentosHandler;
import main.ViewsConsultasHandler;
import main.Template;

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
        server.createContext("/tecnicos", new TecnicosHandler());
        server.createContext("/presidentes", new PresidentesHandler());
        server.createContext("/estadios", new EstadiosHandler());
        server.createContext("/funcoes-procedimentos", new FuncoesProcedimentosHandler());
        server.createContext("/views-consultas", new ViewsConsultasHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor CRUD iniciado com sucesso!");
        System.out.println("🌐 Acesse: http://localhost:" + PORT);
        System.out.println("⏹️  Para parar, pressione Ctrl+C");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            DashboardEstatisticoDAO statsDAO = new DashboardEstatisticoDAO();
            JogadorDAO jogadorDAO = new JogadorDAO();
            
            Map<String, Object> stats = new HashMap<>();
            List<Map<String, Object>> posicoes = new ArrayList<>();
            List<Map<String, Object>> nacionalidades = new ArrayList<>();
            List<Map<String, Object>> topArtilheiros = new ArrayList<>();
            List<Map<String, Object>> faixasGols = new ArrayList<>();
            List<Map<String, Object>> correlacaoIdade = new ArrayList<>();
            List<Map<String, Object>> topTimes = new ArrayList<>();
            List<Map<String, Object>> pesDominantes = new ArrayList<>();
            List<Map<String, Object>> estadios = new ArrayList<>();
            List<Map<String, Object>> alturaGolsCabeca = new ArrayList<>();
            List<Map<String, Object>> radarPosicoes = new ArrayList<>();
            List<Map<String, Object>> topSocios = new ArrayList<>();
            List<Map<String, Object>> estatisticasPosicao = new ArrayList<>();
            
            String artilheiroNome = "N/A";
            int artilheiroGols = 0;

            try {
                stats = statsDAO.obterEstatisticasGerais();
                posicoes = statsDAO.distribuirJogadoresPorPosicao();
                nacionalidades = statsDAO.distribuirNacionalidades();
                topArtilheiros = statsDAO.topArtilheiros(10);
                faixasGols = statsDAO.distribuirGolsPorFaixa();
                correlacaoIdade = statsDAO.correlacaoIdadeGols();
                topTimes = statsDAO.topTimesPorGols(10);
                pesDominantes = statsDAO.distribuirPesDominantes();
                estadios = statsDAO.estatisticasEstadios();
                alturaGolsCabeca = statsDAO.correlacaoAlturaGolsCabeca();
                radarPosicoes = statsDAO.tendenciaGolsAssistenciasPorPosicao();
                topSocios = statsDAO.topTimesPorSocios(10);
                estatisticasPosicao = statsDAO.estatisticasPorPosicao();
                
                List<String[]> artilheiros = jogadorDAO.buscarArtilheiros();
                if (!artilheiros.isEmpty()) {
                    artilheiroNome = artilheiros.get(0)[0];
                    artilheiroGols = Integer.parseInt(artilheiros.get(0)[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            StringBuilder content = new StringBuilder();
            
            // Header
            content.append("<div class=\"header\">\n");
            content.append("    <h1>Dashboard <span>Estatístico</span> Brasileirão</h1>\n");
            content.append("    <p>Análises estatísticas completas baseadas nos dados do banco</p>\n");
            content.append("</div>\n");

            // Indicadores Resumidos
            content.append("<div class=\"stats-grid\">\n");
            content.append("    <div class=\"stat-card green\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Total de Jogadores</h3>\n");
            content.append("            <div class=\"number\">").append(stats.getOrDefault("total_jogadores", 0)).append("</div>\n");
            content.append("            <div class=\"subtitle\">Idade média: ").append(String.format("%.1f", stats.getOrDefault("idade_media_jogadores", 0.0))).append(" anos</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">👥</div>\n");
            content.append("    </div>\n");
            
            content.append("    <div class=\"stat-card blue\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Total de Gols</h3>\n");
            content.append("            <div class=\"number\">").append(stats.getOrDefault("total_gols", 0)).append("</div>\n");
            content.append("            <div class=\"subtitle\">Média: ").append(String.format("%.2f", stats.getOrDefault("media_gols_por_jogador", 0.0))).append(" gols/jogador</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">⚽</div>\n");
            content.append("    </div>\n");
            
            content.append("    <div class=\"stat-card purple\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Total de Assistências</h3>\n");
            content.append("            <div class=\"number\">").append(stats.getOrDefault("total_assistencias", 0)).append("</div>\n");
            content.append("            <div class=\"subtitle\">Participações em gols</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">🎯</div>\n");
            content.append("    </div>\n");
            
            content.append("    <div class=\"stat-card orange\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Média de Sócios</h3>\n");
            content.append("            <div class=\"number\">").append(String.format("%.0f", stats.getOrDefault("media_socios_por_time", 0.0))).append("</div>\n");
            content.append("            <div class=\"subtitle\">Por time</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">👑</div>\n");
            content.append("    </div>\n");
            
            content.append("    <div class=\"stat-card yellow\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Capacidade Média</h3>\n");
            content.append("            <div class=\"number\">").append(String.format("%.0f", stats.getOrDefault("capacidade_media_estadios", 0.0))).append("</div>\n");
            content.append("            <div class=\"subtitle\">Estádios</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">🏟️</div>\n");
            content.append("    </div>\n");
            
            content.append("    <div class=\"stat-card red\">\n");
            content.append("        <div class=\"stat-info\">\n");
            content.append("            <h3>Artilheiro</h3>\n");
            content.append("            <div class=\"number\">").append(artilheiroGols).append("</div>\n");
            content.append("            <div class=\"subtitle\">").append(artilheiroNome).append("</div>\n");
            content.append("        </div>\n");
            content.append("        <div class=\"stat-icon\">🏆</div>\n");
            content.append("    </div>\n");
            content.append("</div>\n");

            // Gráficos
            content.append("<div style=\"margin-top: 40px;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 30px;\">📊 Análises Estatísticas</h2>\n");
            content.append("    <div style=\"display: grid; grid-template-columns: repeat(auto-fit, minmax(500px, 1fr)); gap: 30px;\">\n");
            
            // Gráfico 1: Distribuição por Posição (Pizza)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Distribuição de Jogadores por Posição</h3>\n");
            content.append("            <canvas id=\"chartPosicao\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 2: Top Artilheiros (Barras)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Top 10 Artilheiros</h3>\n");
            content.append("            <canvas id=\"chartArtilheiros\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 3: Distribuição de Gols por Faixa (Barras - Histograma)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Distribuição de Gols por Faixa</h3>\n");
            content.append("            <canvas id=\"chartFaixasGols\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 4: Correlação Idade vs Gols (Linha)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Correlação: Idade vs Média de Gols</h3>\n");
            content.append("            <canvas id=\"chartIdadeGols\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 5: Top Times por Gols (Barras)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Top 10 Times por Gols</h3>\n");
            content.append("            <canvas id=\"chartTimesGols\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 6: Pés Dominantes (Pizza)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Distribuição de Pés Dominantes</h3>\n");
            content.append("            <canvas id=\"chartPes\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 7: Nacionalidades (Barras)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Distribuição por Nacionalidade</h3>\n");
            content.append("            <canvas id=\"chartNacionalidades\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 8: Altura vs Gols de Cabeça (Dispersão/Linha)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Correlação: Altura vs Gols de Cabeça</h3>\n");
            content.append("            <canvas id=\"chartAlturaCabeca\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 9: Radar - Gols e Assistências por Posição
            //content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            //content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Radar: Gols e Assistências por Posição</h3>\n");
            //content.append("            <canvas id=\"chartRadar\"></canvas>\n");
            //content.append("        </div>\n");
            
            // Gráfico 10: Top Times por Sócios (Barras)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Top 10 Times por Sócios</h3>\n");
            content.append("            <canvas id=\"chartSocios\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 11: Estatísticas por Posição (Barras Múltiplas)
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Estatísticas por Posição (Média, Desvio)</h3>\n");
            content.append("            <canvas id=\"chartEstatisticasPosicao\"></canvas>\n");
            content.append("        </div>\n");
            
            // Gráfico 12: Top Estádios por Capacidade
            content.append("        <div style=\"background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);\">\n");
            content.append("            <h3 style=\"color: #1e293b; margin-bottom: 15px;\">Top 10 Estádios por Capacidade</h3>\n");
            content.append("            <canvas id=\"chartEstadios\"></canvas>\n");
            content.append("        </div>\n");
            
            content.append("    </div>\n");
            content.append("</div>\n");

            // Preparar dados para JavaScript
            content.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
            content.append("<script>\n");
            
            // Dados para gráficos
            content.append("const dadosPosicao = ").append(convertToJson(posicoes)).append(";\n");
            content.append("const dadosArtilheiros = ").append(convertToJson(topArtilheiros)).append(";\n");
            content.append("const dadosFaixasGols = ").append(convertToJson(faixasGols)).append(";\n");
            content.append("const dadosIdadeGols = ").append(convertToJson(correlacaoIdade)).append(";\n");
            content.append("const dadosTimesGols = ").append(convertToJson(topTimes)).append(";\n");
            content.append("const dadosPes = ").append(convertToJson(pesDominantes)).append(";\n");
            content.append("const dadosNacionalidades = ").append(convertToJson(nacionalidades)).append(";\n");
            content.append("const dadosAlturaCabeca = ").append(convertToJson(alturaGolsCabeca)).append(";\n");
            content.append("const dadosRadar = ").append(convertToJson(radarPosicoes)).append(";\n");
            content.append("const dadosSocios = ").append(convertToJson(topSocios)).append(";\n");
            content.append("const dadosEstatisticasPosicao = ").append(convertToJson(estatisticasPosicao)).append(";\n");
            content.append("const dadosEstadios = ").append(convertToJson(estadios)).append(";\n");
            
            // Scripts dos gráficos
            content.append(getChartScripts());
            content.append("</script>\n");

            String html = Template.render("Dashboard Estatístico", "home", content.toString());

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }
        
        private String convertToJson(List<Map<String, Object>> dados) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < dados.size(); i++) {
                Map<String, Object> item = dados.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                boolean first = true;
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    if (!first) json.append(",");
                    json.append("\"").append(entry.getKey()).append("\":");
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        json.append("\"").append(escapeJson(value.toString())).append("\"");
                    } else if (value instanceof Number) {
                        json.append(value);
                    } else {
                        json.append("\"").append(escapeJson(String.valueOf(value))).append("\"");
                    }
                    first = false;
                }
                json.append("}");
            }
            json.append("]");
            return json.toString();
        }
        
        private String escapeJson(String str) {
            return str.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
        }
        
        private String getChartScripts() {
            return """
                // Gráfico 1: Pizza - Distribuição por Posição
                new Chart(document.getElementById('chartPosicao'), {
                    type: 'pie',
                    data: {
                        labels: dadosPosicao.map(d => d.posicao),
                        datasets: [{
                            data: dadosPosicao.map(d => d.total),
                            backgroundColor: [
                                '#3b82f6', '#16a34a', '#f59e0b', '#ef4444', 
                                '#8b5cf6', '#ec4899', '#06b6d4', '#84cc16'
                            ]
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { position: 'bottom' },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.label || '';
                                        let value = context.parsed || 0;
                                        let percent = dadosPosicao[context.dataIndex].percentual;
                                        return label + ': ' + value + ' (' + percent + '%)';
                                    }
                                }
                            }
                        }
                    }
                });

                // Gráfico 2: Barras - Top Artilheiros
                new Chart(document.getElementById('chartArtilheiros'), {
                    type: 'bar',
                    data: {
                        labels: dadosArtilheiros.map(d => d.nome),
                        datasets: [{
                            label: 'Gols',
                            data: dadosArtilheiros.map(d => d.gols),
                            backgroundColor: 'rgba(59, 130, 246, 0.6)',
                            borderColor: 'rgba(59, 130, 246, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        indexAxis: 'y',
                        scales: { x: { beginAtZero: true } }
                    }
                });

                // Gráfico 3: Barras - Distribuição de Gols por Faixa
                new Chart(document.getElementById('chartFaixasGols'), {
                    type: 'bar',
                    data: {
                        labels: dadosFaixasGols.map(d => d.faixa),
                        datasets: [{
                            label: 'Quantidade de Jogadores',
                            data: dadosFaixasGols.map(d => d.quantidade),
                            backgroundColor: 'rgba(22, 163, 74, 0.6)',
                            borderColor: 'rgba(22, 163, 74, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: { y: { beginAtZero: true } }
                    }
                });

                // Gráfico 4: Linha - Correlação Idade vs Gols
                new Chart(document.getElementById('chartIdadeGols'), {
                    type: 'line',
                    data: {
                        labels: dadosIdadeGols.map(d => d.idade + ' anos'),
                        datasets: [{
                            label: 'Média de Gols',
                            data: dadosIdadeGols.map(d => d.media_gols),
                            borderColor: 'rgba(239, 68, 68, 1)',
                            backgroundColor: 'rgba(239, 68, 68, 0.1)',
                            tension: 0.4,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: { y: { beginAtZero: true } }
                    }
                });

                // Gráfico 5: Barras - Top Times por Gols
                new Chart(document.getElementById('chartTimesGols'), {
                    type: 'bar',
                    data: {
                        labels: dadosTimesGols.map(d => d.time),
                        datasets: [{
                            label: 'Total de Gols',
                            data: dadosTimesGols.map(d => d.total_gols),
                            backgroundColor: 'rgba(139, 92, 246, 0.6)',
                            borderColor: 'rgba(139, 92, 246, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        indexAxis: 'y',
                        scales: { x: { beginAtZero: true } }
                    }
                });

                // Gráfico 6: Pizza - Pés Dominantes
                new Chart(document.getElementById('chartPes'), {
                    type: 'doughnut',
                    data: {
                        labels: dadosPes.map(d => d.pe),
                        datasets: [{
                            data: dadosPes.map(d => d.total),
                            backgroundColor: ['#3b82f6', '#f59e0b']
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { position: 'bottom' },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let percent = dadosPes[context.dataIndex].percentual;
                                        return context.label + ': ' + context.parsed + ' (' + percent + '%)';
                                    }
                                }
                            }
                        }
                    }
                });

                // Gráfico 7: Barras - Nacionalidades
                new Chart(document.getElementById('chartNacionalidades'), {
                    type: 'bar',
                    data: {
                        labels: dadosNacionalidades.map(d => d.nacionalidade),
                        datasets: [{
                            label: 'Jogadores',
                            data: dadosNacionalidades.map(d => d.total),
                            backgroundColor: 'rgba(236, 72, 153, 0.6)',
                            borderColor: 'rgba(236, 72, 153, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: { y: { beginAtZero: true } }
                    }
                });

                // Gráfico 8: Linha - Altura vs Gols de Cabeça
                new Chart(document.getElementById('chartAlturaCabeca'), {
                    type: 'scatter',
                    data: {
                        datasets: [{
                            label: 'Gols de Cabeça',
                            data: dadosAlturaCabeca.map(d => ({
                                x: d.altura,
                                y: d.media_gols_cabeca
                            })),
                            backgroundColor: 'rgba(6, 182, 212, 0.6)',
                            borderColor: 'rgba(6, 182, 212, 1)'
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            x: { title: { display: true, text: 'Altura (m)' } },
                            y: { title: { display: true, text: 'Média de Gols de Cabeça' }, beginAtZero: true }
                        }
                    }
                });

                // Gráfico 9: Radar - Gols e Assistências por Posição
                new Chart(document.getElementById('chartRadar'), {
                    type: 'radar',
                    data: {
                        labels: dadosRadar.map(d => d.posicao),
                        datasets: [
                            {
                                label: 'Média de Gols',
                                data: dadosRadar.map(d => d.media_gols),
                                borderColor: 'rgba(59, 130, 246, 1)',
                                backgroundColor: 'rgba(59, 130, 246, 0.2)'
                            },
                            {
                                label: 'Média de Assistências',
                                data: dadosRadar.map(d => d.media_assistencias),
                                borderColor: 'rgba(22, 163, 74, 1)',
                                backgroundColor: 'rgba(22, 163, 74, 0.2)'
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            r: { beginAtZero: true }
                        }
                    }
                });

                // Gráfico 10: Barras - Top Times por Sócios
                new Chart(document.getElementById('chartSocios'), {
                    type: 'bar',
                    data: {
                        labels: dadosSocios.map(d => d.time),
                        datasets: [{
                            label: 'Sócios',
                            data: dadosSocios.map(d => d.socios),
                            backgroundColor: 'rgba(234, 179, 8, 0.6)',
                            borderColor: 'rgba(234, 179, 8, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        indexAxis: 'y',
                        scales: { x: { beginAtZero: true } }
                    }
                });

                // Gráfico 11: Barras Múltiplas - Estatísticas por Posição
                new Chart(document.getElementById('chartEstatisticasPosicao'), {
                    type: 'bar',
                    data: {
                        labels: dadosEstatisticasPosicao.map(d => d.posicao),
                        datasets: [
                            {
                                label: 'Média de Gols',
                                data: dadosEstatisticasPosicao.map(d => d.media_gols),
                                backgroundColor: 'rgba(59, 130, 246, 0.6)'
                            },
                            {
                                label: 'Desvio Padrão de Gols',
                                data: dadosEstatisticasPosicao.map(d => d.desvio_gols || 0),
                                backgroundColor: 'rgba(239, 68, 68, 0.6)'
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        scales: { y: { beginAtZero: true } }
                    }
                });

                // Gráfico 12: Barras - Top Estádios
                new Chart(document.getElementById('chartEstadios'), {
                    type: 'bar',
                    data: {
                        labels: dadosEstadios.map(d => d.estadio),
                        datasets: [{
                            label: 'Capacidade',
                            data: dadosEstadios.map(d => d.capacidade),
                            backgroundColor: 'rgba(245, 158, 11, 0.6)',
                            borderColor: 'rgba(245, 158, 11, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        indexAxis: 'y',
                        scales: { x: { beginAtZero: true } }
                    }
                });
            """;
        }
    }


    static class RelatoriosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
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
                String submenu = params.getOrDefault("submenu", "");
                
                // Redirecionar para funções/procedimentos ou views/consultas
                if ("funcoes".equals(submenu)) {
                    FuncoesProcedimentosHandler handler = new FuncoesProcedimentosHandler();
                    handler.handle(exchange);
                    return;
                } else if ("consultas".equals(submenu)) {
                    ViewsConsultasHandler handler = new ViewsConsultasHandler();
                    handler.handle(exchange);
                    return;
                }
                
                if (relatorioType.isEmpty()) {
                    mostrarMenuRelatorios(exchange);
                    return;
                }

                RelatorioDAO dao = new RelatorioDAO();
                List<String[]> dadosRelatorio;
                String titulo = "";
                String[] cabecalhos = {};

                switch (relatorioType) {
                    case "times_estatisticas":
                        titulo = "Relatório: Times com Estatísticas";
                        cabecalhos = new String[] { "Time", "Total de Jogadores", "Total de Gols", "Idade Média", "Técnico" };
                        dadosRelatorio = dao.relatorioTimesComJogadores();
                        break;
                    case "estatisticas_posicao":
                        titulo = "Relatório: Estatísticas por Posição";
                        cabecalhos = new String[] { "Posição", "Total de Jogadores", "Idade Média", "Altura Média",
                                "Total de Gols", "Total de Assistências" };
                        dadosRelatorio = dao.relatorioEstatisticasPorPosicao();
                        break;
                    case "times_estadios":
                        titulo = "Relatório: Times e seus Estádios";
                        cabecalhos = new String[] { "Time", "Estádio", "Capacidade", "Bairro", "Endereço", "Presidente" };
                        dadosRelatorio = dao.relatorioTimesComEstadios();
                        break;
                    case "tecnicos_experiencia":
                        titulo = "Relatório: Técnicos por Experiência";
                        cabecalhos = new String[] { "Técnico", "Idade", "Times Treinados", "Time Atual", "Presidente" };
                        dadosRelatorio = dao.relatorioTecnicosExperiencia();
                        break;
                    default:
                        mostrarMenuRelatorios(exchange);
                        return;
                }

                StringBuilder content = new StringBuilder();
                content.append("<h1>").append(titulo).append("</h1>");
                content.append("<table>");
                content.append("<thead><tr>");
                for (String cabecalho : cabecalhos) {
                    content.append("<th>").append(cabecalho).append("</th>");
                }
                content.append("</tr></thead>");

                content.append("<tbody>");
                if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
                    for (String[] linha : dadosRelatorio) {
                        content.append("<tr>");
                        for (String campo : linha) {
                            content.append("<td>").append(campo).append("</td>");
                        }
                        content.append("</tr>");
                    }
                } else {
                    content.append("<tr><td colspan='").append(cabecalhos.length)
                            .append("'>Nenhum dado encontrado.</td></tr>");
                }
                content.append("</tbody></table>");

                String html = Template.render(titulo, "relatorios", content.toString());
                sendResponse(exchange, html, 200);

            } catch (SQLException e) {
                String error = "Erro no banco de dados: " + e.getMessage();
                e.printStackTrace();
                sendResponse(exchange, Template.render("Erro", "relatorios", "<h1>Erro 500</h1><p>" + error + "</p>"), 500);
            }
        }

        private void mostrarMenuRelatorios(HttpExchange exchange) throws IOException {
            StringBuilder content = new StringBuilder();
            content.append("<div class=\"header\">\n");
            content.append("    <h1>📊 Menu de Relatórios</h1>\n");
            content.append("    <p>Selecione uma das opções abaixo para visualizar relatórios, consultas e funcionalidades do banco de dados.</p>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Relatórios Básicos</h2>\n");
            content.append("    <div class='menu-relatorios' style='margin-top: 20px;'>\n");
            content.append("        <a href='/relatorios?type=times_estatisticas' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times com Estatísticas</a>\n");
            content.append("        <a href='/relatorios?type=estatisticas_posicao' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Estatísticas por Posição</a>\n");
            content.append("        <a href='/relatorios?type=times_estadios' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Times e seus Estádios</a>\n");
            content.append("        <a href='/relatorios?type=tecnicos_experiencia' class='btn btn-primary' style='display:block; margin-bottom:10px;'>Técnicos por Experiência</a>\n");
            content.append("        <a href='/dashboard' class='btn btn-success' style='display:block; margin-bottom:10px;'>Gráficos de Estatística (Dashboard)</a>\n");
            content.append("    </div>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Funções, Procedimentos e Triggers</h2>\n");
            content.append("    <p style=\"color: #64748b; margin-bottom: 15px;\">Execute funções, procedimentos e visualize os efeitos dos triggers do banco de dados.</p>\n");
            content.append("    <a href='/relatorios?submenu=funcoes' class='btn btn-primary' style='display:block; margin-bottom:10px;'>⚙️ Funções, Procedimentos e Triggers</a>\n");
            content.append("</div>\n");
            
            content.append("<div style=\"margin: 30px 0;\">\n");
            content.append("    <h2 style=\"color: #1e293b; margin-bottom: 20px;\">Views e Consultas</h2>\n");
            content.append("    <p style=\"color: #64748b; margin-bottom: 15px;\">Visualize views e execute consultas com filtros e gráficos.</p>\n");
            content.append("    <a href='/relatorios?submenu=consultas' class='btn btn-primary' style='display:block; margin-bottom:10px;'>📊 Views e Consultas</a>\n");
            content.append("</div>\n");
            
            String html = Template.render("Menu de Relatórios", "relatorios", content.toString());
            sendResponse(exchange, html, 200);
        }

        private void sendResponse(HttpExchange exchange, String html, int statusCode) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uriPath = exchange.getRequestURI().getPath();
            String filePath = "." + uriPath;
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                String contentType = "application/octet-stream";
                if (filePath.endsWith(".png")) contentType = "image/png";
                else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (filePath.endsWith(".css")) contentType = "text/css";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                    final byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}
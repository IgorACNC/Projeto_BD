package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class DashboardHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Dashboard de Analytics</title>
                    <style>
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                            margin: 0; 
                            background-color: #f4f7f9; 
                            color: #333;
                        }
                        .container { 
                            max-width: 1200px; 
                            margin: 30px auto; 
                            padding: 20px;
                        }
                        header {
                            text-align: center;
                            margin-bottom: 40px;
                        }
                        header h1 {
                            font-size: 2.8em;
                            color: #2c3e50;
                        }
                        .btn-voltar {
                            display: inline-block; padding: 10px 25px; margin-bottom: 20px;
                            background-color: #7f8c8d; color: white; text-decoration: none;
                            border-radius: 50px; font-weight: 600;
                        }
                        .grid-graficos {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
                            gap: 30px;
                        }
                        .grafico-card {
                            background: white;
                            padding: 30px;
                            border-radius: 12px;
                            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
                        }
                        .grafico-card h2 { margin-top: 0; color: #2c3e50; }
                        .grafico-card p { color: #555; line-height: 1.6; }
                        .grafico-imagem img { max-width: 100%; border-radius: 8px; border: 1px solid #eee; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <a href="/" class="btn-voltar">&#8592; Voltar para Home</a>
                        <header>
                            <h1>Dashboard de Analytics</h1>
                        </header>
                        
                        <main class="grid-graficos">
                            <section class="grafico-card">
                                <h2>Média de cartões por jogo dos árbitros do Brasileirão</h2>
                                <p>O histograma mostra a distribuição da média de cartões aplicados por jogo no Campeonato Brasileiro, indicando que a maioria dos árbitros tem média entre 2,2 e 2,4 cartões. Isso revela um padrão disciplinar consistente, permitindo identificar árbitros mais rigorosos ou permissivos e auxiliando em análises de desempenho e estilo de jogo.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura1.png" alt="Gráfico de estatísticas por posição">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Média de faltas por jogo dos árbitros do Brasileirão</h2>
                                <p>O histograma mostra a média de faltas por jogo no Campeonato Brasileiro, indicando que a maioria dos árbitros marca entre 24 e 28 faltas por partida. Isso revela consistência no critério de marcação e permite identificar perfis mais "fluídos" (menos faltas) ou mais "parados" (mais faltas), oferecendo um panorama do estilo de arbitragem e do impacto nas partidas.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura2.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Quantidade de jogadores por time</h2>
                                <p>O gráfico mostra o tamanho dos elencos dos times do Campeonato Brasileiro, permitindo comparar estratégias de gestão esportiva. Elencos maiores indicam mais opções para rodízio, lesões e múltiplas competições, enquanto elencos menores refletem uma aposta em grupos mais enxutos e coesos. Assim, o gráfico revela diferentes filosofias de planejamento que influenciam o desempenho dos clubes na temporada.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Quantidade de sócios torcedores adimplentes por time</h2>
                                <p>O gráfico mostra o número de sócios-torcedores adimplentes dos clubes brasileiros, evidenciando o engajamento e a força financeira das torcidas. Clubes como Palmeiras e Flamengo lideram, indicando maior receita recorrente e capacidade de investimento. A variação entre os times revela diferenças no engajamento e nas estratégias de marketing e fidelização, refletindo o poder de mobilização e a estabilidade financeira de cada clube.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura4.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Porcentagem dos jogadores com os pés dominantes</h2>
                                <p>O gráfico mostra a proporção de jogadores destros (75,5%) e canhotos (24,5%) no futebol brasileiro, confirmando a predominância global de destros. Essa diferença tem impacto tático, pois canhotos podem oferecer vantagens estratégicas em posições específicas e tendem a ser mais valorizados por sua raridade. A análise ajuda a compreender o perfil técnico dos atletas e pode apoiar estudos sobre a relação entre pé dominante e desempenho em campo.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura5.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2> Estádios do futebol brasileiro de maior capacidade</h2>
                                <p>O gráfico mostra a relação entre os estádios brasileiros e suas capacidades, com tendência linear (y = 3,94595 + 2,37044x) indicando crescimento gradual. As capacidades variam de cerca de 10 mil a 80 mil lugares, refletindo diversidade na infraestrutura. Estádios maiores, como Maracanã e Mineirão, geram mais receita e são ideais para grandes jogos, enquanto a maioria se concentra em faixas intermediárias. A análise ajuda gestores a planejar preços, eventos e estratégias de aumento de receita com base na capacidade dos estádios.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura6.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Quantidade de gols de cabeça por altura</h2>
                                <p>O gráfico mostra a relação entre a altura dos jogadores e o número de gols de cabeça, com tendência crescente (y = −12,44924 + 7,36287x), mas correlação fraca devido à dispersão dos pontos. Isso indica que a altura não é o principal fator para marcar gols de cabeça. Aspectos como posicionamento, impulsão, timing e técnica têm maior influência. Assim, a altura pode ser uma vantagem, mas o sucesso no jogo aéreo depende principalmente das habilidades técnicas do jogador.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura7.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Ranking de Artilheiros</h2>
                                <p>Este gráfico mostra os 10 maiores artilheiros do campeonato, destacando os jogadores com mais gols.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura3.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>
                        </main>
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
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
                                <h2>Estatísticas por Posição de Jogador</h2>
                                <p>Análise da distribuição e performance média dos jogadores, agrupados por sua posição em campo.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura1.png" alt="Gráfico de estatísticas por posição">
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
                                <h2>Passes dados ao longo do tempo jogado</h2>
                                <p>O gráfico de dispersão mostra uma correlação positiva entre o número de passes e o tempo em campo: quanto mais passes, maior tende a ser a permanência do jogador. Isso confirma a lógica do futebol, já que atletas mais participativos são mantidos por mais tempo. Porém, a relação não é perfeita, pois há casos de jogadores que entram no segundo tempo e dão muitos passes ou que jogam o tempo todo, mas participam pouco (como goleiros). A análise é útil para entender o estilo de jogo, o papel tático e identificar atletas de alta produtividade em pouco tempo.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura8.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Quantidade de times jogados por idade dos jogadores</h2>
                                <p>O gráfico mostra uma correlação positiva entre idade e número de clubes: jogadores mais velhos tendem a ter passado por mais times, o que reflete a dinâmica natural da carreira no futebol. Contudo, a dispersão dos dados indica que isso não é regra, já que fatores como desempenho, lesões e identificação com um clube também influenciam. A análise ajuda a entender a mobilidade dos atletas, a fidelidade a clubes e a construção de carreiras mais estáveis ou “ciganas”.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura9.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Tempo de cargo por idade do presidente</h2>
                                <p>O gráfico mostra que a idade do presidente tem pouca influência no tempo de permanência no cargo, já que a correlação é muito fraca. Fatores como desempenho esportivo, gestão financeira, apoio político e pressão da torcida são muito mais determinantes. Assim, a estabilidade na presidência depende mais da capacidade de gestão e do contexto do clube do que da faixa etária do dirigente.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura10.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Proporção de nacionalidades dos jogadores</h2>
                                <p>O gráfico mostra que 66,7% dos jogadores do Campeonato Brasileiro são brasileiros, confirmando a predominância de atletas locais. Ainda assim, a presença de estrangeiros, especialmente argentinos, uruguaios e paraguaios, revela uma tendência de internacionalização dos elencos. Isso reflete tanto a valorização de talentos sul-americanos quanto a facilidade de adaptação desses jogadores ao futebol brasileiro, sendo útil para compreender estratégias de recrutamento dos clubes.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura11.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Percentual de jogadores por posição</h2>
                                <p>O gráfico mostra que atacantes (26,7%) e meias (20%) representam quase metade dos jogadores, destacando a prioridade em posições ofensivas e de criação. Goleiros têm baixa porcentagem (3,3%), enquanto laterais, volantes e zagueiros apresentam distribuição equilibrada. Isso indica que o futebol atual concentra mais investimentos no setor ofensivo, sendo útil para gestores, técnicos e analistas ao avaliar elencos e estratégias de jogo.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura12.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Valor de mercado dos jogadores</h2>
                                <p>O gráfico mostra grande variação no valor de mercado dos jogadores, influenciado por fatores como idade, desempenho, potencial, contrato, posição e clube. Alguns atletas se destacam como ativos de alto valor, capazes de gerar grandes receitas em transferências. Assim, o valor de mercado reflete o capital humano no futebol e sua análise é fundamental para decisões estratégicas de clubes, agentes e olheiros.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura13.png" alt="Gráfico de artilheiros">
                                </div>
                            </section>

                            <section class="grafico-card">
                                <h2>Frequência de pesos dos jogadores de acordo com a posição</h2>
                                <p>O gráfico de calor mostra padrões de peso por posição: zagueiros e atacantes concentram-se em faixas mais altas, laterais e meias em faixas intermediárias, enquanto goleiros apresentam distribuição mais variada. Isso indica que cada posição exige um perfil físico específico. A análise é útil para treinadores e preparadores na avaliação de elencos, no ajuste de treinos e na identificação de desequilíbrios físicos.</p>
                                <div class="grafico-imagem">
                                    <img src="/public/images/figura14.png" alt="Gráfico de artilheiros">
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
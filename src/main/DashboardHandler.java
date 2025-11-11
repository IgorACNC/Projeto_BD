package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import main.Template; // Importe a nova classe

public class DashboardHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        // O CSS antigo foi removido, pois agora está no Template.
        // O CSS específico para esta página foi adicionado ao Template.
        // Vamos construir apenas o conteúdo.

        String content = """
            <style>
            /* Estilos especificos para esta pagina de dashboard */
            .grid-graficos {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
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
            
            <h1 style="text-align: center; margin-bottom: 30px;">Dashboard de Análises</h1>
            
            <main class="grid-graficos">
                <section class="grafico-card">
                    <h2>Média de cartões por jogo dos árbitros</h2>
                    <p>O histograma mostra a distribuição da média de cartões... (árbitros tem média entre 2,2 e 2,4 cartões).</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura1.png" alt="Média de cartões por jogo">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Média de faltas por jogo dos árbitros</h2>
                    <p>O histograma mostra a média de faltas por jogo... (maioria dos árbitros marca entre 24 e 28 faltas por partida).</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura2.png" alt="Média de faltas por jogo">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Quantidade de jogadores por time</h2>
                    <p>O gráfico mostra o tamanho dos elencos dos times... (Elencos maiores indicam mais opções...).</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura3.png" alt="Quantidade de jogadores por time">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Quantidade de sócios torcedores por time</h2>
                    <p>O gráfico mostra o número de sócios-torcedores... (Clubes como Palmeiras e Flamengo lideram...).</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura4.png" alt="Quantidade de sócios torcedores">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Porcentagem dos jogadores com os pés dominantes</h2>
                    <p>O gráfico mostra a proporção de jogadores destros (75,5%) e canhotos (24,5%)...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura5.png" alt="Pés dominantes">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2> Estádios de maior capacidade</h2>
                    <p>O gráfico mostra a relação entre os estádios brasileiros e suas capacidades...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura6.png" alt="Capacidade dos estádios">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Quantidade de gols de cabeça por altura</h2>
                    <p>O gráfico mostra a relação entre a altura dos jogadores e o número de gols de cabeça...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura7.png" alt="Gols de cabeça por altura">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Passes dados ao longo do tempo jogado</h2>
                    <p>O gráfico de dispersão mostra uma correlação positiva entre o número de passes e o tempo em campo...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura8.png" alt="Passes por tempo jogado">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Quantidade de times jogados por idade</h2>
                    <p>O gráfico mostra uma correlação positiva entre idade e número de clubes...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura9.png" alt="Times por idade">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Tempo de cargo por idade do presidente</h2>
                    <p>O gráfico mostra que a idade do presidente tem pouca influência no tempo de permanência no cargo...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura10.png" alt="Tempo de cargo por idade">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Proporção de nacionalidades dos jogadores</h2>
                    <p>O gráfico mostra que 66,7% dos jogadores do Campeonato Brasileiro são brasileiros...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura11.png" alt="Nacionalidades dos jogadores">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Percentual de jogadores por posição</h2>
                    <p>O gráfico mostra que atacantes (26,7%) e meias (20%) representam quase metade dos jogadores...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura12.png" alt="Jogadores por posição">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Valor de mercado dos jogadores</h2>
                    <p>O gráfico mostra grande variação no valor de mercado dos jogadores...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura13.png" alt="Valor de mercado">
                    </div>
                </section>

                <section class="grafico-card">
                    <h2>Frequência de pesos por posição</h2>
                    <p>O gráfico de calor mostra padrões de peso por posição: zagueiros e atacantes concentram-se em faixas mais altas...</p>
                    <div class="grafico-imagem">
                        <img src="/public/images/figura14.png" alt="Peso por posição">
                    </div>
                </section>
            </main>
            """;
        
        // Renderiza a página usando o Template
        String html = Template.render("Dashboard de Análises", "relatorios", content);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes());
        }
    }
}
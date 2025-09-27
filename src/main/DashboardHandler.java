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
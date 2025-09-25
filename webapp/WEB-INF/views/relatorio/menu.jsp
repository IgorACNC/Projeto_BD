<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="pt-BR">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Relatórios - Brasileirão Série A</title>
        <link rel="stylesheet" href="../../css/style.css">
    </head>

    <body>
        <header>
            <div class="container">
                <h1>📊 Relatórios e Análises</h1>
            </div>
        </header>

        <nav>
            <div class="container">
                <ul>
                    <li><a href="../../index.jsp">Home</a></li>
                    <li><a href="../../times?action=list">Times</a></li>
                    <li><a href="../../jogadores?action=list">Jogadores</a></li>
                    <li><a href="../tecnico/listar.jsp">Técnicos</a></li>
                    <li><a href="../estadio/listar.jsp">Estádios</a></li>
                    <li><a href="../presidente/listar.jsp">Presidentes</a></li>
                    <li><a href="menu.jsp">Relatórios</a></li>
                </ul>
            </div>
        </nav>

        <div class="container">
            <div class="card">
                <h2>Relatórios Disponíveis</h2>
                <p>Consulte análises detalhadas com consultas JOIN entre as tabelas do sistema.</p>

                <div class="grid">
                    <div class="stat-card">
                        <h3>🏆 Times com Estatísticas</h3>
                        <p>Análise completa dos times com dados de jogadores, técnicos e gols.</p>
                        <a href="relatorios?action=times" class="btn btn-primary">Ver Relatório</a>
                    </div>

                    <div class="stat-card">
                        <h3>⚽ Estatísticas por Posição</h3>
                        <p>Média de idade, altura, gols e assistências por posição dos jogadores.</p>
                        <a href="relatorios?action=posicoes" class="btn btn-primary">Ver Relatório</a>
                    </div>

                    <div class="stat-card">
                        <h3>🏟️ Times e Estádios</h3>
                        <p>Informações sobre os estádios dos times com capacidade e localização.</p>
                        <a href="relatorios?action=estadios" class="btn btn-primary">Ver Relatório</a>
                    </div>

                    <div class="stat-card">
                        <h3>👨‍💼 Técnicos por Experiência</h3>
                        <p>Ranking dos técnicos mais experientes com número de times treinados.</p>
                        <a href="relatorios?action=tecnicos" class="btn btn-primary">Ver Relatório</a>
                    </div>

                    <div class="stat-card">
                        <h3>🌍 Jogadores por Nacionalidade</h3>
                        <p>Distribuição de jogadores por país de origem com estatísticas.</p>
                        <a href="relatorios?action=nacionalidades" class="btn btn-primary">Ver Relatório</a>
                    </div>

                    <div class="stat-card">
                        <h3>🥅 Artilheiros</h3>
                        <p>Top 10 jogadores com mais gols na temporada.</p>
                        <a href="../../jogadores?action=artilheiros" class="btn btn-primary">Ver Relatório</a>
                    </div>
                </div>
            </div>

            <div class="card">
                <h3>Informações sobre os Relatórios</h3>
                <ul>
                    <li><strong>Consultas JOIN:</strong> Todos os relatórios utilizam JOINs entre múltiplas tabelas</li>
                    <li><strong>Análise Completa:</strong> Dados agregados com médias, somas e contagens</li>
                    <li><strong>Ordenação Inteligente:</strong> Resultados ordenados por critérios relevantes</li>
                    <li><strong>Performance:</strong> Consultas otimizadas para melhor desempenho</li>
                </ul>
            </div>
        </div>
    </body>

    </html>
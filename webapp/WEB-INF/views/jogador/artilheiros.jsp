<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Artilheiros - Brasileirão Série A</title>
            <link rel="stylesheet" href="../../css/style.css">
        </head>

        <body>
            <header>
                <div class="container">
                    <h1>🥅 Artilheiros do Campeonato</h1>
                </div>
            </header>

            <nav>
                <div class="container">
                    <ul>
                        <li><a href="../../index.jsp">Home</a></li>
                        <li><a href="../../times?action=list">Times</a></li>
                        <li><a href="jogadores?action=list">Jogadores</a></li>
                        <li><a href="../tecnico/listar.jsp">Técnicos</a></li>
                        <li><a href="../estadio/listar.jsp">Estádios</a></li>
                        <li><a href="../presidente/listar.jsp">Presidentes</a></li>
                        <li><a href="../relatorio/menu.jsp">Relatórios</a></li>
                    </ul>
                </div>
            </nav>

            <div class="container">
                <div class="card">
                    <h2>Top 10 Artilheiros da Temporada</h2>
                    <p>Ranking dos jogadores com mais gols no campeonato brasileiro.</p>

                    <table>
                        <thead>
                            <tr>
                                <th>Posição</th>
                                <th>Jogador</th>
                                <th>Gols</th>
                                <th>Posição</th>
                                <th>Time</th>
                                <th>Nacionalidade</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% List<String[]> artilheiros = (List<String[]>) request.getAttribute("artilheiros");
                                    if (artilheiros != null && !artilheiros.isEmpty()) {
                                    int posicao = 1;
                                    for (String[] artilheiro : artilheiros) {
                                    String jogador = artilheiro[0];
                                    String gols = artilheiro[1];
                                    String pos = artilheiro[2];
                                    String time = artilheiro[3];
                                    String nacionalidade = artilheiro[4];
                                    %>
                                    <tr>
                                        <td>
                                            <%= posicao %>
                                        </td>
                                        <td><strong>
                                                <%= jogador %>
                                            </strong></td>
                                        <td><span style="color: #e74c3c; font-weight: bold; font-size: 1.2em;">
                                                <%= gols %>
                                            </span></td>
                                        <td>
                                            <%= pos %>
                                        </td>
                                        <td>
                                            <%= time %>
                                        </td>
                                        <td>
                                            <%= nacionalidade %>
                                        </td>
                                    </tr>
                                    <% posicao++; } } else { %>
                                        <tr>
                                            <td colspan="6" class="text-center">Nenhum dado encontrado.</td>
                                        </tr>
                                        <% } %>
                        </tbody>
                    </table>

                    <div style="text-align: center; margin-top: 20px;">
                        <a href="jogadores?action=list" class="btn btn-primary">Voltar para Jogadores</a>
                        <a href="../../index.jsp" class="btn btn-secondary">Home</a>
                    </div>
                </div>
            </div>
        </body>

        </html>
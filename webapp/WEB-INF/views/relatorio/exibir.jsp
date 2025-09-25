<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <!DOCTYPE html>
        <html lang="pt-BR">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Relatório - Brasileirão Série A</title>
            <link rel="stylesheet" href="../../css/style.css">
        </head>

        <body>
            <header>
                <div class="container">
                    <h1>📊 Relatório</h1>
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
                    <h2>
                        <%= request.getAttribute("titulo") %>
                    </h2>
                    <p>Relatório gerado com consultas JOIN entre múltiplas tabelas do sistema.</p>

                    <% List<String[]> relatorio = (List<String[]>) request.getAttribute("relatorio");
                            String titulo = (String) request.getAttribute("titulo");

                            if (relatorio != null && !relatorio.isEmpty()) {
                            %>
                            <table>
                                <thead>
                                    <tr>
                                        <% // Cabeçalhos baseados no tipo de relatório if (titulo.contains("Times com
                                            Estatísticas")) { %>
                                            <th>Time</th>
                                            <th>Total Jogadores</th>
                                            <th>Total Gols</th>
                                            <th>Idade Média</th>
                                            <th>Técnico</th>
                                            <% } else if (titulo.contains("Posição")) { %>
                                                <th>Posição</th>
                                                <th>Total Jogadores</th>
                                                <th>Idade Média</th>
                                                <th>Altura Média</th>
                                                <th>Total Gols</th>
                                                <th>Total Assistências</th>
                                                <% } else if (titulo.contains("Estádios")) { %>
                                                    <th>Time</th>
                                                    <th>Estádio</th>
                                                    <th>Capacidade</th>
                                                    <th>Bairro</th>
                                                    <th>Endereço</th>
                                                    <th>Presidente</th>
                                                    <% } else if (titulo.contains("Técnicos")) { %>
                                                        <th>Técnico</th>
                                                        <th>Idade</th>
                                                        <th>Times Treinados</th>
                                                        <th>Time Atual</th>
                                                        <th>Presidente</th>
                                                        <% } else if (titulo.contains("Nacionalidade")) { %>
                                                            <th>Nacionalidade</th>
                                                            <th>Total Jogadores</th>
                                                            <th>Idade Média</th>
                                                            <th>Total Gols</th>
                                                            <th>Média Gols/Jogador</th>
                                                            <% } %>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (String[] linha : relatorio) { %>
                                        <tr>
                                            <% for (String campo : linha) { %>
                                                <td>
                                                    <%= campo %>
                                                </td>
                                                <% } %>
                                        </tr>
                                        <% } %>
                                </tbody>
                            </table>
                            <% } else { %>
                                <div class="alert alert-error">
                                    Nenhum dado encontrado para este relatório.
                                </div>
                                <% } %>

                                    <div style="text-align: center; margin-top: 30px;">
                                        <a href="menu.jsp" class="btn btn-primary">Voltar para Relatórios</a>
                                        <a href="../../index.jsp" class="btn btn-secondary">Home</a>
                                    </div>
                </div>
            </div>
        </body>

        </html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <%@ page import="model.Jogador" %>
            <!DOCTYPE html>
            <html lang="pt-BR">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Jogadores - Brasileirão Série A</title>
                <link rel="stylesheet" href="../../css/style.css">
            </head>

            <body>
                <header>
                    <div class="container">
                        <h1>Jogadores</h1>
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
                        <div
                            style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                            <h2>Lista de Jogadores</h2>
                            <div>
                                <a href="jogadores?action=form" class="btn btn-success">Novo Jogador</a>
                                <a href="jogadores?action=relatorio" class="btn btn-primary">Relatório Completo</a>
                                <a href="jogadores?action=artilheiros" class="btn btn-warning">Artilheiros</a>
                            </div>
                        </div>

                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nome</th>
                                    <th>Posição</th>
                                    <th>Número</th>
                                    <th>Gols</th>
                                    <th>Assistências</th>
                                    <th>Time ID</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% List<Jogador> jogadores = (List<Jogador>) request.getAttribute("jogadores");
                                        if (jogadores != null && !jogadores.isEmpty()) {
                                        for (Jogador jogador : jogadores) {
                                        %>
                                        <tr>
                                            <td>
                                                <%= jogador.getId_jogador() %>
                                            </td>
                                            <td>
                                                <%= jogador.getNome() %>
                                            </td>
                                            <td>
                                                <%= jogador.getPosicao_jogador() %>
                                            </td>
                                            <td>
                                                <%= jogador.getNumero_camisa() %>
                                            </td>
                                            <td>
                                                <%= jogador.getGols_temporada_jogador() %>
                                            </td>
                                            <td>
                                                <%= jogador.getAssistencias() %>
                                            </td>
                                            <td>
                                                <%= jogador.getFk_time() %>
                                            </td>
                                            <td>
                                                <a href="jogadores?action=edit&id=<%= jogador.getId_jogador() %>"
                                                    class="btn btn-warning">Editar</a>
                                                <a href="jogadores?action=delete&id=<%= jogador.getId_jogador() %>"
                                                    class="btn btn-danger"
                                                    onclick="return confirm('Tem certeza que deseja excluir este jogador?')">Excluir</a>
                                            </td>
                                        </tr>
                                        <% } } else { %>
                                            <tr>
                                                <td colspan="8" class="text-center">Nenhum jogador encontrado.</td>
                                            </tr>
                                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </body>

            </html>
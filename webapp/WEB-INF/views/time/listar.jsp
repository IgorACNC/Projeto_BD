<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <%@ page import="model.Time" %>
            <!DOCTYPE html>
            <html lang="pt-BR">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Times - Brasileirão Série A</title>
                <link rel="stylesheet" href="../../css/style.css">
            </head>

            <body>
                <header>
                    <div class="container">
                        <h1>Times</h1>
                    </div>
                </header>

                <nav>
                    <div class="container">
                        <ul>
                            <li><a href="../../index.jsp">Home</a></li>
                            <li><a href="times?action=list">Times</a></li>
                            <li><a href="../jogador/listar.jsp">Jogadores</a></li>
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
                            <h2>Lista de Times</h2>
                            <div>
                                <a href="times?action=form" class="btn btn-success">Novo Time</a>
                                <a href="times?action=relatorio" class="btn btn-primary">Relatório Completo</a>
                            </div>
                        </div>

                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nome</th>
                                    <th>Jogadores</th>
                                    <th>Sócios</th>
                                    <th>ID Técnico</th>
                                    <th>ID Presidente</th>
                                    <th>ID Estádio</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% List<Time> times = (List<Time>) request.getAttribute("times");
                                        if (times != null && !times.isEmpty()) {
                                        for (Time time : times) {
                                        %>
                                        <tr>
                                            <td>
                                                <%= time.getId_time() %>
                                            </td>
                                            <td>
                                                <%= time.getNome() %>
                                            </td>
                                            <td>
                                                <%= time.getQuant_jogadores() %>
                                            </td>
                                            <td>
                                                <%= time.getQuant_socios() %>
                                            </td>
                                            <td>
                                                <%= time.getFk_tecnico() %>
                                            </td>
                                            <td>
                                                <%= time.getFk_presidente() %>
                                            </td>
                                            <td>
                                                <%= time.getFk_estadio() %>
                                            </td>
                                            <td>
                                                <a href="times?action=edit&id=<%= time.getId_time() %>"
                                                    class="btn btn-warning">Editar</a>
                                                <a href="times?action=delete&id=<%= time.getId_time() %>"
                                                    class="btn btn-danger"
                                                    onclick="return confirm('Tem certeza que deseja excluir este time?')">Excluir</a>
                                            </td>
                                        </tr>
                                        <% } } else { %>
                                            <tr>
                                                <td colspan="8" class="text-center">Nenhum time encontrado.</td>
                                            </tr>
                                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </body>

            </html>
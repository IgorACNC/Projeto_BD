<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.List" %>
        <%@ page import="model.Time" %>
            <%@ page import="model.Tecnico" %>
                <%@ page import="model.Presidente" %>
                    <%@ page import="model.Estadio" %>
                        <!DOCTYPE html>
                        <html lang="pt-BR">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Editar Time - Brasileirão Série A</title>
                            <link rel="stylesheet" href="../../css/style.css">
                        </head>

                        <body>
                            <header>
                                <div class="container">
                                    <h1>Editar Time</h1>
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
                                    <h2>Editar Time</h2>

                                    <% Time time=(Time) request.getAttribute("time"); if (time !=null) { %>
                                        <form action="times" method="post">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="id" value="<%= time.getId_time() %>">

                                            <div class="form-group">
                                                <label for="nome">Nome do Time:</label>
                                                <input type="text" id="nome" name="nome" value="<%= time.getNome() %>"
                                                    required>
                                            </div>

                                            <div class="form-group">
                                                <label for="quant_jogadores">Quantidade de Jogadores:</label>
                                                <input type="number" id="quant_jogadores" name="quant_jogadores" min="1"
                                                    max="50" value="<%= time.getQuant_jogadores() %>" required>
                                            </div>

                                            <div class="form-group">
                                                <label for="quant_socios">Quantidade de Sócios:</label>
                                                <input type="number" id="quant_socios" name="quant_socios" min="0"
                                                    value="<%= time.getQuant_socios() %>" required>
                                            </div>

                                            <div class="form-group">
                                                <label for="fk_tecnico">Técnico:</label>
                                                <select id="fk_tecnico" name="fk_tecnico" required>
                                                    <option value="">Selecione um técnico</option>
                                                    <% List<Tecnico> tecnicos = (List<Tecnico>)
                                                            request.getAttribute("tecnicos");
                                                            if (tecnicos != null) {
                                                            for (Tecnico tecnico : tecnicos) {
                                                            String selected = (tecnico.getId_tecnico() ==
                                                            time.getFk_tecnico()) ? "selected" : "";
                                                            %>
                                                            <option value="<%= tecnico.getId_tecnico() %>" <%=selected
                                                                %>>
                                                                <%= tecnico.getNome() %> (ID: <%=
                                                                        tecnico.getId_tecnico() %>)
                                                            </option>
                                                            <% } } %>
                                                </select>
                                            </div>

                                            <div class="form-group">
                                                <label for="fk_presidente">Presidente:</label>
                                                <select id="fk_presidente" name="fk_presidente" required>
                                                    <option value="">Selecione um presidente</option>
                                                    <% List<Presidente> presidentes = (List<Presidente>)
                                                            request.getAttribute("presidentes");
                                                            if (presidentes != null) {
                                                            for (Presidente presidente : presidentes) {
                                                            String selected = (presidente.getId_presidente() ==
                                                            time.getFk_presidente()) ? "selected" : "";
                                                            %>
                                                            <option value="<%= presidente.getId_presidente() %>"
                                                                <%=selected %>>
                                                                <%= presidente.getNome() %> (ID: <%=
                                                                        presidente.getId_presidente() %>)
                                                            </option>
                                                            <% } } %>
                                                </select>
                                            </div>

                                            <div class="form-group">
                                                <label for="fk_estadio">Estádio:</label>
                                                <select id="fk_estadio" name="fk_estadio" required>
                                                    <option value="">Selecione um estádio</option>
                                                    <% List<Estadio> estadios = (List<Estadio>)
                                                            request.getAttribute("estadios");
                                                            if (estadios != null) {
                                                            for (Estadio estadio : estadios) {
                                                            String selected = (estadio.getId_estadio() ==
                                                            time.getFk_estadio()) ? "selected" : "";
                                                            %>
                                                            <option value="<%= estadio.getId_estadio() %>" <%=selected
                                                                %>>
                                                                <%= estadio.getNome() %> (ID: <%=
                                                                        estadio.getId_estadio() %>)
                                                            </option>
                                                            <% } } %>
                                                </select>
                                            </div>

                                            <div style="text-align: center; margin-top: 30px;">
                                                <button type="submit" class="btn btn-success">Atualizar Time</button>
                                                <a href="times?action=list" class="btn btn-secondary">Cancelar</a>
                                            </div>
                                        </form>
                                        <% } else { %>
                                            <div class="alert alert-error">
                                                Time não encontrado.
                                            </div>
                                            <a href="times?action=list" class="btn btn-primary">Voltar para Lista</a>
                                            <% } %>
                                </div>
                            </div>
                        </body>

                        </html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="pt-BR">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Brasileirão Série A - Sistema de Gerenciamento</title>
        <link rel="stylesheet" href="css/style.css">
    </head>

    <body>
        <header>
            <div class="container">
                <h1>🏆 Brasileirão Série A</h1>
                <p style="text-align: center; margin-top: 10px;">Sistema de Gerenciamento de Dados</p>
            </div>
        </header>

        <nav>
            <div class="container">
                <ul>
                    <li><a href="times?action=list">Times</a></li>
                    <li><a href="jogadores?action=list">Jogadores</a></li>
                    <li><a href="tecnicos?action=list">Técnicos</a></li>
                    <li><a href="estadios?action=list">Estádios</a></li>
                    <li><a href="presidentes?action=list">Presidentes</a></li>
                    <li><a href="relatorios?action=menu">Relatórios</a></li>
                </ul>
            </div>
        </nav>

        <div class="container">
            <div class="card">
                <h2>Bem-vindo ao Sistema de Gerenciamento do Brasileirão Série A</h2>
                <p>Este sistema permite gerenciar informações sobre times, jogadores, técnicos, estádios e presidentes
                    do campeonato brasileiro.</p>

                <div class="grid">
                    <div class="stat-card">
                        <h3>Times</h3>
                        <div class="number">30</div>
                        <p>Clubes participantes</p>
                        <a href="times?action=list" class="btn btn-primary">Gerenciar</a>
                    </div>

                    <div class="stat-card">
                        <h3>Jogadores</h3>
                        <div class="number">900+</div>
                        <p>Atletas cadastrados</p>
                        <a href="jogadores?action=list" class="btn btn-primary">Gerenciar</a>
                    </div>

                    <div class="stat-card">
                        <h3>Técnicos</h3>
                        <div class="number">30</div>
                        <p>Treinadores ativos</p>
                        <a href="tecnicos?action=list" class="btn btn-primary">Gerenciar</a>
                    </div>

                    <div class="stat-card">
                        <h3>Estádios</h3>
                        <div class="number">30</div>
                        <p>Arenas do campeonato</p>
                        <a href="estadios?action=list" class="btn btn-primary">Gerenciar</a>
                    </div>
                </div>
            </div>

            <div class="card">
                <h3>Funcionalidades Disponíveis</h3>
                <ul>
                    <li><strong>CRUD Completo:</strong> Criar, visualizar, editar e excluir registros</li>
                    <li><strong>Relatórios Avançados:</strong> Consultas com JOINs para análise de dados</li>
                    <li><strong>Estatísticas:</strong> Artilheiros, times com mais gols, técnicos mais experientes</li>
                    <li><strong>Navegação Intuitiva:</strong> Interface simples e funcional</li>
                </ul>
            </div>

            <div class="card">
                <h3>Relatórios Disponíveis</h3>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px;">
                    <a href="relatorios?action=times" class="btn btn-secondary">Times com Estatísticas</a>
                    <a href="relatorios?action=posicoes" class="btn btn-secondary">Estatísticas por Posição</a>
                    <a href="relatorios?action=estadios" class="btn btn-secondary">Times e Estádios</a>
                    <a href="relatorios?action=tecnicos" class="btn btn-secondary">Técnicos por Experiência</a>
                    <a href="relatorios?action=nacionalidades" class="btn btn-secondary">Jogadores por Nacionalidade</a>
                    <a href="jogadores?action=artilheiros" class="btn btn-secondary">Artilheiros</a>
                </div>
            </div>
        </div>
    </body>

    </html>
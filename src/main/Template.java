package main;

public class Template {
    private static final String CSS_STYLES = """
            <style>
                /* Reset Básico */
                * { margin: 0; padding: 0; box-sizing: border-box; }
                
                /* Layout Principal */
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                    background: #f5f7fa;
                    display: flex;
                    min-height: 100vh;
                }

                /* --- Sidebar CSS (da sua Home) --- */
                .sidebar {
                    width: 250px;
                    background: linear-gradient(180deg, #16a34a 0%, #15803d 100%);
                    color: white;
                    padding: 0;
                    position: fixed;
                    height: 100vh;
                    overflow-y: auto;
                    flex-shrink: 0;
                }
                .logo-section {
                    padding: 30px 20px;
                    border-bottom: 1px solid rgba(255,255,255,0.1);
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }
                .logo-icon {
                    width: 48px;
                    height: 48px;
                    background: #fbbf24;
                    border-radius: 8px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 28px;
                    flex-shrink: 0;
                }
                .logo-text h2 { font-size: 1.2em; font-weight: 700; margin-bottom: 2px; }
                .logo-text p { font-size: 0.75em; opacity: 0.9; }
                .nav-section { padding: 20px 0; }
                .nav-title {
                    padding: 10px 20px;
                    font-size: 0.7em;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    opacity: 0.7;
                    font-weight: 600;
                }
                .nav-item {
                    padding: 12px 20px;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    color: white;
                    text-decoration: none;
                    transition: background 0.2s;
                    cursor: pointer;
                }
                .nav-item:hover { background: rgba(255,255,255,0.1); }
                .nav-item.active {
                    background: rgba(251, 191, 36, 0.2);
                    border-left: 3px solid #fbbf24;
                    padding-left: 17px; /* 20px - 3px */
                }
                .nav-icon { font-size: 18px; width: 20px; text-align: center; }

                /* --- Conteúdo Principal (Main Content) --- */
                .main-content {
                    margin-left: 250px; /* IMPORTANTE: Deixa espaço para a sidebar */
                    flex: 1;
                    padding: 40px;
                    width: calc(100% - 250px);
                }
                
                /* Títulos da Página */
                .main-content h1 {
                    font-size: 2.2em;
                    color: #1e293b;
                    margin-bottom: 20px;
                }
                .main-content h1 span { color: #16a34a; }

                /* --- CSS para Tabelas --- */
                table {
                    border-collapse: collapse;
                    width: 100%;
                    margin: 20px 0;
                    background: white;
                    border-radius: 8px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    font-size: 0.9em;
                }
                th, td {
                    border: 1px solid #e2e8f0;
                    padding: 12px 15px;
                    text-align: left;
                    vertical-align: middle;
                }
                th { background-color: #f8f9fa; font-weight: 600; color: #475569; }
                tr:hover { background-color: #f9fafb; }

                /* --- CSS para Botões --- */
                .btn {
                    padding: 8px 15px;
                    margin: 2px;
                    text-decoration: none;
                    border: none;
                    border-radius: 5px;
                    color: white;
                    display: inline-block;
                    cursor: pointer;
                    font-size: 0.9em;
                }
                .btn-primary { background: #3b82f6; } .btn-primary:hover { background: #2563eb; }
                .btn-success { background: #16a34a; } .btn-success:hover { background: #15803d; }
                .btn-warning { background: #f59e0b; } .btn-warning:hover { background: #d97706; }
                .btn-danger { background: #ef4444; } .btn-danger:hover { background: #dc2626; }

                /* --- CSS para Formulários (Novo/Editar) --- */
                form {
                    background: white;
                    padding: 24px;
                    border-radius: 8px;
                    margin: 20px 0;
                    border: 1px solid #e2e8f0;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                form label { font-weight: 600; display: block; margin-bottom: 5px; color: #334155; }
                input[type=text], input[type=number], input[type=url], select {
                    padding: 10px;
                    margin-bottom: 15px;
                    border: 1px solid #cbd5e1;
                    border-radius: 5px;
                    width: 100%;
                    max-width: 400px;
                    font-size: 1em;
                }
                input[type=submit] {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 5px;
                    color: white;
                    background: #16a34a;
                    cursor: pointer;
                    font-size: 1em;
                    font-weight: 600;
                }
                input[type=submit]:hover { background: #15803d; }
                form .btn { margin-top: 15px; }

                td form {
                    background: none;
                    padding: 0;
                    margin: 0;
                    border: none;
                    box-shadow: none;
                }

                .header { margin-bottom: 40px; }
                .header h1 { font-size: 2.2em; color: #1e293b; margin-bottom: 8px; }
                .header p { color: #64748b; font-size: 1em; }
                .stats-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                    gap: 20px;
                    margin-bottom: 40px;
                }
                .stat-card {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    transition: transform 0.2s, box-shadow 0.2s;
                }
                .stat-card:hover {
                    transform: translateY(-4px);
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                }
                .stat-info h3 {
                    font-size: 0.85em;
                    color: #64748b;
                    font-weight: 600;
                    margin-bottom: 8px;
                }
                .stat-info .number {
                    font-size: 2.5em;
                    font-weight: 700;
                    color: #1e293b;
                    line-height: 1;
                }
                .stat-info .subtitle {
                    font-size: 0.85em;
                    color: #94a3b8;
                    margin-top: 4px;
                }
                .stat-icon {
                    width: 64px;
                    height: 64px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 28px;
                    color: white; /* Ícones do dashboard são brancos */
                }
                .stat-card.green .stat-icon { background: linear-gradient(135deg, #16a34a, #22c55e); }
                .stat-card.blue .stat-icon { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
                .stat-card.purple .stat-icon { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }
                .stat-card.orange .stat-icon { background: linear-gradient(135deg, #f97316, #fb923c); }
                .stat-card.yellow .stat-icon { background: linear-gradient(135deg, #eab308, #facc15); }
                .stat-card.red .stat-icon { background: linear-gradient(135deg, #ef4444, #f87171); }

                .activity-section {
                    background: white;
                    border-radius: 12px;
                    padding: 30px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                .activity-section h2 {
                    font-size: 1.4em;
                    color: #1e293b;
                    margin-bottom: 25px;
                    font-weight: 700;
                }
                .activity-item {
                    display: flex;
                    align-items: center;
                    gap: 16px;
                    padding: 16px 0;
                    border-bottom: 1px solid #f1f5f9;
                }
                .activity-item:last-child { border-bottom: none; }
                .activity-icon {
                    width: 48px;
                    height: 48px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 20px;
                    flex-shrink: 0;
                }
                .activity-icon.jogador { background: #dbeafe; color: #3b82f6; }
                .activity-icon.time { background: #dcfce7; color: #16a34a; }
                .activity-info h4 {
                    font-size: 1em;
                    color: #1e293b;
                    margin-bottom: 4px;
                    font-weight: 600;
                }
                .activity-badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 12px;
                    font-size: 0.75em;
                    font-weight: 600;
                }
                .activity-badge.jogador { background: #dbeafe; color: #2563eb; }
                .activity-badge.time { background: #dcfce7; color: #16a34a; }
                .activity-date { font-size: 0.85em; color: #94a3b8; }

                /* Responsividade */
                @media (max-width: 768px) {
                    .sidebar { display: none; }
                    .main-content { margin-left: 0; padding: 20px; width: 100%; }
                    th, td { padding: 8px; }
                    .stats-grid { grid-template-columns: 1fr; }
                }
            </style>
            """;

    /**
     * Gera o HTML da barra lateral, marcando o item correto como "active".
     * @param activeNav O nome da página (ex: "jogadores", "times")
     * @return O HTML da barra lateral.
     */
    private static String getSidebarHtml(String activeNav) {
        return String.format("""
            <div class="sidebar">
                <div class="logo-section">
                    <div class="logo-icon">🏆</div>
                    <div class="logo-text">
                        <h2>Brasileirão</h2>
                        <p>Sistema de Gestão</p>
                    </div>
                </div>
                <nav class="nav-section">
                    <div class="nav-title">NAVEGAÇÃO PRINCIPAL</div>
                    <a href="/" class="nav-item %s">
                        <span class="nav-icon">🏠</span>
                        <span>Dashboard</span>
                    </a>
                    <a href="/times" class="nav-item %s">
                        <span class="nav-icon">⚽</span>
                        <span>Times</span>
                    </a>
                    <a href="/jogadores" class="nav-item %s">
                        <span class="nav-icon">👤</span>
                        <span>Jogadores</span>
                    </a>
                    <a href="/tecnicos" class="nav-item %s">
                        <span class="nav-icon">📋</span>
                        <span>Técnicos</span>
                    </a>
                    <a href="/estadios" class="nav-item %s">
                        <span class="nav-icon">🏟️</span>
                        <span>Estádios</span>
                    </a>
                    <a href="/presidentes" class="nav-item %s">
                        <span class="nav-icon">👑</span>
                        <span>Presidentes</span>
                    </a>
                    <a href="/relatorios" class="nav-item %s">
                        <span class="nav-icon">📊</span>
                        <span>Relatórios</span>
                    </a>
                </nav>
            </div>
        """,
            "home".equals(activeNav) ? "active" : "",
            "times".equals(activeNav) ? "active" : "",
            "jogadores".equals(activeNav) ? "active" : "",
            "tecnicos".equals(activeNav) ? "active" : "",
            "estadios".equals(activeNav) ? "active" : "",
            "presidentes".equals(activeNav) ? "active" : "",
            "relatorios".equals(activeNav) ? "active" : "");
    }

    /**
     * Método principal para renderizar qualquer página.
     * @param pageTitle O título que vai na aba do navegador.
     * @param activeNav O nome da página para marcar como "ativa" na sidebar.
     * @param mainContent O HTML do conteúdo principal (tabelas, forms, etc.).
     * @return O HTML completo da página.
     */
    public static String render(String pageTitle, String activeNav, String mainContent) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s - Brasileirão</title>
                %s
            </head>
            <body>
                %s
                <main class="main-content">
                    %s
                </main>
            </body>
            </html>
        """, pageTitle, CSS_STYLES, getSidebarHtml(activeNav), mainContent);
    }
}
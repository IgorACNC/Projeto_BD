package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardEstatisticoDAO {

    /**
     * Retorna estatísticas gerais do sistema
     */
    public Map<String, Object> obterEstatisticasGerais() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM Time) as total_times, " +
                "(SELECT COUNT(*) FROM Jogador) as total_jogadores, " +
                "(SELECT COUNT(*) FROM Tecnico) as total_tecnicos, " +
                "(SELECT COUNT(*) FROM Estadio) as total_estadios, " +
                "(SELECT COUNT(*) FROM Presidente) as total_presidentes, " +
                "(SELECT COUNT(*) FROM Partida) as total_partidas, " +
                "(SELECT AVG(idade) FROM Jogador) as idade_media_jogadores, " +
                "(SELECT AVG(altura) FROM Jogador) as altura_media_jogadores, " +
                "(SELECT AVG(peso) FROM Jogador) as peso_medio_jogadores, " +
                "(SELECT SUM(gols_temporada_jogador) FROM Jogador) as total_gols, " +
                "(SELECT SUM(assistencias) FROM Jogador) as total_assistencias, " +
                "(SELECT AVG(gols_temporada_jogador) FROM Jogador) as media_gols_por_jogador, " +
                "(SELECT AVG(quant_socios) FROM Time) as media_socios_por_time, " +
                "(SELECT AVG(capacidade) FROM Estadio) as capacidade_media_estadios, " +
                "(SELECT AVG(idade) FROM Tecnico) as idade_media_tecnicos, " +
                "(SELECT AVG(media_cartoes) FROM Arbitro) as media_cartoes_arbitros, " +
                "(SELECT AVG(media_faltas) FROM Arbitro) as media_faltas_arbitros";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                stats.put("total_times", rs.getInt("total_times"));
                stats.put("total_jogadores", rs.getInt("total_jogadores"));
                stats.put("total_tecnicos", rs.getInt("total_tecnicos"));
                stats.put("total_estadios", rs.getInt("total_estadios"));
                stats.put("total_presidentes", rs.getInt("total_presidentes"));
                stats.put("total_partidas", rs.getInt("total_partidas"));
                stats.put("idade_media_jogadores", rs.getDouble("idade_media_jogadores"));
                stats.put("altura_media_jogadores", rs.getDouble("altura_media_jogadores"));
                stats.put("peso_medio_jogadores", rs.getDouble("peso_medio_jogadores"));
                stats.put("total_gols", rs.getInt("total_gols"));
                stats.put("total_assistencias", rs.getInt("total_assistencias"));
                stats.put("media_gols_por_jogador", rs.getDouble("media_gols_por_jogador"));
                stats.put("media_socios_por_time", rs.getDouble("media_socios_por_time"));
                stats.put("capacidade_media_estadios", rs.getDouble("capacidade_media_estadios"));
                stats.put("idade_media_tecnicos", rs.getDouble("idade_media_tecnicos"));
                stats.put("media_cartoes_arbitros", rs.getDouble("media_cartoes_arbitros"));
                stats.put("media_faltas_arbitros", rs.getDouble("media_faltas_arbitros"));
            }
        }
        return stats;
    }

    /**
     * Distribuição de jogadores por posição (para gráfico de pizza)
     */
    public List<Map<String, Object>> distribuirJogadoresPorPosicao() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT posicao_jogador, COUNT(*) as total, " +
                "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Jogador), 2) as percentual " +
                "FROM Jogador " +
                "GROUP BY posicao_jogador " +
                "ORDER BY total DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("posicao", rs.getString("posicao_jogador"));
                item.put("total", rs.getInt("total"));
                item.put("percentual", rs.getDouble("percentual"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Distribuição de nacionalidades (para gráfico de barras)
     */
    public List<Map<String, Object>> distribuirNacionalidades() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT nacionalidade, COUNT(*) as total, " +
                "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Jogador), 2) as percentual " +
                "FROM Jogador " +
                "GROUP BY nacionalidade " +
                "ORDER BY total DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nacionalidade", rs.getString("nacionalidade"));
                item.put("total", rs.getInt("total"));
                item.put("percentual", rs.getDouble("percentual"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Top 10 artilheiros (para gráfico de barras)
     */
    public List<Map<String, Object>> topArtilheiros(int limite) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT j.nome, j.gols_temporada_jogador as gols, t.nome as time_nome " +
                "FROM Jogador j " +
                "LEFT JOIN Time t ON j.fk_time = t.id_time " +
                "ORDER BY j.gols_temporada_jogador DESC " +
                "LIMIT ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nome", rs.getString("nome"));
                    item.put("gols", rs.getInt("gols"));
                    item.put("time", rs.getString("time_nome"));
                    dados.add(item);
                }
            }
        }
        return dados;
    }

    /**
     * Estatísticas por posição (média, mediana, moda, variância, desvio padrão)
     */
    public List<Map<String, Object>> estatisticasPorPosicao() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "posicao_jogador, " +
                "COUNT(*) as total, " +
                "AVG(idade) as media_idade, " +
                "AVG(altura) as media_altura, " +
                "AVG(peso) as media_peso, " +
                "AVG(gols_temporada_jogador) as media_gols, " +
                "SUM(gols_temporada_jogador) as total_gols, " +
                "SUM(assistencias) as total_assistencias, " +
                "STDDEV_POP(idade) as desvio_idade, " +
                "STDDEV_POP(altura) as desvio_altura, " +
                "STDDEV_POP(gols_temporada_jogador) as desvio_gols " +
                "FROM Jogador " +
                "GROUP BY posicao_jogador " +
                "ORDER BY total_gols DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("posicao", rs.getString("posicao_jogador"));
                item.put("total", rs.getInt("total"));
                item.put("media_idade", rs.getDouble("media_idade"));
                item.put("media_altura", rs.getDouble("media_altura"));
                item.put("media_peso", rs.getDouble("media_peso"));
                item.put("media_gols", rs.getDouble("media_gols"));
                item.put("total_gols", rs.getInt("total_gols"));
                item.put("total_assistencias", rs.getInt("total_assistencias"));
                item.put("desvio_idade", rs.getDouble("desvio_idade"));
                item.put("desvio_altura", rs.getDouble("desvio_altura"));
                item.put("desvio_gols", rs.getDouble("desvio_gols"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Distribuição de gols por faixa (histograma)
     */
    public List<Map<String, Object>> distribuirGolsPorFaixa() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "CASE " +
                "  WHEN gols_temporada_jogador = 0 THEN '0 gols' " +
                "  WHEN gols_temporada_jogador BETWEEN 1 AND 5 THEN '1-5 gols' " +
                "  WHEN gols_temporada_jogador BETWEEN 6 AND 10 THEN '6-10 gols' " +
                "  WHEN gols_temporada_jogador BETWEEN 11 AND 15 THEN '11-15 gols' " +
                "  WHEN gols_temporada_jogador BETWEEN 16 AND 20 THEN '16-20 gols' " +
                "  ELSE '21+ gols' " +
                "END as faixa_gols, " +
                "COUNT(*) as quantidade " +
                "FROM Jogador " +
                "GROUP BY faixa_gols " +
                "ORDER BY MIN(gols_temporada_jogador)";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("faixa", rs.getString("faixa_gols"));
                item.put("quantidade", rs.getInt("quantidade"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Correlação: Idade vs Gols (gráfico de dispersão/linha)
     */
    public List<Map<String, Object>> correlacaoIdadeGols() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "idade, " +
                "AVG(gols_temporada_jogador) as media_gols, " +
                "COUNT(*) as total_jogadores " +
                "FROM Jogador " +
                "GROUP BY idade " +
                "ORDER BY idade";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("idade", rs.getInt("idade"));
                item.put("media_gols", rs.getDouble("media_gols"));
                item.put("total_jogadores", rs.getInt("total_jogadores"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Top times por gols (gráfico de barras)
     */
    public List<Map<String, Object>> topTimesPorGols(int limite) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "t.nome as time_nome, " +
                "SUM(j.gols_temporada_jogador) as total_gols, " +
                "COUNT(j.id_jogador) as total_jogadores, " +
                "AVG(j.gols_temporada_jogador) as media_gols_por_jogador " +
                "FROM Time t " +
                "LEFT JOIN Jogador j ON t.id_time = j.fk_time " +
                "GROUP BY t.id_time, t.nome " +
                "ORDER BY total_gols DESC " +
                "LIMIT ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("time", rs.getString("time_nome"));
                    item.put("total_gols", rs.getInt("total_gols"));
                    item.put("total_jogadores", rs.getInt("total_jogadores"));
                    item.put("media_gols", rs.getDouble("media_gols_por_jogador"));
                    dados.add(item);
                }
            }
        }
        return dados;
    }

    /**
     * Distribuição de pés dominantes (gráfico de pizza)
     */
    public List<Map<String, Object>> distribuirPesDominantes() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "pe_dominante, " +
                "COUNT(*) as total, " +
                "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Jogador), 2) as percentual " +
                "FROM Jogador " +
                "GROUP BY pe_dominante " +
                "ORDER BY total DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("pe", rs.getString("pe_dominante"));
                item.put("total", rs.getInt("total"));
                item.put("percentual", rs.getDouble("percentual"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Estatísticas de estádios (capacidade)
     */
    public List<Map<String, Object>> estatisticasEstadios() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "e.nome as estadio_nome, " +
                "e.capacidade, " +
                "t.nome as time_nome " +
                "FROM Estadio e " +
                "LEFT JOIN Time t ON e.id_estadio = t.fk_estadio " +
                "ORDER BY e.capacidade DESC " +
                "LIMIT 10";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("estadio", rs.getString("estadio_nome"));
                item.put("capacidade", rs.getInt("capacidade"));
                item.put("time", rs.getString("time_nome"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Correlação: Altura vs Gols de Cabeça (gráfico de dispersão)
     */
    public List<Map<String, Object>> correlacaoAlturaGolsCabeca() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "ROUND(altura, 1) as altura_arredondada, " +
                "AVG(gols_cabeca) as media_gols_cabeca, " +
                "SUM(gols_cabeca) as total_gols_cabeca, " +
                "COUNT(*) as total_jogadores " +
                "FROM Jogador " +
                "GROUP BY altura_arredondada " +
                "ORDER BY altura_arredondada";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("altura", rs.getDouble("altura_arredondada"));
                item.put("media_gols_cabeca", rs.getDouble("media_gols_cabeca"));
                item.put("total_gols_cabeca", rs.getInt("total_gols_cabeca"));
                item.put("total_jogadores", rs.getInt("total_jogadores"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Tendência: Gols e Assistências por Posição (gráfico radar)
     */
    public List<Map<String, Object>> tendenciaGolsAssistenciasPorPosicao() throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "posicao_jogador, " +
                "AVG(gols_temporada_jogador) as media_gols, " +
                "AVG(assistencias) as media_assistencias, " +
                "SUM(gols_temporada_jogador) as total_gols, " +
                "SUM(assistencias) as total_assistencias " +
                "FROM Jogador " +
                "GROUP BY posicao_jogador " +
                "ORDER BY posicao_jogador";
        
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("posicao", rs.getString("posicao_jogador"));
                item.put("media_gols", rs.getDouble("media_gols"));
                item.put("media_assistencias", rs.getDouble("media_assistencias"));
                item.put("total_gols", rs.getInt("total_gols"));
                item.put("total_assistencias", rs.getInt("total_assistencias"));
                dados.add(item);
            }
        }
        return dados;
    }

    /**
     * Times com mais sócios (gráfico de barras)
     */
    public List<Map<String, Object>> topTimesPorSocios(int limite) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        String sql = "SELECT " +
                "nome, " +
                "quant_socios, " +
                "quant_jogadores " +
                "FROM Time " +
                "ORDER BY quant_socios DESC " +
                "LIMIT ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("time", rs.getString("nome"));
                    item.put("socios", rs.getInt("quant_socios"));
                    item.put("jogadores", rs.getInt("quant_jogadores"));
                    dados.add(item);
                }
            }
        }
        return dados;
    }
}


package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewConsultaDAO {

    /**
     * Busca dados da view vw_JogadorDetalhado
     */
    public List<String[]> buscarJogadorDetalhado(String filtroNome, String filtroTime) throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM vw_JogadorDetalhado WHERE 1=1");
        
        if (filtroNome != null && !filtroNome.trim().isEmpty()) {
            sql.append(" AND jogador_nome LIKE ?");
        }
        if (filtroTime != null && !filtroTime.trim().isEmpty()) {
            sql.append(" AND time_nome LIKE ?");
        }
        sql.append(" ORDER BY gols_temporada_jogador DESC");

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + filtroNome + "%");
            }
            if (filtroTime != null && !filtroTime.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + filtroTime + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] resultado = {
                        rs.getString("jogador_nome"),
                        rs.getString("posicao_jogador"),
                        String.valueOf(rs.getInt("jogador_idade")),
                        rs.getString("nacionalidade"),
                        String.valueOf(rs.getInt("gols_temporada_jogador")),
                        String.valueOf(rs.getInt("assistencias")),
                        rs.getString("time_nome"),
                        rs.getString("tecnico_nome"),
                        rs.getString("presidente_nome")
                    };
                    resultados.add(resultado);
                }
            }
        }
        return resultados;
    }

    /**
     * Busca dados da view vw_InfraestruturaTime
     */
    public List<String[]> buscarInfraestruturaTime(String filtroTime) throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM vw_InfraestruturaTime";
        
        if (filtroTime != null && !filtroTime.trim().isEmpty()) {
            sql += " WHERE time_nome LIKE ?";
        }
        sql += " ORDER BY quant_jogadores DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filtroTime != null && !filtroTime.trim().isEmpty()) {
                ps.setString(1, "%" + filtroTime + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] resultado = {
                        rs.getString("time_nome"),
                        String.valueOf(rs.getInt("quant_jogadores")),
                        String.valueOf(rs.getInt("quant_socios")),
                        rs.getString("presidente_nome"),
                        String.valueOf(rs.getInt("meses_no_cargo")),
                        rs.getString("tecnico_nome"),
                        String.valueOf(rs.getInt("quant_time_treinou")),
                        rs.getString("estadio_nome"),
                        String.valueOf(rs.getInt("capacidade")),
                        rs.getString("bairro")
                    };
                    resultados.add(resultado);
                }
            }
        }
        return resultados;
    }

    /**
     * Consulta: Técnicos Desempregados
     */
    public List<String[]> consultarTecnicosDesempregados() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT te.nome as tecnico_nome, te.idade, te.quant_time_treinou " +
                "FROM Tecnico te " +
                "LEFT JOIN Time t ON te.id_tecnico = t.fk_tecnico " +
                "WHERE t.fk_tecnico IS NULL " +
                "ORDER BY te.quant_time_treinou DESC, te.idade DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] resultado = {
                    rs.getString("tecnico_nome"),
                    String.valueOf(rs.getInt("idade")),
                    String.valueOf(rs.getInt("quant_time_treinou"))
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    /**
     * Consulta: Jogadores em Times com Estádios com Capacidade Maior que X
     */
    public List<String[]> consultarJogadoresEstadiosGrandes(int capacidadeMinima) throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome AS jogador_nome, j.posicao_jogador, t.nome AS time_nome, e.capacidade " +
                "FROM Jogador j " +
                "JOIN Time t ON j.fk_time = t.id_time " +
                "JOIN Estadio e ON t.fk_estadio = e.id_estadio " +
                "WHERE e.capacidade > ? AND t.fk_estadio IS NOT NULL " +
                "ORDER BY e.capacidade DESC, j.nome";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, capacidadeMinima);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] resultado = {
                        rs.getString("jogador_nome"),
                        rs.getString("posicao_jogador"),
                        rs.getString("time_nome"),
                        String.valueOf(rs.getInt("capacidade"))
                    };
                    resultados.add(resultado);
                }
            }
        }
        return resultados;
    }

    /**
     * Consulta: Times com Artilheiros
     */
    public List<String[]> consultarTimesComArtilheiros() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT t.nome AS time_nome, t.quant_socios, " +
                "(SELECT j.nome FROM Jogador j WHERE j.fk_time = t.id_time ORDER BY j.gols_temporada_jogador DESC LIMIT 1) AS artilheiro_do_time, " +
                "(SELECT MAX(j.gols_temporada_jogador) FROM Jogador j WHERE j.fk_time = t.id_time) AS gols_artilheiro " +
                "FROM Time t " +
                "ORDER BY t.nome";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] resultado = {
                    rs.getString("time_nome"),
                    String.valueOf(rs.getInt("quant_socios")),
                    rs.getString("artilheiro_do_time"),
                    rs.getString("gols_artilheiro") != null ? rs.getString("gols_artilheiro") : "0"
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    /**
     * Consulta: Estatísticas por Posição (para gráficos)
     */
    public List<String[]> consultarEstatisticasPorPosicao() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.posicao_jogador, " +
                "COUNT(*) as total_jogadores, " +
                "AVG(j.idade) as idade_media, " +
                "AVG(j.altura) as altura_media, " +
                "SUM(j.gols_temporada_jogador) as total_gols, " +
                "SUM(j.assistencias) as total_assistencias " +
                "FROM Jogador j " +
                "GROUP BY j.posicao_jogador " +
                "ORDER BY total_gols DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] resultado = {
                    rs.getString("posicao_jogador"),
                    String.valueOf(rs.getInt("total_jogadores")),
                    String.format("%.1f", rs.getDouble("idade_media")),
                    String.format("%.2f", rs.getDouble("altura_media")),
                    String.valueOf(rs.getInt("total_gols")),
                    String.valueOf(rs.getInt("total_assistencias"))
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    /**
     * Consulta: Times com Estatísticas (para gráficos)
     */
    public List<String[]> consultarTimesComEstatisticas() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT t.nome as time_nome, " +
                "COUNT(j.id_jogador) as total_jogadores, " +
                "SUM(j.gols_temporada_jogador) as total_gols_time, " +
                "AVG(j.idade) as idade_media, " +
                "te.nome as tecnico_nome " +
                "FROM Time t " +
                "LEFT JOIN Jogador j ON t.id_time = j.fk_time " +
                "LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico " +
                "GROUP BY t.id_time, t.nome, te.nome " +
                "ORDER BY total_gols_time DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] resultado = {
                    rs.getString("time_nome"),
                    String.valueOf(rs.getInt("total_jogadores")),
                    String.valueOf(rs.getInt("total_gols_time")),
                    String.format("%.1f", rs.getDouble("idade_media")),
                    rs.getString("tecnico_nome")
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }
}


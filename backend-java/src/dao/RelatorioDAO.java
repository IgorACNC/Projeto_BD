package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    // Relatório: Times com seus jogadores e estatísticas
    public List<String[]> relatorioTimesComJogadores() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT t.nome as time_nome, COUNT(j.id_jogador) as total_jogadores, " +
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

    // Relatório: Estatísticas por posição
    public List<String[]> relatorioEstatisticasPorPosicao() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.posicao_jogador, COUNT(*) as total_jogadores, " +
                "AVG(j.idade) as idade_media, AVG(j.altura) as altura_media, " +
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

    // Relatório: Times com maiores estádios
    public List<String[]> relatorioTimesComEstadios() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT t.nome as time_nome, e.nome as estadio_nome, e.capacidade, " +
                "e.bairro, e.rua, e.numero, p.nome as presidente_nome " +
                "FROM Time t " +
                "LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio " +
                "LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente " +
                "WHERE e.capacidade IS NOT NULL " +
                "ORDER BY e.capacidade DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        rs.getString("time_nome"),
                        rs.getString("estadio_nome"),
                        String.valueOf(rs.getInt("capacidade")),
                        rs.getString("bairro"),
                        rs.getString("rua") + ", " + rs.getInt("numero"),
                        rs.getString("presidente_nome")
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    // Relatório: Técnicos mais experientes
    public List<String[]> relatorioTecnicosExperiencia() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT te.nome as tecnico_nome, te.idade, te.quant_time_treinou, " +
                "t.nome as time_atual, p.nome as presidente_nome " +
                "FROM Tecnico te " +
                "LEFT JOIN Time t ON te.id_tecnico = t.fk_tecnico " +
                "LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente " +
                "ORDER BY te.quant_time_treinou DESC, te.idade DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        rs.getString("tecnico_nome"),
                        String.valueOf(rs.getInt("idade")),
                        String.valueOf(rs.getInt("quant_time_treinou")),
                        rs.getString("time_atual"),
                        rs.getString("presidente_nome")
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    // Relatório: Jogadores por nacionalidade
    public List<String[]> relatorioJogadoresPorNacionalidade() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nacionalidade, COUNT(*) as total_jogadores, " +
                "AVG(j.idade) as idade_media, " +
                "SUM(j.gols_temporada_jogador) as total_gols, " +
                "AVG(j.gols_temporada_jogador) as media_gols_por_jogador " +
                "FROM Jogador j " +
                "GROUP BY j.nacionalidade " +
                "ORDER BY total_jogadores DESC, total_gols DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        rs.getString("nacionalidade"),
                        String.valueOf(rs.getInt("total_jogadores")),
                        String.format("%.1f", rs.getDouble("idade_media")),
                        String.valueOf(rs.getInt("total_gols")),
                        String.format("%.2f", rs.getDouble("media_gols_por_jogador"))
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }
}

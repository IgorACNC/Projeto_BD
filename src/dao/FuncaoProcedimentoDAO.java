package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncaoProcedimentoDAO {

    /**
     * Executa a função fn_ClassificarDesempenho para um número de gols
     */
    public String classificarDesempenho(int gols) throws SQLException {
        String sql = "SELECT fn_ClassificarDesempenho(?) AS classificacao";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gols);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("classificacao");
                }
            }
        }
        return "Erro ao classificar";
    }

    /**
     * Executa a função fn_CalcularParticipacoesGols
     */
    public int calcularParticipacoesGols(int gols, int assistencias) throws SQLException {
        String sql = "SELECT fn_CalcularParticipacoesGols(?, ?) AS total_participacoes";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gols);
            ps.setInt(2, assistencias);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_participacoes");
                }
            }
        }
        return 0;
    }

    /**
     * Executa o procedimento sp_ContratarTecnico
     */
    public void contratarTecnico(int timeId, int novoTecnicoId) throws SQLException {
        String sql = "CALL sp_ContratarTecnico(?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, timeId);
            cs.setInt(2, novoTecnicoId);
            cs.execute();
        }
    }

    /**
     * Executa o procedimento sp_GerarListaElenco e retorna a lista
     */
    public String gerarListaElenco(int timeId) throws SQLException {
        String sql = "CALL sp_GerarListaElenco(?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, timeId);
            cs.registerOutParameter(2, Types.LONGVARCHAR);
            cs.execute();
            return cs.getString(2);
        }
    }

    /**
     * Busca os logs de jogadores deletados (efeito do trigger)
     */
    public List<String[]> buscarLogsJogadoresDeletados() throws SQLException {
        List<String[]> logs = new ArrayList<>();
        String sql = "SELECT id_log, id_jogador_deletado, nome_jogador, id_time_antigo, data_delecao " +
                "FROM Log_Jogador_Deletado ORDER BY data_delecao DESC";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] log = {
                    String.valueOf(rs.getInt("id_log")),
                    String.valueOf(rs.getInt("id_jogador_deletado")),
                    rs.getString("nome_jogador"),
                    String.valueOf(rs.getInt("id_time_antigo")),
                    rs.getTimestamp("data_delecao").toString()
                };
                logs.add(log);
            }
        }
        return logs;
    }

    /**
     * Busca informações sobre os triggers existentes
     */
    public List<String[]> listarTriggers() throws SQLException {
        List<String[]> triggers = new ArrayList<>();
        String sql = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE, ACTION_STATEMENT " +
                "FROM INFORMATION_SCHEMA.TRIGGERS " +
                "WHERE TRIGGER_SCHEMA = 'Brasileirao_serie_A' " +
                "ORDER BY TRIGGER_NAME";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] trigger = {
                    rs.getString("TRIGGER_NAME"),
                    rs.getString("EVENT_MANIPULATION"),
                    rs.getString("EVENT_OBJECT_TABLE"),
                    rs.getString("ACTION_STATEMENT").substring(0, Math.min(100, rs.getString("ACTION_STATEMENT").length())) + "..."
                };
                triggers.add(trigger);
            }
        }
        return triggers;
    }

    /**
     * Busca informações sobre as funções existentes
     */
    public List<String[]> listarFuncoes() throws SQLException {
        List<String[]> funcoes = new ArrayList<>();
        String sql = "SELECT ROUTINE_NAME, ROUTINE_TYPE, DATA_TYPE " +
                "FROM INFORMATION_SCHEMA.ROUTINES " +
                "WHERE ROUTINE_SCHEMA = 'Brasileirao_serie_A' " +
                "AND ROUTINE_TYPE = 'FUNCTION' " +
                "ORDER BY ROUTINE_NAME";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] funcao = {
                    rs.getString("ROUTINE_NAME"),
                    rs.getString("ROUTINE_TYPE"),
                    rs.getString("DATA_TYPE")
                };
                funcoes.add(funcao);
            }
        }
        return funcoes;
    }

    /**
     * Busca informações sobre os procedimentos existentes
     */
    public List<String[]> listarProcedimentos() throws SQLException {
        List<String[]> procedimentos = new ArrayList<>();
        String sql = "SELECT ROUTINE_NAME, ROUTINE_TYPE " +
                "FROM INFORMATION_SCHEMA.ROUTINES " +
                "WHERE ROUTINE_SCHEMA = 'Brasileirao_serie_A' " +
                "AND ROUTINE_TYPE = 'PROCEDURE' " +
                "ORDER BY ROUTINE_NAME";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String[] procedimento = {
                    rs.getString("ROUTINE_NAME"),
                    rs.getString("ROUTINE_TYPE")
                };
                procedimentos.add(procedimento);
            }
        }
        return procedimentos;
    }
}


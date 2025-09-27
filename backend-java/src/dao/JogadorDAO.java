package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Jogador;

public class JogadorDAO {

    public void inserir(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO Jogador (nome, idade, altura, numero_camisa, quant_times_jogados, gols_temporada_jogador, "
                +
                "pe_dominante, gols_penalti, gols_cabeca, peso, posicao_jogador, quant_jogos, nacionalidade, " +
                "assistencias, fk_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jogador.getNome());
            ps.setInt(2, jogador.getIdade());
            ps.setFloat(3, jogador.getAltura());
            ps.setInt(4, jogador.getNumero_camisa());
            ps.setInt(5, jogador.getQuant_times_jogados());
            ps.setInt(6, jogador.getGols_temporada_jogador());
            ps.setString(7, jogador.getPe_dominante());
            ps.setInt(8, jogador.getGols_penalti());
            ps.setInt(9, jogador.getGols_cabeca());
            ps.setFloat(10, jogador.getPeso());
            ps.setString(11, jogador.getPosicao_jogador());
            ps.setInt(12, jogador.getQuant_jogos());
            ps.setString(13, jogador.getNacionalidade());
            ps.setInt(14, jogador.getAssistencias());
            ps.setInt(15, jogador.getFk_time());

            ps.executeUpdate();
        }
    }

    public List<Jogador> listar() throws SQLException {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogador";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Jogador j = new Jogador();
                j.setId_jogador(rs.getInt("id_jogador"));
                j.setNome(rs.getString("nome"));
                j.setIdade(rs.getInt("idade"));
                j.setAltura(rs.getFloat("altura"));
                j.setNumero_camisa(rs.getInt("numero_camisa"));
                j.setQuant_times_jogados(rs.getInt("quant_times_jogados"));
                j.setGols_temporada_jogador(rs.getInt("gols_temporada_jogador"));
                j.setPe_dominante(rs.getString("pe_dominante"));
                j.setGols_penalti(rs.getInt("gols_penalti"));
                j.setGols_cabeca(rs.getInt("gols_cabeca"));
                j.setPeso(rs.getFloat("peso"));
                j.setPosicao_jogador(rs.getString("posicao_jogador"));
                j.setQuant_jogos(rs.getInt("quant_jogos"));
                j.setNacionalidade(rs.getString("nacionalidade"));
                j.setAssistencias(rs.getInt("assistencias"));
                j.setFk_time(rs.getInt("fk_time"));

                jogadores.add(j);
            }
        }
        return jogadores;
    }

    public Jogador buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Jogador WHERE id_jogador = ?";
        Jogador jogador = null;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jogador = new Jogador();
                    jogador.setId_jogador(rs.getInt("id_jogador"));
                    jogador.setNome(rs.getString("nome"));
                    jogador.setIdade(rs.getInt("idade"));
                    jogador.setAltura(rs.getFloat("altura"));
                    jogador.setNumero_camisa(rs.getInt("numero_camisa"));
                    jogador.setQuant_times_jogados(rs.getInt("quant_times_jogados"));
                    jogador.setGols_temporada_jogador(rs.getInt("gols_temporada_jogador"));
                    jogador.setPe_dominante(rs.getString("pe_dominante"));
                    jogador.setGols_penalti(rs.getInt("gols_penalti"));
                    jogador.setGols_cabeca(rs.getInt("gols_cabeca"));
                    jogador.setPeso(rs.getFloat("peso"));
                    jogador.setPosicao_jogador(rs.getString("posicao_jogador"));
                    jogador.setQuant_jogos(rs.getInt("quant_jogos"));
                    jogador.setNacionalidade(rs.getString("nacionalidade"));
                    jogador.setAssistencias(rs.getInt("assistencias"));
                    jogador.setFk_time(rs.getInt("fk_time"));
                }
            }
        }
        return jogador;
    }

    public void atualizar(Jogador jogador) throws SQLException {
        String sql = "UPDATE Jogador SET nome = ?, idade = ?, altura = ?, numero_camisa = ?, quant_times_jogados = ?, "
                +
                "gols_temporada_jogador = ?, pe_dominante = ?, gols_penalti = ?, gols_cabeca = ?, peso = ?, " +
                "posicao_jogador = ?, quant_jogos = ?, nacionalidade = ?, assistencias = ?, fk_time = ? WHERE id_jogador = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jogador.getNome());
            ps.setInt(2, jogador.getIdade());
            ps.setFloat(3, jogador.getAltura());
            ps.setInt(4, jogador.getNumero_camisa());
            ps.setInt(5, jogador.getQuant_times_jogados());
            ps.setInt(6, jogador.getGols_temporada_jogador());
            ps.setString(7, jogador.getPe_dominante());
            ps.setInt(8, jogador.getGols_penalti());
            ps.setInt(9, jogador.getGols_cabeca());
            ps.setFloat(10, jogador.getPeso());
            ps.setString(11, jogador.getPosicao_jogador());
            ps.setInt(12, jogador.getQuant_jogos());
            ps.setString(13, jogador.getNacionalidade());
            ps.setInt(14, jogador.getAssistencias());
            ps.setInt(15, jogador.getFk_time());
            ps.setInt(16, jogador.getId_jogador());

            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Jogador WHERE id_jogador = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Método com JOIN para buscar jogadores com informações do time
    public List<String[]> listarComInformacoesDoTime() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.id_jogador, j.nome as jogador_nome, j.posicao_jogador, j.numero_camisa, " +
                "j.gols_temporada_jogador, j.assistencias, j.nacionalidade, " +
                "t.nome as time_nome, t.quant_jogadores " +
                "FROM Jogador j " +
                "LEFT JOIN Time t ON j.fk_time = t.id_time " +
                "ORDER BY j.gols_temporada_jogador DESC";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        String.valueOf(rs.getInt("id_jogador")),
                        rs.getString("jogador_nome"),
                        rs.getString("posicao_jogador"),
                        String.valueOf(rs.getInt("numero_camisa")),
                        String.valueOf(rs.getInt("gols_temporada_jogador")),
                        String.valueOf(rs.getInt("assistencias")),
                        rs.getString("nacionalidade"),
                        rs.getString("time_nome"),
                        String.valueOf(rs.getInt("quant_jogadores"))
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    // Método para buscar artilheiros (jogadores com mais gols)
    public List<String[]> buscarArtilheiros() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome as jogador_nome, j.gols_temporada_jogador, j.posicao_jogador, " +
                "t.nome as time_nome, j.nacionalidade " +
                "FROM Jogador j " +
                "LEFT JOIN Time t ON j.fk_time = t.id_time " +
                "WHERE j.gols_temporada_jogador > 0 " +
                "ORDER BY j.gols_temporada_jogador DESC, j.assistencias DESC " +
                "LIMIT 10";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        rs.getString("jogador_nome"),
                        String.valueOf(rs.getInt("gols_temporada_jogador")),
                        rs.getString("posicao_jogador"),
                        rs.getString("time_nome"),
                        rs.getString("nacionalidade")
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }
}
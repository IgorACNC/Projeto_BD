package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Time;

public class TimeDAO {

    public void inserir(Time time) throws SQLException {
        String sql = "INSERT INTO Time (nome, quant_jogadores, quant_socios, fk_tecnico, fk_presidente, fk_estadio) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, time.getNome());
            ps.setInt(2, time.getQuant_jogadores());
            ps.setInt(3, time.getQuant_socios());
            ps.setInt(4, time.getFk_tecnico());
            ps.setInt(5, time.getFk_presidente());
            ps.setInt(6, time.getFk_estadio());
            ps.executeUpdate();
        }
    }

    public List<Time> listar() throws SQLException {
        List<Time> times = new ArrayList<>();
        String sql = "SELECT * FROM Time";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Time t = new Time();
                t.setId_time(rs.getInt("id_time"));
                t.setNome(rs.getString("nome"));
                t.setQuant_jogadores(rs.getInt("quant_jogadores"));
                t.setQuant_socios(rs.getInt("quant_socios"));
                t.setFk_tecnico(rs.getInt("fk_tecnico"));
                t.setFk_presidente(rs.getInt("fk_presidente"));
                t.setFk_estadio(rs.getInt("fk_estadio"));
                times.add(t);
            }
        }
        return times;
    }

    public void atualizar(Time time) throws SQLException {
        String sql = "UPDATE Time SET nome = ?, quant_jogadores = ?, quant_socios = ?, fk_tecnico = ?, fk_presidente = ?, fk_estadio = ? WHERE id_time = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, time.getNome());
            ps.setInt(2, time.getQuant_jogadores());
            ps.setInt(3, time.getQuant_socios());
            ps.setInt(4, time.getFk_tecnico());
            ps.setInt(5, time.getFk_presidente());
            ps.setInt(6, time.getFk_estadio());
            ps.setInt(7, time.getId_time());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Time WHERE id_time = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Time buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Time WHERE id_time = ?";
        Time time = null;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    time = new Time();
                    time.setId_time(rs.getInt("id_time"));
                    time.setNome(rs.getString("nome"));
                    time.setQuant_jogadores(rs.getInt("quant_jogadores"));
                    time.setQuant_socios(rs.getInt("quant_socios"));
                    time.setFk_tecnico(rs.getInt("fk_tecnico"));
                    time.setFk_presidente(rs.getInt("fk_presidente"));
                    time.setFk_estadio(rs.getInt("fk_estadio"));
                }
            }
        }
        return time;
    }

    // Método com JOIN para buscar times com informações completas
    public List<String[]> listarComInformacoesCompletas() throws SQLException {
        List<String[]> resultados = new ArrayList<>();
        String sql = "SELECT t.id_time, t.nome as time_nome, t.quant_jogadores, t.quant_socios, " +
                "te.nome as tecnico_nome, p.nome as presidente_nome, e.nome as estadio_nome, e.capacidade " +
                "FROM Time t " +
                "LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico " +
                "LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente " +
                "LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio";

        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] resultado = {
                        String.valueOf(rs.getInt("id_time")),
                        rs.getString("time_nome"),
                        String.valueOf(rs.getInt("quant_jogadores")),
                        String.valueOf(rs.getInt("quant_socios")),
                        rs.getString("tecnico_nome"),
                        rs.getString("presidente_nome"),
                        rs.getString("estadio_nome"),
                        String.valueOf(rs.getInt("capacidade"))
                };
                resultados.add(resultado);
            }
        }
        return resultados;
    }
}
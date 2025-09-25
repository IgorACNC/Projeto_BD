package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Presidente;

public class PresidenteDAO {

    public void inserir(Presidente presidente) throws SQLException {
        String sql = "INSERT INTO Presidente (nome, idade, quant_titulo, tempo_cargo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, presidente.getNome());
            ps.setInt(2, presidente.getIdade());
            ps.setInt(3, presidente.getQuant_titulo());
            ps.setInt(4, presidente.getTempo_cargo());
            ps.executeUpdate();
        }
    }

    public List<Presidente> listar() throws SQLException {
        List<Presidente> presidentes = new ArrayList<>();
        String sql = "SELECT * FROM Presidente";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Presidente p = new Presidente();
                p.setId_presidente(rs.getInt("id_presidente"));
                p.setNome(rs.getString("nome"));
                p.setIdade(rs.getInt("idade"));
                p.setQuant_titulo(rs.getInt("quant_titulo"));
                p.setTempo_cargo(rs.getInt("tempo_cargo"));
                presidentes.add(p);
            }
        }
        return presidentes;
    }

    public void atualizar(Presidente presidente) throws SQLException {
        String sql = "UPDATE Presidente SET nome = ?, idade = ?, quant_titulo = ?, tempo_cargo = ? WHERE id_presidente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, presidente.getNome());
            ps.setInt(2, presidente.getIdade());
            ps.setInt(3, presidente.getQuant_titulo());
            ps.setInt(4, presidente.getTempo_cargo());
            ps.setInt(5, presidente.getId_presidente());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Presidente WHERE id_presidente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Presidente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Presidente WHERE id_presidente = ?";
        Presidente presidente = null;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    presidente = new Presidente();
                    presidente.setId_presidente(rs.getInt("id_presidente"));
                    presidente.setNome(rs.getString("nome"));
                    presidente.setIdade(rs.getInt("idade"));
                    presidente.setQuant_titulo(rs.getInt("quant_titulo"));
                    presidente.setTempo_cargo(rs.getInt("tempo_cargo"));
                }
            }
        }
        return presidente;
    }
}

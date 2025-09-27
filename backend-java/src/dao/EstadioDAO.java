package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Estadio;

public class EstadioDAO {

    public void inserir(Estadio estadio) throws SQLException {
        String sql = "INSERT INTO Estadio (nome, capacidade, rua, numero, bairro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNome());
            ps.setInt(2, estadio.getCapacidade());
            ps.setString(3, estadio.getRua());
            ps.setInt(4, estadio.getNumero());
            ps.setString(5, estadio.getBairro());
            ps.executeUpdate();
        }
    }

    public List<Estadio> listar() throws SQLException {
        List<Estadio> estadios = new ArrayList<>();
        String sql = "SELECT * FROM Estadio";
        try (Connection conn = ConnectionFactory.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Estadio e = new Estadio();
                e.setId_estadio(rs.getInt("id_estadio"));
                e.setNome(rs.getString("nome"));
                e.setCapacidade(rs.getInt("capacidade"));
                e.setRua(rs.getString("rua"));
                e.setNumero(rs.getInt("numero"));
                e.setBairro(rs.getString("bairro"));
                estadios.add(e);
            }
        }
        return estadios;
    }

    public void atualizar(Estadio estadio) throws SQLException {
        String sql = "UPDATE Estadio SET nome = ?, capacidade = ?, rua = ?, numero = ?, bairro = ? WHERE id_estadio = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estadio.getNome());
            ps.setInt(2, estadio.getCapacidade());
            ps.setString(3, estadio.getRua());
            ps.setInt(4, estadio.getNumero());
            ps.setString(5, estadio.getBairro());
            ps.setInt(6, estadio.getId_estadio());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Estadio WHERE id_estadio = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Estadio buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Estadio WHERE id_estadio = ?";
        Estadio estadio = null;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadio = new Estadio();
                    estadio.setId_estadio(rs.getInt("id_estadio"));
                    estadio.setNome(rs.getString("nome"));
                    estadio.setCapacidade(rs.getInt("capacidade"));
                    estadio.setRua(rs.getString("rua"));
                    estadio.setNumero(rs.getInt("numero"));
                    estadio.setBairro(rs.getString("bairro"));
                }
            }
        }
        return estadio;
    }
}
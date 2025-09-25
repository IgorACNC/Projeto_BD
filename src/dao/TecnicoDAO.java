package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Tecnico;

public class TecnicoDAO {

    public void inserir(Tecnico tecnico) throws SQLException {
        String sql = "INSERT INTO Tecnico (nome, idade, quant_time_treinou) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tecnico.getNome());
            ps.setInt(2, tecnico.getIdade());
            ps.setInt(3, tecnico.getQuant_time_treinou());
            ps.executeUpdate();
        }
    }

    public List<Tecnico> listar() throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        String sql = "SELECT * FROM Tecnico";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Tecnico t = new Tecnico();
                t.setId_tecnico(rs.getInt("id_tecnico"));
                t.setNome(rs.getString("nome"));
                t.setIdade(rs.getInt("idade"));
                t.setQuant_time_treinou(rs.getInt("quant_time_treinou"));
                tecnicos.add(t);
            }
        }
        return tecnicos;
    }

    public void atualizar(Tecnico tecnico) throws SQLException {
        String sql = "UPDATE Tecnico SET nome = ?, idade = ?, quant_time_treinou = ? WHERE id_tecnico = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tecnico.getNome());
            ps.setInt(2, tecnico.getIdade());
            ps.setInt(3, tecnico.getQuant_time_treinou());
            ps.setInt(4, tecnico.getId_tecnico());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Tecnico WHERE id_tecnico = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

public Tecnico buscarPorId(int id) throws SQLException {
    String sql = "SELECT * FROM Tecnico WHERE id_tecnico = ?";
    Tecnico tecnico = null;
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                tecnico = new Tecnico();
                tecnico.setId_tecnico(rs.getInt("id_tecnico"));
                tecnico.setNome(rs.getString("nome"));
                tecnico.setIdade(rs.getInt("idade"));
                tecnico.setQuant_time_treinou(rs.getInt("quant_time_treinou"));
            }
        }
    }
    return tecnico;
}
}
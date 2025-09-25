package dao;

import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Jogador;

public class JogadorDAO {

    public void inserir(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO Jogador (nome, idade, altura, numero_camisa, quant_times_jogados, gols_temporada_jogador, " +
                     "pe_dominante, gols_penalti, gols_cabeca, peso, posicao_jogador, quant_jogos, nacionalidade, " +
                     "assistencias, fk_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, jogador.getNome());
            ps.setInt(2, jogador.getIdade());
            ps.setFloat(3, jogador.getAltura());
            // ... continue preenchendo todos os 15 parâmetros com os getters do objeto jogador
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
                // ... continue populando o objeto com os setters para todos os 16 campos
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
                // ... continue populando todos os outros campos do jogador ...
                jogador.setFk_time(rs.getInt("fk_time"));
            }
        }
    }
    return jogador;
}
}
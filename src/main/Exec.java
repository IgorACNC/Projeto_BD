package main;

import java.util.List;

import dao.EstadioDAO;
import dao.JogadorDAO;
import dao.TecnicoDAO;
import dao.TimeDAO;
import model.Estadio;
import model.Jogador;
import model.Tecnico;
import model.Time;

public class Exec {
    public static void main(String[] args) {

        TecnicoDAO tecnicoDAO = new TecnicoDAO();
        EstadioDAO estadioDAO = new EstadioDAO();
        TimeDAO timeDAO = new TimeDAO();
        JogadorDAO jogadorDAO = new JogadorDAO();

        try {
            // --- GERENCIANDO TÉCNICOS ---
            System.out.println("\n--- LISTANDO TÉCNICOS ---");
            List<Tecnico> tecnicos = tecnicoDAO.listar();
            tecnicos.forEach(System.out::println);

            // Inserção
            /*
            System.out.println("\nInserindo novo técnico...");
            Tecnico novoTecnico = new Tecnico();
            novoTecnico.setNome("Rogério Ceni");
            novoTecnico.setIdade(51);
            novoTecnico.setQuant_time_treinou(5);
            tecnicoDAO.inserir(novoTecnico);
            System.out.println("Técnico inserido com sucesso!");
            */

            // --- GERENCIANDO ESTÁDIOS ---
            System.out.println("\n--- LISTANDO ESTÁDIOS ---");
            List<Estadio> estadios = estadioDAO.listar();
            estadios.forEach(System.out::println);

            //ATUALIZAÇÃO
            /*
            System.out.println("\nAtualizando estádio...");
            Estadio estadioParaAtualizar = new Estadio();
            estadioParaAtualizar.setId_estadio(2); // ID do Maracanã
            estadioParaAtualizar.setNome("Maracanã");
            estadioParaAtualizar.setCapacidade(78840); // Corrigindo a capacidade
            estadioParaAtualizar.setRua("R. Prof. Eurico Rabelo");
            estadioParaAtualizar.setNumero(0);
            estadioParaAtualizar.setBairro("Maracanã");
            estadioDAO.atualizar(estadioParaAtualizar);
            System.out.println("Estádio atualizado com sucesso!");
            */


            
            // --- GERENCIANDO TIMES ---
            
            System.out.println("\n--- LISTANDO TIMES ---");
            List<Time> times = timeDAO.listar();
            times.forEach(System.out::println);

            // EXCLUSÃO 
            /*
            System.out.println("\nExcluindo time...");
            timeDAO.excluir(21); // Supondo que existe um time com ID 21 para teste
            System.out.println("Time excluído com sucesso!");
            */
            
            // --- GERENCIANDO JOGADORES ---
            System.out.println("\n--- LISTANDO JOGADORES ---");
            List<Jogador> jogadores = jogadorDAO.listar();
            jogadores.forEach(System.out::println);

            // INSERÇÃO
            /*
            System.out.println("\nInserindo novo jogador...");
            Jogador novoJogador = new Jogador();
            novoJogador.setNome("Luciano");
            novoJogador.setIdade(31);
            novoJogador.setAltura(1.81f);
            novoJogador.setNumero_camisa(10);
            novoJogador.setQuant_times_jogados(8);
            novoJogador.setGols_temporada_jogador(15);
            novoJogador.setPe_dominante("Esquerdo");
            novoJogador.setGols_penalti(5);
            novoJogador.setGols_cabeca(2);
            novoJogador.setPeso(79.0f);
            novoJogador.setPosicao_jogador("Atacante");
            novoJogador.setQuant_jogos(200);
            novoJogador.setNacionalidade("Brasileiro");
            novoJogador.setAssistencias(10);
            novoJogador.setFk_time(4); // ID do São Paulo
            jogadorDAO.inserir(novoJogador);

            System.out.println("Jogador inserido com sucesso!");
            */


        } catch (Exception e) {
            System.err.println("Ocorreu um erro geral na execução:");
            e.printStackTrace();
        }
    }
}
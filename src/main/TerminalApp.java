package main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import dao.TecnicoDAO;
import model.Tecnico;

import dao.EstadioDAO;
import model.Estadio;

import dao.TimeDAO;
import model.Time;

import dao.JogadorDAO;
import model.Jogador;

public class TerminalApp {

    
    private static final TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private static final EstadioDAO estadioDAO = new EstadioDAO(); 
    private static final TimeDAO timeDAO = new TimeDAO();     
    private static final JogadorDAO jogadorDAO = new JogadorDAO();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            exibirMenuPrincipal();
            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        gerenciarTecnicos(scanner);
                        break;
                    case 2:
                        System.out.println("Gerenciamento de Estádios ainda não implementado.");
                        // gerenciarEstadios(scanner); // Chame aqui o método para estádios
                        break;
                    case 3:
                         System.out.println("Gerenciamento de Times ainda não implementado.");
                        // gerenciarTimes(scanner); // Chame aqui o método para times
                        break;
                    case 4:
                         System.out.println("Gerenciamento de Jogadores ainda não implementado.");
                        // gerenciarJogadores(scanner); // Chame aqui o método para jogadores
                        break;
                    case 0:
                        System.out.println("Obrigado por usar o sistema. Até logo!");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Por favor, digite um número.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n===== BEM-VINDO AO SISTEMA DO BRASILEIRÃO =====");
        System.out.println("1. Gerenciar Técnicos");
        System.out.println("2. Gerenciar Estádios");
        System.out.println("3. Gerenciar Times");
        System.out.println("4. Gerenciar Jogadores");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void gerenciarTecnicos(Scanner scanner) throws SQLException {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Gerenciamento de Técnicos ---");
            System.out.println("1. Inserir novo técnico");
            System.out.println("2. Listar todos os técnicos");
            System.out.println("3. Atualizar um técnico");
            System.out.println("4. Excluir um técnico");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1: // INSERIR
                    System.out.print("Digite o nome do técnico: ");
                    String nome = scanner.nextLine();
                    System.out.print("Digite a idade: ");
                    int idade = Integer.parseInt(scanner.nextLine());
                    System.out.print("Digite a quantidade de times que já treinou: ");
                    int quantTimes = Integer.parseInt(scanner.nextLine());

                    Tecnico novoTecnico = new Tecnico();
                    novoTecnico.setNome(nome);
                    novoTecnico.setIdade(idade);
                    novoTecnico.setQuant_time_treinou(quantTimes);
                    tecnicoDAO.inserir(novoTecnico);
                    System.out.println("Técnico inserido com sucesso!");
                    break;

                case 2: // LISTAR
                    List<Tecnico> tecnicos = tecnicoDAO.listar();
                    System.out.println("\n--- Lista de Técnicos ---");
                    if(tecnicos.isEmpty()) {
                        System.out.println("Nenhum técnico cadastrado.");
                    } else {
                        tecnicos.forEach(System.out::println);
                    }
                    break;
                
                case 3: // ATUALIZAR
                    System.out.print("Digite o ID do técnico que deseja atualizar: ");
                    int idUpdate = Integer.parseInt(scanner.nextLine());
                    Tecnico tecnicoParaAtualizar = tecnicoDAO.buscarPorId(idUpdate);

                    if (tecnicoParaAtualizar == null) {
                        System.out.println("Técnico com ID " + idUpdate + " não encontrado.");
                    } else {
                        System.out.println("Dados atuais: " + tecnicoParaAtualizar);
                        System.out.print("Digite o novo nome (ou deixe em branco para não alterar): ");
                        String novoNome = scanner.nextLine();
                        if (!novoNome.isEmpty()) tecnicoParaAtualizar.setNome(novoNome);

                        System.out.print("Digite a nova idade (ou -1 para não alterar): ");
                        int novaIdade = Integer.parseInt(scanner.nextLine());
                        if (novaIdade != -1) tecnicoParaAtualizar.setIdade(novaIdade);
                        
                        // Continue para os outros campos...

                        tecnicoDAO.atualizar(tecnicoParaAtualizar);
                        System.out.println("Técnico atualizado com sucesso!");
                    }
                    break;

                case 4: // EXCLUIR
                    System.out.print("Digite o ID do técnico que deseja excluir: ");
                    int idDelete = Integer.parseInt(scanner.nextLine());
                    tecnicoDAO.excluir(idDelete);
                    System.out.println("Técnico com ID " + idDelete + " excluído (se existia).");
                    break;
                
                case 0: // VOLTAR
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }
}
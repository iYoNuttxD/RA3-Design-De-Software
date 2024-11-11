package RA3Design;

import RA3Design.crud.ClienteCrud;
import RA3Design.crud.PersonalTrainerCrud;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            // Exibe o menu
            System.out.println("========= Menu =========");
            System.out.println("1 - CRUD de Cliente");
            System.out.println("2 - CRUD de Personal Trainer");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    exibirMenuCrudCliente(scanner);
                    break;
                case 2:
                    exibirMenuCrudPersonalTrainer(scanner);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static void exibirMenuCrudCliente(Scanner scanner) {
        int opcaoCrudCliente;

        do {
            // Menu específico para CRUD de Cliente
            System.out.println("========= CRUD Cliente =========");
            System.out.println("1 - Criar Cliente");
            System.out.println("2 - Ler Clientes");
            System.out.println("3 - Atualizar Cliente");
            System.out.println("4 - Excluir Cliente");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoCrudCliente = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoCrudCliente) {
                case 1:
                    ClienteCrud.createCliente();
                    break;
                case 2:
                    ClienteCrud.readClientes();
                    break;
                case 3:
                    ClienteCrud.updateCliente();
                    break;
                case 4:
                    ClienteCrud.deleteCliente();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoCrudCliente != 0);
    }

    private static void exibirMenuCrudPersonalTrainer(Scanner scanner) {
        int opcaoCrudPersonalTrainer;

        do {
            System.out.println("========= CRUD Personal Trainer =========");
            System.out.println("1 - Criar Personal Trainer");
            System.out.println("2 - Ler Personal Trainers");
            System.out.println("3 - Atualizar Personal Trainer");
            System.out.println("4 - Excluir Personal Trainer");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoCrudPersonalTrainer = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoCrudPersonalTrainer) {
                case 1:
                    PersonalTrainerCrud.createPersonalTrainer();
                    break;
                case 2:
                    PersonalTrainerCrud.readPersonalTrainer();
                    break;
                case 3:
                    PersonalTrainerCrud.updatePersonalTrainer();
                    break;
                case 4:
                    PersonalTrainerCrud.deletePersonalTrainer();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoCrudPersonalTrainer != 0);
    }
}

package RA3Design;

import RA3Design.crud.ClienteCrud;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            // Exibe o menu
            System.out.println("========= Menu =========");
            System.out.println("1 - CRUD de Cliente");
            System.out.println("2 - Outra opção (futuramente)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    exibirMenuCrudCliente(scanner);
                    break;
                case 2:
                    System.out.println("Opção 2 selecionada (em breve).");
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
}


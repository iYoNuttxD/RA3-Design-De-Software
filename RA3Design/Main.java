package RA3Design;

import RA3Design.crud.ClienteCrud;
import RA3Design.crud.PersonalTrainerCrud;
import RA3Design.crud.EquipamentoCrud;
import RA3Design.crud.PlanoTreinoCrud;
import RA3Design.crud.ReservaCrud;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("========= Menu =========");
            System.out.println("1 - CRUD de Cliente");
            System.out.println("2 - CRUD de Personal Trainer");
            System.out.println("3 - CRUD de Equipamento");
            System.out.println("4 - CRUD de Plano de Treino");
            System.out.println("5 - CRUD de Reserva");
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
                case 3:
                    exibirMenuCrudEquipamento(scanner);
                    break;
                case 4:
                    exibirMenuCrudPlanoTreino(scanner);
                    break;
                case 5:
                    exibirMenuCrudReserva(scanner); 
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

    private static void exibirMenuCrudEquipamento(Scanner scanner) {
        int opcaoCrudEquipamento;

        do {
            System.out.println("========= CRUD Equipamento =========");
            System.out.println("1 - Criar Equipamento");
            System.out.println("2 - Ler Equipamentos");
            System.out.println("3 - Atualizar Equipamento");
            System.out.println("4 - Excluir Equipamento");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoCrudEquipamento = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoCrudEquipamento) {
                case 1:
                    EquipamentoCrud.createEquipamento();
                    break;
                case 2:
                    EquipamentoCrud.readEquipamentos();
                    break;
                case 3:
                    EquipamentoCrud.updateEquipamento();
                    break;
                case 4:
                    EquipamentoCrud.deleteEquipamento();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoCrudEquipamento != 0);
    }

    private static void exibirMenuCrudPlanoTreino(Scanner scanner) {
        int opcaoCrudPlanoTreino;

        do {
            System.out.println("========= CRUD Plano de Treino =========");
            System.out.println("1 - Criar Plano de Treino");
            System.out.println("2 - Ler Planos de Treino");
            System.out.println("3 - Atualizar Plano de Treino");
            System.out.println("4 - Excluir Plano de Treino");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoCrudPlanoTreino = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoCrudPlanoTreino) {
                case 1:
                    PlanoTreinoCrud.createPlanoTreino();
                    break;
                case 2:
                    PlanoTreinoCrud.readPlanoTreino();
                    break;
                case 3:
                    PlanoTreinoCrud.updatePlanoTreino();
                    break;
                case 4:
                    PlanoTreinoCrud.deletePlanoTreino();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoCrudPlanoTreino != 0);
    }

    private static void exibirMenuCrudReserva(Scanner scanner) {
        int opcaoCrudReserva;
        do {
            System.out.println("========= CRUD Reserva =========");
            System.out.println("1 - Criar Reserva");
            System.out.println("2 - Ler Reservas");
            System.out.println("3 - Atualizar Reserva");
            System.out.println("4 - Excluir Reserva");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoCrudReserva = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoCrudReserva) {
                case 1:
                    ReservaCrud.createReserva();
                    break;
                case 2:
                    ReservaCrud.readReservas();
                    break;
                case 3:
                    ReservaCrud.updateReserva();
                    break;
                case 4:
                    ReservaCrud.deleteReserva();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoCrudReserva != 0);
    }
}

package RA3Design.crud;

import RA3Design.model.*;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class EquipamentoCrud {
    public static void createEquipamento() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            Equipamento equipamento = new Equipamento();

            System.out.print("Digite o nome do equipamento: ");
            equipamento.setNome(scanner.nextLine());

            System.out.print("Digite a categoria do equipamento: ");
            equipamento.setCategoria(scanner.nextLine());

            // Validação para aceitar apenas true ou false para disponibilidade
            Boolean disponibilidade = null;
            while (disponibilidade == null) {
                System.out.print("Digite a disponibilidade do equipamento (true/false): ");
                String disponibilidadeStr = scanner.nextLine();
                if (disponibilidadeStr.equalsIgnoreCase("true")) {
                    disponibilidade = true;
                } else if (disponibilidadeStr.equalsIgnoreCase("false")) {
                    disponibilidade = false;
                } else {
                    System.out.println("Por favor, digite 'true' ou 'false'.");
                }
            }
            equipamento.setDisponibilidade(disponibilidade);

            entityManager.persist(equipamento);
            entityManager.flush();

            System.out.print("Deseja associar uma reserva ao equipamento? (s/n): ");
            String respostaReserva = scanner.nextLine();
            if (respostaReserva.equalsIgnoreCase("s")) {
                List<Reserva> reservasEquipamento = new ArrayList<>();
                String continuar;
                do {
                    System.out.print("Digite o ID da reserva que deseja associar: ");
                    Long idReserva = scanner.nextLong();
                    scanner.nextLine();  // Limpa o buffer após o nextLong()

                    // Realiza a pesquisa da reserva pelo ID
                    Reserva reserva = findReservaById(entityManager, idReserva);
                    if (reserva != null) {
                        reserva.setEquipamento(equipamento);
                        entityManager.merge(reserva);
                        reservasEquipamento.add(reserva);
                    } else {
                        System.out.println("Reserva não encontrada.");
                    }

                    System.out.print("Deseja associar outra reserva? (s/n): ");
                    continuar = scanner.nextLine();
                } while (continuar.equalsIgnoreCase("s"));

                equipamento.setReservas(reservasEquipamento);
                entityManager.merge(equipamento);
            }

            transaction.commit();

        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException nestedException) {
                    nestedException.printStackTrace();
                }
            }
            throw exception;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void updateEquipamento() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Mudança para buscar equipamento por ID em vez de nome
            System.out.print("Digite o ID do equipamento a ser atualizado: ");
            Long idEquipamento = scanner.nextLong();
            scanner.nextLine();  // Limpa o buffer

            Equipamento equipamento = findEquipamentoById(entityManager, idEquipamento);

            if (equipamento != null) {
                System.out.println("Equipamento encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Nome atual: " + equipamento.getNome() + ". Novo nome (ou pressione Enter para manter): ");
                String nome = scanner.nextLine();
                if (!nome.isEmpty()) {
                    equipamento.setNome(nome);
                }

                System.out.print("Categoria atual: " + equipamento.getCategoria() + ". Nova categoria (ou pressione Enter para manter): ");
                String categoria = scanner.nextLine();
                if (!categoria.isEmpty()) {
                    equipamento.setCategoria(categoria);
                }

                System.out.print("Disponibilidade atual: " + equipamento.getDisponibilidade() + ". Nova disponibilidade (true/false) (ou pressione Enter para manter): ");
                String disponibilidadeStr = scanner.nextLine();
                if (!disponibilidadeStr.isEmpty()) {
                    Boolean novaDisponibilidade = Boolean.parseBoolean(disponibilidadeStr);
                    equipamento.setDisponibilidade(novaDisponibilidade);
                }

                // Atualização de reservas associadas
                System.out.print("Deseja atualizar as reservas associadas ao equipamento? (s/n): ");
                String respostaReserva = scanner.nextLine();
                if (respostaReserva.equalsIgnoreCase("s")) {
                    List<Reserva> reservasEquipamento = new ArrayList<>();
                    String continuar;
                    do {
                        System.out.print("Digite o ID da reserva que deseja associar: ");
                        Long idReserva = scanner.nextLong();
                        scanner.nextLine();  // Limpa o buffer

                        Reserva reserva = findReservaById(entityManager, idReserva);
                        if (reserva != null) {
                            reserva.setEquipamento(equipamento);
                            entityManager.merge(reserva);
                            reservasEquipamento.add(reserva);
                        } else {
                            System.out.println("Reserva não encontrada.");
                        }

                        System.out.print("Deseja associar outra reserva? (s/n): ");
                        continuar = scanner.nextLine();
                    } while (continuar.equalsIgnoreCase("s"));

                    equipamento.setReservas(reservasEquipamento);
                }

                transaction.commit();
                System.out.println("Equipamento atualizado com sucesso!");

            } else {
                System.out.println("Equipamento não encontrado.");
            }

        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException nestedException) {
                    nestedException.printStackTrace();
                }
            }
            throw exception;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void deleteEquipamento() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Mudança para buscar equipamento por ID em vez de nome
            System.out.print("Digite o ID do equipamento a ser excluído: ");
            Long idEquipamento = scanner.nextLong();
            scanner.nextLine();  // Limpa o buffer

            Equipamento equipamento = findEquipamentoById(entityManager, idEquipamento);

            if (equipamento == null) {
                System.out.println("Equipamento não encontrado.");
                return;
            }

            // Verificar se o equipamento está associado a alguma reserva
            if (equipamento.getReservas() != null && !equipamento.getReservas().isEmpty()) {
                System.out.println("O equipamento possui reservas associadas. Exclua as reservas antes de excluir o equipamento.");
                return;
            }

            System.out.println("Equipamento encontrado: " + equipamento.getNome());
            System.out.print("Tem certeza que deseja excluir este equipamento? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(equipamento);
                transaction.commit();
                System.out.println("Equipamento excluído com sucesso!");
            } else {
                System.out.println("Exclusão cancelada.");
            }

        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException nestedException) {
                    nestedException.printStackTrace();
                }
            }
            throw exception;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void readEquipamentos() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            List<Equipamento> equipamentos = findAllEquipamentos(entityManager);

            if (equipamentos == null || equipamentos.isEmpty()) {
                System.out.println("Nenhum equipamento encontrado.");
            } else {
                System.out.println("Lista de Equipamentos:");
                for (Equipamento equipamento : equipamentos) {
                    System.out.println("=======================================");
                    System.out.println("Nome: " + equipamento.getNome());
                    System.out.println("Categoria: " + equipamento.getCategoria());
                    System.out.println("Disponibilidade: " + equipamento.getDisponibilidade());
                    if (equipamento.getReservas() != null && !equipamento.getReservas().isEmpty()) {
                        System.out.println("Reservas: ");
                        for (Reserva reserva : equipamento.getReservas()) {
                            System.out.println("Data: " + reserva.getDataReserva());
                            System.out.println("Hora: " + reserva.getHoraReserva());
                        }
                    } else {
                        System.out.println("Reservas: Não atribuídas.");
                    }
                    System.out.println("=======================================");
                }
            }

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    // Método auxiliar para buscar Equipamento por ID
    private static Equipamento findEquipamentoById(EntityManager entityManager, Long idEquipamento) {
        return entityManager.find(Equipamento.class, idEquipamento);
    }

    // Método auxiliar para buscar todas as reservas associadas a um equipamento
    private static Reserva findReservaById(EntityManager entityManager, Long idReserva) {
        return entityManager.find(Reserva.class, idReserva);
    }

    // Método auxiliar para listar todos os equipamentos
    private static List<Equipamento> findAllEquipamentos(EntityManager entityManager) {
        String jpql = "SELECT e FROM Equipamento e";
        return entityManager.createQuery(jpql, Equipamento.class).getResultList();
    }
}

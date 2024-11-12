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

        boolean disponibilidadeValida = false;
        while (!disponibilidadeValida) {
            System.out.print("Digite a disponibilidade do equipamento (true/false): ");
            String disponibilidadeStr = scanner.nextLine();
            if (disponibilidadeStr.equalsIgnoreCase("true") || disponibilidadeStr.equalsIgnoreCase("false")) {
                equipamento.setDisponibilidade(Boolean.parseBoolean(disponibilidadeStr));
                disponibilidadeValida = true;
            } else {
                System.out.println("Entrada inválida. Digite 'true' ou 'false'.");
            }
        }

        entityManager.persist(equipamento);
        entityManager.flush();

        System.out.print("Deseja associar uma reserva ao equipamento? (s/n): ");
        String respostaReserva = scanner.nextLine();
        if (respostaReserva.equalsIgnoreCase("s")) {
            Reserva reserva = buscarReservaPelosDados(scanner, entityManager);
            if (reserva != null) {
                reserva.setEquipamento(equipamento);
                entityManager.merge(reserva);
            }
        }

        transaction.commit();

    } catch (RuntimeException exception) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        throw exception;
    } finally {
        if (entityManager != null) {
            entityManager.close();
        }
    }
}

// Método auxiliar para buscar reserva existente pelos dados inseridos pelo usuário
private static Reserva buscarReservaPelosDados(Scanner scanner, EntityManager entityManager) {
    System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
    String dataReservaStr = scanner.nextLine();
    System.out.print("Digite a hora da reserva (HH:mm): ");
    String horaReservaStr = scanner.nextLine();

    // Query para buscar a reserva
    TypedQuery<Reserva> query = entityManager.createQuery(
            "SELECT r FROM Reserva r WHERE r.dataReserva = :dataReserva AND r.horaReserva = :horaReserva",
            Reserva.class
    );
    query.setParameter("dataReserva", parseDate(dataReservaStr));
    query.setParameter("horaReserva", parseTime(horaReservaStr));

    try {
        return query.getSingleResult();
    } catch (NoResultException e) {
        System.out.println("Reserva não encontrada.");
        return null;
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

        System.out.print("Digite o nome do equipamento a ser atualizado: ");
        String nomeEquipamento = scanner.nextLine();

        Equipamento equipamento = findEquipamentoByNome(entityManager, nomeEquipamento);

        if (equipamento != null) {
            System.out.println("Equipamento encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

            // Atualização das propriedades do equipamento
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
                equipamento.setDisponibilidade(Boolean.parseBoolean(disponibilidadeStr));
            }

            // Atualizando as reservas
            System.out.print("Deseja atualizar as reservas do equipamento? (s/n): ");
            String respostaReserva = scanner.nextLine();
            if (respostaReserva.equalsIgnoreCase("s")) {
                List<Reserva> reservasEquipamento = equipamento.getReservas();
                System.out.print("Deseja adicionar uma nova reserva? (s/n): ");
                String adicionarReserva = scanner.nextLine();
                if (adicionarReserva.equalsIgnoreCase("s")) {
                    Reserva reserva = buscarReservaPelosDados(scanner, entityManager);
                    if (reserva != null) {
                        reservasEquipamento.add(reserva);
                    }
                }

                System.out.print("Deseja remover alguma reserva existente? (s/n): ");
                String removerReserva = scanner.nextLine();
                if (removerReserva.equalsIgnoreCase("s")) {
                    System.out.print("Digite o ID da reserva a ser removida: ");
                    Long idReserva = scanner.nextLong();
                    reservasEquipamento.removeIf(reserva -> reserva.getId().equals(idReserva));
                }

                equipamento.setReservas(reservasEquipamento);
                entityManager.merge(equipamento);
            }

            transaction.commit();
            System.out.println("Equipamento atualizado com sucesso!");
        } else {
            System.out.println("Equipamento não encontrado.");
        }

    } catch (RuntimeException exception) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
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

        // Solicitar que o usuário selecione uma categoria
        System.out.print("Digite a categoria do equipamento: ");
        String categoriaEquipamento = scanner.nextLine();

        // Buscar equipamentos pela categoria
        List<Equipamento> equipamentos = findEquipamentosByCategoria(entityManager, categoriaEquipamento);

        if (equipamentos.isEmpty()) {
            System.out.println("Nenhum equipamento encontrado para essa categoria.");
            return;
        }

        // Exibir lista de equipamentos com nome e ID
        System.out.println("Equipamentos disponíveis na categoria " + categoriaEquipamento + ":");
        for (Equipamento equipamento : equipamentos) {
            System.out.println("ID: " + equipamento.getId() + " - Nome: " + equipamento.getNome());
        }

        // Solicitar que o usuário escolha um ID de equipamento
        System.out.print("Digite o ID do equipamento que deseja excluir: ");
        int equipamentoId = scanner.nextInt();
        scanner.nextLine(); // Consumir o newline

        // Buscar o equipamento pelo ID informado
        Equipamento equipamento = entityManager.find(Equipamento.class, equipamentoId);

        if (equipamento == null || !equipamento.getCategoria().equals(categoriaEquipamento)) {
            System.out.println("Equipamento não encontrado.");
            return;
        }

        // Verificar se o equipamento está associado a alguma reserva
        if (equipamento.getReservas() != null && !equipamento.getReservas().isEmpty()) {
            System.out.println("O equipamento possui reservas associadas. Exclua as reservas antes de excluir o equipamento.");
            return;
        }

        // Confirmar a exclusão
        System.out.println("Equipamento encontrado: " + equipamento.getNome() + " (ID: " + equipamento.getId() + ")");
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

// Método auxiliar para buscar equipamentos pela categoria
private static List<Equipamento> findEquipamentosByCategoria(EntityManager entityManager, String categoria) {
    Query query = entityManager.createQuery("SELECT e FROM Equipamento e WHERE e.categoria = :categoria");
    query.setParameter("categoria", categoria);
    return query.getResultList();
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

package RA3Design.crud;

import RA3Design.model.*;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ReservaCrud {
    public static void createReserva() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            Reserva reserva = new Reserva();

            Date dataReserva = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            while (dataReserva == null) {
                System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                String dataReservaStr = scanner.nextLine();
                try {
                    dataReserva = sdf.parse(dataReservaStr);
                    reserva.setDataReserva(dataReserva);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data da reserva. Formato esperado: dd/MM/yyyy");
                }
            }

            Date horaReserva = null;
            SimpleDateFormat shf = new SimpleDateFormat("HH:mm");
            shf.setLenient(false);
            while (horaReserva == null) {
                System.out.print("Digite a hora da reserva (HH:mm): ");
                String horaReservaStr = scanner.nextLine();
                try {
                    horaReserva = shf.parse(horaReservaStr);
                    reserva.setHoraReserva(horaReserva);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a hora da reserva. Formato esperado: HH:mm");
                }
            }

            System.out.print("Digite o nome do cliente: ");
            String nomeCliente = scanner.nextLine();

            System.out.print("Digite o e-mail do cliente: ");
            String email = scanner.nextLine();

            System.out.print("Digite a categoria do equipamento para reserva: ");
            String nomeEquipamento = scanner.nextLine();

            System.out.print("Digite a categoria do equipamento para reserva: ");
            String catEquipamento = scanner.nextLine();

            Reserva findReserva = findReservaByDataHora(entityManager, dataReserva, horaReserva);
            Equipamento findEquipamento = findEquipamentoByNomeCategoria(entityManager, nomeEquipamento, catEquipamento);
            Cliente findCliente = findClienteByNomeEmail(entityManager, nomeCliente, email);

            if (findReserva == null && findEquipamento != null && findCliente != null){
                entityManager.persist(reserva);
                entityManager.merge(findEquipamento);
                entityManager.merge(findCliente);
                entityManager.flush();
                transaction.commit();
            } else {
                System.out.println("Erro ao criar reserva! Tente adicionar um cliente existente ao um horário que ainda não foi reservado!");
            }

            entityManager.persist(reserva);
            entityManager.flush();
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

    public static void readReservas() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            List<Reserva> reservas = findAllReservas(entityManager);

            if (reservas == null || reservas.isEmpty()) {
                System.out.println("Nenhuma reserva encontrada.");
            } else {
                System.out.println("Lista de reservas:");
                for (Reserva reserva : reservas) {
                    System.out.println("=======================================");
                    System.out.println("Id: " + reserva.getId());
                    System.out.println("Data: " + reserva.getDataReserva());
                    System.out.println("Hora: " + reserva.getHoraReserva());
                    System.out.println("Nome cliente: " + reserva.getCliente().getNome());
                    System.out.println("Email cliente: " + reserva.getCliente().getEmail());
                    System.out.println("Equipamento: " + reserva.getEquipamento().getNome());
                    System.out.println("Categoria equipamento: " + reserva.getEquipamento().getCategoria());
                    System.out.println("=======================================");
                }
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void updateReserva() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o id da reserva a ser atualizada: ");
            int idReserva = Integer.parseInt(scanner.nextLine());

            Reserva reserva = findReservaById(entityManager, idReserva);

            if (reserva != null) {
                System.out.println("Reserva encontrada. Atualize as informações ou pressione Enter para manter o valor atual.");

                Date dataReserva = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                while (dataReserva == null) {
                    System.out.print("Data atual: " + reserva.getDataReserva() + ". Nova data (ou pressione Enter para manter): ");
                    String dataReservaStr = scanner.nextLine();
                    try {
                        dataReserva = sdf.parse(dataReservaStr);
                        reserva.setDataReserva(dataReserva);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a data da reserva. Formato esperado: dd/MM/yyyy");
                    }
                }

                Date horaReserva = null;
                SimpleDateFormat shf = new SimpleDateFormat("HH:mm");
                shf.setLenient(false);
                while (horaReserva == null) {
                    System.out.print("Hora atual: " + reserva.getHoraReserva() + ". Nova hora (ou pressione Enter para manter): ");
                    String horaReservaStr = scanner.nextLine();
                    try {
                        horaReserva = shf.parse(horaReservaStr);
                        reserva.setHoraReserva(horaReserva);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a hora da reserva. Formato esperado: HH:mm");
                    }
                }
                transaction.commit();
                System.out.println("Reserva atualizada com sucesso!");

            } else {
                System.out.println("Reserva não encontrada.");
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

    public static void deleteReserva() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o id da reserva a ser excluída: ");
            int idReserva = Integer.parseInt(scanner.nextLine());

            Reserva reserva = findReservaById(entityManager, idReserva);

            if (reserva == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            System.out.println("Reserva encontrada: " + reserva.getDataReserva() + ", " + reserva.getHoraReserva() + " - " + reserva.getCliente().getNome());
            System.out.print("Tem certeza que deseja excluir esta reserva? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(reserva);
                transaction.commit();
                System.out.println("Reserva excluída com sucesso!");
            } else {
                System.out.println("Reserva cancelada.");
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

    private static Reserva findReservaByDataHora(EntityManager entityManager, Date dataReserva, Date horaReserva) {
        Query query = entityManager.createQuery("SELECT r FROM Reserva r WHERE r.dataReserva = :dataReserva AND " +
                "r.horaReserva = :horaReserva");
        query.setParameter("dataReserva", dataReserva);
        query.setParameter("horaReserva", horaReserva);
        try {
            return (Reserva) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Reserva findReservaById(EntityManager entityManager, int idReserva) {
        Query query = entityManager.createQuery("SELECT r FROM Reserva r WHERE r.idReserva = :idReserva");
        query.setParameter("dataReserva", idReserva);
        try {
            return (Reserva) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Cliente findClienteByNomeEmail(EntityManager entityManager, String nome, String email) {
        Query query = entityManager.createQuery("SELECT c FROM Cliente c WHERE c.nome = :nome AND c.email = :email");
        query.setParameter("nome", nome);
        query.setParameter("email", email);
        try {
            return (Cliente) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Equipamento findEquipamentoByNomeCategoria(EntityManager entityManager, String Nome, String Categoria) {
        Query query = entityManager.createQuery("SELECT e FROM Equipamento e WHERE e.nome = :nome AND " +
                "e.categoria = :categoria");
        query.setParameter("nome", Nome);
        query.setParameter("categoria", Categoria);
        try {
            return (Equipamento) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static List<Reserva> findAllReservas(EntityManager entityManager) {
        TypedQuery<Reserva> query = entityManager.createQuery("SELECT r FROM Reserva r", Reserva.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }
}

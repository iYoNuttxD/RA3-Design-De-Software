package RA3Design.crud;

import RA3Design.model.*;
import jakarta.persistence.*;

import java.text.ParseException;
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
                String disponibilidadeStr = scanner.nextLine().trim();

                if (disponibilidadeStr.equalsIgnoreCase("true") || disponibilidadeStr.equalsIgnoreCase("false")) {
                    equipamento.setDisponibilidade(Boolean.parseBoolean(disponibilidadeStr));
                    disponibilidadeValida = true;
                } else {
                    System.out.println("Entrada inválida. Por favor, insira 'true' ou 'false'.");
                }
            }

            entityManager.persist(equipamento);
            entityManager.flush();

            System.out.print("Deseja associar reservas ao equipamento? (s/n): ");
            String respostaReserva = scanner.nextLine();
            if (respostaReserva.equalsIgnoreCase("s")) {
                List<Reserva> reservasEquipamento = new ArrayList<>();
                String continuar;
                do {
                    Date dataReserva = null;
                    while (dataReserva == null) {
                        System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                        String dataReservaStr = scanner.nextLine();
                        try {
                            dataReserva = new SimpleDateFormat("dd/MM/yyyy").parse(dataReservaStr);
                        } catch (ParseException e) {
                            System.out.println("Erro ao converter data da reserva. Tente novamente.");
                        }
                    }

                    Date horaReserva = null;
                    while (horaReserva == null) {
                        System.out.print("Digite a hora da reserva (HH:mm): ");
                        String horaReservaStr = scanner.nextLine();
                        try {
                            horaReserva = new SimpleDateFormat("HH:mm").parse(horaReservaStr);
                        } catch (ParseException e) {
                            System.out.println("Erro ao converter hora da reserva. Tente novamente.");
                        }
                    }

                    Reserva reserva = findReservaByDataHora(entityManager, dataReserva, horaReserva);
                    if (reserva != null) {
                        reserva.setEquipamento(equipamento);
                        reservasEquipamento.add(reserva);
                        System.out.println("Reserva associada com sucesso!");
                    } else {
                        System.out.println("Reserva com a data e hora fornecidas não encontrada.");
                    }

                    System.out.print("Deseja associar outra reserva? (s/n): ");
                    continuar = scanner.nextLine();
                } while (continuar.equalsIgnoreCase("s"));

                equipamento.setReservas(reservasEquipamento);
                for (Reserva reserva : reservasEquipamento) {
                    entityManager.merge(reserva);
                }
            }

            transaction.commit();
            System.out.println("Equipamento criado com sucesso!");

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

            System.out.print("Digite o nome do equipamento a ser atualizado: ");
            String nomeEquipamento = scanner.nextLine();

            System.out.print("Digite a categoria do equipamento a ser atualizado: ");
            String categoriaEquipamento = scanner.nextLine();

            Equipamento equipamento = findEquipamentoByNomeCategoria(entityManager, nomeEquipamento, categoriaEquipamento);

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

                boolean disponibilidadeValida = false;
                while (!disponibilidadeValida) {
                    System.out.print("Disponibilidade atual: " + equipamento.isDisponibilidade() + ". Nova disponibilidade (true/false ou pressione Enter para manter): ");
                    String disponibilidadeStr = scanner.nextLine().trim();

                    if (disponibilidadeStr.isEmpty()) {
                        disponibilidadeValida = true;
                    } else if (disponibilidadeStr.equalsIgnoreCase("true") || disponibilidadeStr.equalsIgnoreCase("false")) {
                        equipamento.setDisponibilidade(Boolean.parseBoolean(disponibilidadeStr));
                        disponibilidadeValida = true;
                    } else {
                        System.out.println("Entrada inválida. Por favor, insira 'true' ou 'false'.");
                    }
                }

                entityManager.merge(equipamento);

                System.out.print("Deseja atualizar as reservas do equipamento? (s/n): ");
                String respostaReserva = scanner.nextLine();
                if (respostaReserva.equalsIgnoreCase("s")) {
                    List<Reserva> reservasEquipamento = equipamento.getReservas();

                    if (reservasEquipamento.isEmpty()) {
                        System.out.println("Este equipamento não possui reservas associadas.");
                    } else {
                        for (Reserva reserva : reservasEquipamento) {
                            System.out.print("Data atual: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(reserva.getDataReserva())
                                    + ". Nova data (ou pressione Enter para manter): ");
                            String dataReservaStr = scanner.nextLine();
                            if (!dataReservaStr.isEmpty()) {
                                try {
                                    Date novaData = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataReservaStr);
                                    reserva.setDataReserva(novaData);
                                } catch (java.text.ParseException e) {
                                    System.out.println("Erro ao converter a data.");
                                }
                            }

                            System.out.print("Hora atual: " + new java.text.SimpleDateFormat("HH:mm").format(reserva.getHoraReserva())
                                    + ". Nova hora (ou pressione Enter para manter): ");
                            String horaReservaStr = scanner.nextLine();
                            if (!horaReservaStr.isEmpty()) {
                                try {
                                    Date novaHora = new java.text.SimpleDateFormat("HH:mm").parse(horaReservaStr);
                                    reserva.setHoraReserva(novaHora);
                                } catch (java.text.ParseException e) {
                                    System.out.println("Erro ao converter a hora.");
                                }
                            }

                            entityManager.merge(reserva);
                            System.out.println("Reserva atualizada com sucesso!");
                        }
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de reserva ao equipamento? (s/n): ");
                String respostaReserva1 = scanner.nextLine();
                while (respostaReserva1.equalsIgnoreCase("s")) {
                    System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                    String dataReservaStr = scanner.nextLine();

                    System.out.print("Digite a hora da reserva (HH:mm): ");
                    String horaReservaStr = scanner.nextLine();

                    Date dataReserva = null;
                    Date horaReserva = null;
                    try {
                        java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                        dataReserva = sdfData.parse(dataReservaStr);
                        horaReserva = sdfHora.parse(horaReservaStr);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter data ou hora da reserva.");
                    }

                    Reserva reserva = findReservaByDataHora(entityManager, dataReserva, horaReserva);
                    if (reserva != null) {
                        equipamento.getReservas().add(reserva);
                        reserva.setEquipamento(equipamento);
                        entityManager.merge(equipamento);
                        entityManager.merge(reserva);

                        System.out.println("Relacionamento de reserva adicionado com sucesso!");
                    } else {
                        System.out.println("Reserva com os parâmetros fornecidos não encontrada.");
                    }

                    System.out.print("Deseja adicionar outra reserva? (s/n): ");
                    respostaReserva1 = scanner.nextLine();
                }

                System.out.print("Deseja remover uma reserva do equipamento? (s/n): ");
                String respostaRemoverReserva = scanner.nextLine();
                while (respostaRemoverReserva.equalsIgnoreCase("s")) {
                    System.out.print("Digite a data da reserva que deseja remover (dd/MM/yyyy): ");
                    String dataReservaRemoverStr = scanner.nextLine();

                    System.out.print("Digite a hora da reserva que deseja remover (HH:mm): ");
                    String horaReservaRemoverStr = scanner.nextLine();

                    Date dataReservaRemover = null;
                    Date horaReservaRemover = null;
                    try {
                        java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                        dataReservaRemover = sdfData.parse(dataReservaRemoverStr);
                        horaReservaRemover = sdfHora.parse(horaReservaRemoverStr);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter data ou hora da reserva.");
                    }

                    Reserva reserva = findReservaByDataHora(entityManager, dataReservaRemover, horaReservaRemover);
                    if (reserva != null && equipamento.getReservas().contains(reserva)) {
                        equipamento.getReservas().remove(reserva);
                        reserva.setEquipamento(null);

                        entityManager.merge(equipamento);
                        entityManager.merge(reserva);

                        System.out.println("Reserva removida com sucesso!");
                    } else {
                        System.out.println("Reserva com os parâmetros fornecidos não encontrada ou não está associada a este equipamento.");
                    }

                    System.out.print("Deseja remover outra reserva? (s/n): ");
                    respostaRemoverReserva = scanner.nextLine();
                }

                entityManager.merge(equipamento);
                transaction.commit();
                System.out.println("Equipamento atualizado com sucesso!");
            } else {
                System.out.println("Equipamento com o nome e categoria fornecidos não encontrado.");
            }

        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Erro durante a transação: " + exception.getMessage());
            exception.printStackTrace();
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

            System.out.print("Digite o nome do equipamento a ser excluído: ");
            String nomeEquipamento = scanner.nextLine();

            System.out.print("Digite a categoria do equipamento a ser excluído: ");
            String categoriaEquipamento = scanner.nextLine();

            Equipamento equipamento = findEquipamentoByNomeCategoria(entityManager, nomeEquipamento, categoriaEquipamento);

            if (equipamento == null) {
                System.out.println("Equipamento não encontrado.");
                return;
            }

            if (equipamento.getReservas() != null && !equipamento.getReservas().isEmpty()) {
                System.out.println("Este equipamento não pode ser excluído porque possui reservas associadas.");
                return;
            }

            System.out.println("Equipamento encontrado: " + equipamento.getNome() + " - " + equipamento.getCategoria());
            System.out.print("Tem certeza que deseja excluir este equipamento? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(entityManager.contains(equipamento) ? equipamento : entityManager.merge(equipamento));
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
                    System.out.println("Disponibilidade: " + (equipamento.isDisponibilidade() ? "Disponível" : "Indisponível"));

                    if (equipamento.getReservas() != null && !equipamento.getReservas().isEmpty()) {
                        System.out.println("Reservas: ");
                        for (Reserva reserva : equipamento.getReservas()) {
                            System.out.println("Data da Reserva: " + reserva.getDataReserva());
                            System.out.println("Hora da Reserva: " + reserva.getHoraReserva());
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

    private static List<Equipamento> findAllEquipamentos(EntityManager entityManager) {
        TypedQuery<Equipamento> query = entityManager.createQuery("SELECT e FROM Equipamento e", Equipamento.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
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
}



package RA3Design.crud;

import RA3Design.model.*;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            while (dataReserva == null) {
                System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                String dataReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    dataReserva = sdfData.parse(dataReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter data da reserva.");
                }
            }

            Date horaReserva = null;
            while (horaReserva == null) {
                System.out.print("Digite a hora da reserva (HH:mm): ");
                String horaReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                    horaReserva = sdfHora.parse(horaReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter hora da reserva.");
                }
            }

            entityManager.persist(reserva);
            entityManager.flush();

            System.out.print("Deseja associar um cliente a uma reserva? (s/n): ");
            String respostaCliente = scanner.nextLine();
            if (respostaCliente.equalsIgnoreCase("s")) {
                System.out.print("Digite o nome do cliente para associar: ");
                String nomeCliente = scanner.nextLine();

                System.out.print("Digite o e-mail do cliente para associar: ");
                String emailCliente = scanner.nextLine();

                Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);

                if (cliente == null) {
                    System.out.println("Cliente com os parâmetros fornecidos não encontrado.");
                } else {
                    cliente.getReservas().add(reserva);
                    reserva.setCliente(cliente);
                    entityManager.merge(cliente);
                    entityManager.merge(reserva);
                    System.out.println("Cliente associado com sucesso!");
                }
            }

            System.out.print("Deseja associar um equipamento a uma reserva? (s/n): ");
            String respostaEquipamento = scanner.nextLine();
            if (respostaEquipamento.equalsIgnoreCase("s")) {
                System.out.print("Digite o nome do equipamento para associar: ");
                String nomeEquipamento = scanner.nextLine();

                System.out.print("Digite a categoria do equipamento para associar: ");
                String categoriaEquipamento = scanner.nextLine();

                Equipamento equipamento = findEquipamentoByNomeCategoria(entityManager, nomeEquipamento, categoriaEquipamento);

                if (equipamento == null) {
                    System.out.println("Equipamento com os parâmetros fornecidos não encontrado.");
                } else {
                    equipamento.getReservas().add(reserva);
                    reserva.setEquipamento(equipamento);
                    entityManager.merge(equipamento);
                    entityManager.merge(reserva);
                    System.out.println("Equipamento associado com sucesso!");
                }
            }

            transaction.commit();
            System.out.println("Reserva criado com sucesso!");

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

    public static void updateReserva() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            Date dataReserva = null;
            while (dataReserva == null) {
                System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                String dataReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    dataReserva = sdfData.parse(dataReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter data da reserva.");
                }
            }

            Date horaReserva = null;
            while (horaReserva == null) {
                System.out.print("Digite a hora da reserva (HH:mm): ");
                String horaReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                    horaReserva = sdfHora.parse(horaReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter hora da reserva.");
                }
            }

            Reserva reserva = findReservaByDataHora(entityManager, dataReserva, horaReserva);

            if (reserva != null) {
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

                System.out.print("Deseja atualizar o cliente da Reserva? (s/n): ");
                String respostaCliente = scanner.nextLine();
                if (respostaCliente.equalsIgnoreCase("s")) {
                    Cliente cliente = reserva.getCliente();
                    if (cliente != null) {

                        System.out.print("Nome atual do cliente: " + cliente.getNome() +
                                ". Novo nome (ou pressione Enter para manter): ");
                        String novoNome = scanner.nextLine();
                        if (!novoNome.isEmpty()) {
                            cliente.setNome(novoNome);
                        }

                        System.out.print("Email atual do cliente: " + cliente.getEmail() +
                                ". Novo email (ou pressione Enter para manter): ");
                        String novoEmail = scanner.nextLine();
                        if (!novoEmail.isEmpty()) {
                            cliente.setEmail(novoEmail);
                        }

                        System.out.print("Telefone atual do cliente: " + cliente.getTelefone() +
                                ". Novo Telefone (ou pressione Enter para manter): ");
                        String novoTelefone = scanner.nextLine();
                        if (!novoTelefone.isEmpty()) {
                            cliente.setTelefone(novoTelefone);
                        }

                        System.out.print("Data de nascimento atual: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(cliente.getData_nascimento())
                                + ". Nova data de nascimento (dd/MM/yyyy) (ou pressione Enter para manter): ");
                        String novaDataNascimentoStr = scanner.nextLine();
                        if (!novaDataNascimentoStr.isEmpty()) {
                            try {
                                Date novaDataNascimento = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(novaDataNascimentoStr);
                                cliente.setData_nascimento(novaDataNascimento);
                            } catch (java.text.ParseException e) {
                                System.out.println("Erro ao converter a nova data de nascimento.");
                            }
                        }

                        entityManager.merge(cliente);
                        System.out.println("Dados do cliente atualizados com sucesso!");

                    } else {
                        System.out.println("Este plano de treino não possui cliente associado.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de cliente à reserva? (s/n): ");
                String respostaCliente1 = scanner.nextLine();
                if (respostaCliente1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do cliente para associar: ");
                    String nomeCliente = scanner.nextLine();

                    System.out.print("Digite o e-mail do cliente para associar: ");
                    String emailCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);
                    if (cliente != null) {
                        cliente.getReservas().add(reserva);
                        reserva.setCliente(cliente);
                        entityManager.merge(cliente);
                        entityManager.merge(reserva);
                        System.out.println("Relacionamento de cliente adicionado com sucesso!");
                    } else {
                        System.out.println("Cliente com os parâmetros fornecidos não encontrado.");
                    }
                }


                System.out.print("Deseja remover o relacionamento de cliente à reserva? (s/n): ");
                String respostaRemoverPlano = scanner.nextLine();
                if (respostaRemoverPlano.equalsIgnoreCase("s")) {
                    if (reserva.getCliente() != null) {
                        Cliente cliente = reserva.getCliente();

                        cliente.getReservas().remove(reserva);
                        reserva.setCliente(null);

                        entityManager.merge(cliente);
                        entityManager.merge(reserva);

                        System.out.println("Relacionamento de cliente removido com sucesso!");
                    } else {
                        System.out.println("Este plano de treino não possui um cliente associado.");
                    }
                }

                System.out.print("Deseja atualizar o equipamento da Reserva? (s/n): ");
                String respostaEquipamento = scanner.nextLine();
                if (respostaEquipamento.equalsIgnoreCase("s")) {
                    Equipamento equipamento = reserva.getEquipamento();
                    if (equipamento != null) {

                        System.out.print("Nome atual do equipamento: " + equipamento.getNome() +
                                ". Novo nome (ou pressione Enter para manter): ");
                        String novoNome = scanner.nextLine();
                        if (!novoNome.isEmpty()) {
                            equipamento.setNome(novoNome);
                        }

                        System.out.print("Categoria atual do equipamento: " + equipamento.getCategoria() +
                                ". Nova categoria (ou pressione Enter para manter): ");
                        String novaCategoria = scanner.nextLine();
                        if (!novaCategoria.isEmpty()) {
                            equipamento.setCategoria(novaCategoria);
                        }

                        System.out.print("Disponibilidade atual: " + (equipamento.isDisponibilidade() ? "Disponível" : "Indisponível") +
                                ". Nova disponibilidade (true para disponível, false para indisponível) (ou pressione Enter para manter): ");
                        String novaDisponibilidadeStr = scanner.nextLine();
                        if (!novaDisponibilidadeStr.isEmpty()) {
                            equipamento.setDisponibilidade(Boolean.parseBoolean(novaDisponibilidadeStr));
                        }

                        entityManager.merge(equipamento);
                        System.out.println("Dados do equipamento atualizados com sucesso!");

                    } else {
                        System.out.println("Esta reserva não possui equipamento associado.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de equipamento à reserva? (s/n): ");
                String respostaEquipamento1 = scanner.nextLine();
                if (respostaEquipamento1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do equipamento para associar: ");
                    String nomeEquipamento = scanner.nextLine();

                    System.out.print("Digite a categoria do equipamento para associar: ");
                    String categoriaEquipamento = scanner.nextLine();

                    Equipamento equipamento = findEquipamentoByNomeCategoria(entityManager, nomeEquipamento, categoriaEquipamento);
                    if (equipamento != null) {
                        equipamento.getReservas().add(reserva);
                        reserva.setEquipamento(equipamento);
                        entityManager.merge(equipamento);
                        entityManager.merge(reserva);
                        System.out.println("Relacionamento de equipamento adicionado com sucesso!");
                    } else {
                        System.out.println("Equipamento com os parâmetros fornecidos não encontrado.");
                    }
                }

                System.out.print("Deseja remover o relacionamento de equipamento da reserva? (s/n): ");
                String respostaRemoverEquipamento = scanner.nextLine();
                if (respostaRemoverEquipamento.equalsIgnoreCase("s")) {
                    if (reserva.getEquipamento() != null) {
                        Equipamento equipamento = reserva.getEquipamento();

                        equipamento.getReservas().remove(reserva);
                        reserva.setEquipamento(null);

                        entityManager.merge(equipamento);
                        entityManager.merge(reserva);

                        System.out.println("Relacionamento de equipamento removido com sucesso!");
                    } else {
                        System.out.println("Esta reserva não possui um equipamento associado.");
                    }
                }

                entityManager.merge(reserva);
                transaction.commit();
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Reserva não encontrada");
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

            Date dataReserva = null;
            while (dataReserva == null) {
                System.out.print("Digite a data da reserva (dd/MM/yyyy): ");
                String dataReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfData = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    dataReserva = sdfData.parse(dataReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter data da reserva.");
                }
            }

            Date horaReserva = null;
            while (horaReserva == null) {
                System.out.print("Digite a hora da reserva (HH:mm): ");
                String horaReservaStr = scanner.nextLine();
                try {
                    java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                    horaReserva = sdfHora.parse(horaReservaStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter hora da reserva.");
                }
            }

            Reserva reserva = findReservaByDataHora(entityManager, dataReserva, horaReserva);

            if (reserva == null) {
                System.out.println("Reserva não encontrada.");
                return;
            }
            if (reserva.getCliente() != null) {
                System.out.println("A reserva possui um cliente associado. Remova o vínculo com o cliente antes de excluir a reserva.");
                return;
            }
            if (reserva.getEquipamento() != null) {
                System.out.println("A reserva possui um equipamento associado. Remova o vínculo com o equipamento antes de excluir a reserva.");
                return;
            }

            System.out.println("Reserva encontrada: Data: " + reserva.getDataReserva() + " - Hora: " + reserva.getHoraReserva());
            System.out.print("Tem certeza que deseja excluir esta reserva? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(reserva);
                transaction.commit();
                System.out.println("Reserva excluída com sucesso!");
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

    public static void readReservas() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            List<Reserva> reservas = findAllReservas(entityManager);

            if (reservas == null || reservas.isEmpty()) {
                System.out.println("Nenhuma reserva encontrada.");
            } else {
                System.out.println("Lista de Reservas:");
                for (Reserva reserva : reservas) {
                    System.out.println("=======================================");
                    System.out.println("ID da Reserva: " + reserva.getId());
                    System.out.println("Data da Reserva: " + reserva.getDataReserva());
                    System.out.println("Hora da Reserva: " + reserva.getHoraReserva());

                    if (reserva.getCliente() != null) {
                        Cliente cliente = reserva.getCliente();
                        System.out.println("Cliente:");
                        System.out.println("Nome: " + cliente.getNome());
                        System.out.println("E-mail: " + cliente.getEmail());
                        System.out.println("Telefone: " + cliente.getTelefone());
                    } else {
                        System.out.println("Cliente: Não atribuído.");
                    }

                    if (reserva.getEquipamento() != null) {
                        Equipamento equipamento = reserva.getEquipamento();
                        System.out.println("Equipamento:");
                        System.out.println("Nome: " + equipamento.getNome());
                        System.out.println("Categoria: " + equipamento.getCategoria());
                        System.out.println("Disponibilidade: " + (equipamento.isDisponibilidade() ? "Disponível" : "Indisponível"));
                    } else {
                        System.out.println("Equipamento: Não atribuído.");
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


    private static List<Reserva> findAllReservas(EntityManager entityManager) {
        TypedQuery<Reserva> query = entityManager.createQuery("SELECT r FROM Reserva r", Reserva.class);
        try {
            return query.getResultList();
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
}



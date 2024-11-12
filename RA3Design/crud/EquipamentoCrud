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

            System.out.print("Digite a disponibilidade do equipamento (true/false): ");
            equipamento.setDisponibilidade(scanner.nextBoolean());
            scanner.nextLine();  // Limpa o buffer após o nextBoolean()

            entityManager.persist(equipamento);
            entityManager.flush();

            System.out.print("Deseja associar uma reserva ao equipamento? (s/n): ");
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
                            SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
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
                            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
                            horaReserva = sdfHora.parse(horaReservaStr);
                        } catch (java.text.ParseException e) {
                            System.out.println("Erro ao converter hora da reserva.");
                        }
                    }

                    Reserva reserva = new Reserva();
                    reserva.setEquipamento(equipamento);
                    reserva.setDataReserva(dataReserva);
                    reserva.setHoraReserva(horaReserva);

                    // Persistindo a reserva associada ao equipamento
                    entityManager.persist(reserva);
                    reservasEquipamento.add(reserva);

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

            System.out.print("Digite o nome do equipamento a ser atualizado: ");
            String nomeEquipamento = scanner.nextLine();

            Equipamento equipamento = findEquipamentoByNome(entityManager, nomeEquipamento);

            if (equipamento != null) {
                System.out.println("Equipamento encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Nome atual: " + equipamento.getNome() + ". Novo nome (ou pressione Enter para manter): ");
                String nome = scanner.nextLine();
                if (!nome.isEmpty()) {
                    equipamento.setNome(nome);
                }

                System.out.print("Marca atual: " + equipamento.getMarca() + ". Nova marca (ou pressione Enter para manter): ");
                String marca = scanner.nextLine();
                if (!marca.isEmpty()) {
                    equipamento.setMarca(marca);
                }

                System.out.print("Descrição atual: " + equipamento.getDescricao() + ". Nova descrição (ou pressione Enter para manter): ");
                String descricao = scanner.nextLine();
                if (!descricao.isEmpty()) {
                    equipamento.setDescricao(descricao);
                }

                System.out.print("Data de aquisição atual: " + new SimpleDateFormat("dd/MM/yyyy").format(equipamento.getDataAquisicao())
                        + ". Nova data de aquisição (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String dataAquisicaoStr = scanner.nextLine();
                if (!dataAquisicaoStr.isEmpty()) {
                    try {
                        Date dataAquisicao = new SimpleDateFormat("dd/MM/yyyy").parse(dataAquisicaoStr);
                        equipamento.setDataAquisicao(dataAquisicao);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a data de aquisição. Formato esperado: dd/MM/yyyy");
                    }
                }

                System.out.print("Deseja atualizar a manutenção do equipamento? (s/n): ");
                String respostaManutencao = scanner.nextLine();
                if (respostaManutencao.equalsIgnoreCase("s")) {
                    Manutencao manutencao = equipamento.getManutencao();
                    if (manutencao != null) {
                        System.out.print("Descrição atual: " + manutencao.getDescricao() + ". Nova descrição (ou pressione Enter para manter): ");
                        String descricaoManutencao = scanner.nextLine();
                        if (!descricaoManutencao.isEmpty()) {
                            manutencao.setDescricao(descricaoManutencao);
                        }

                        System.out.print("Data de execução atual: " + new SimpleDateFormat("dd/MM/yyyy").format(manutencao.getDataExecucao())
                                + ". Nova data de execução (dd/MM/yyyy) (ou pressione Enter para manter): ");
                        String novaDataExecucaoStr = scanner.nextLine();
                        if (!novaDataExecucaoStr.isEmpty()) {
                            try {
                                Date novaDataExecucao = new SimpleDateFormat("dd/MM/yyyy").parse(novaDataExecucaoStr);
                                manutencao.setDataExecucao(novaDataExecucao);
                            } catch (java.text.ParseException e) {
                                System.out.println("Erro ao converter a nova data de execução.");
                            }
                        }

                        entityManager.merge(manutencao);
                        System.out.println("Manutenção atualizada com sucesso!");
                    } else {
                        System.out.println("Este equipamento não possui manutenção associada.");
                    }
                }

                System.out.print("Deseja adicionar uma nova manutenção ao equipamento? (s/n): ");
                String respostaManutencao1 = scanner.nextLine();
                if (respostaManutencao1.equalsIgnoreCase("s")) {
                    System.out.print("Digite a descrição da manutenção que deseja adicionar: ");
                    String descricaoManutencao = scanner.nextLine();

                    System.out.print("Digite a data de execução da manutenção (dd/MM/yyyy): ");
                    String dataExecucaoStr = scanner.nextLine();
                    Date dataExecucao = null;
                    try {
                        dataExecucao = new SimpleDateFormat("dd/MM/yyyy").parse(dataExecucaoStr);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a data de execução.");
                    }

                    Manutencao manutencao = findManutencaoByDescricaoData(entityManager, descricaoManutencao, dataExecucao);
                    if (manutencao != null) {
                        equipamento.setManutencao(manutencao);
                        manutencao.setEquipamento(equipamento);
                        entityManager.merge(equipamento);
                        entityManager.merge(manutencao);
                        System.out.println("Relacionamento de manutenção adicionado com sucesso!");
                    } else {
                        System.out.println("Manutenção com os parâmetros fornecidos não encontrada.");
                    }
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

            System.out.print("Digite o nome do equipamento a ser excluído: ");
            String nomeEquipamento = scanner.nextLine();

            Equipamento equipamento = findEquipamentoByNome(entityManager, nomeEquipamento);

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
                    System.out.println("Descrição: " + equipamento.getDescricao());
                    System.out.println("Quantidade: " + equipamento.getQuantidade());
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

    private static List<Equipamento> findAllEquipamentos(EntityManager entityManager) {
        TypedQuery<Equipamento> query = entityManager.createQuery("SELECT e FROM Equipamento e", Equipamento.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Equipamento findEquipamentoByNome(EntityManager entityManager, String nome) {
        Query query = entityManager.createQuery("SELECT e FROM Equipamento e WHERE e.nome = :nome");
        query.setParameter("nome", nome);
        try {
            return (Equipamento) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}


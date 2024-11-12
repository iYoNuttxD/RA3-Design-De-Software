package RA3Design.crud;

import RA3Design.model.Cliente;
import RA3Design.model.PersonalTrainer;
import RA3Design.model.PlanoTreino;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class PlanoTreinoCrud {
    public static void createPlanoTreino() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            PlanoTreino planoTreino = new PlanoTreino();

            System.out.print("Digite a descrição do plano de treino: ");
            planoTreino.setDescricao(scanner.nextLine());

            String dataInicio = null;
            Date dateInicio = null;
            while (dateInicio == null) {
                System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy): ");
                dataInicio = scanner.nextLine();
                dateInicio = parseDate(dataInicio);
                if (dateInicio == null) {
                    System.out.println("Erro ao converter a data de início. Formato esperado: dd/MM/yyyy.");
                }
            }
            planoTreino.setDataInicio(dateInicio);

            String dataFim;
            Date dateFim = null;
            while (dateFim == null) {
                System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
                dataFim = scanner.nextLine();
                dateFim = parseDate(dataFim);
                if (dateFim == null) {
                    System.out.println("Erro ao converter a data de fim. Formato esperado: dd/MM/yyyy.");
                }
            }
            planoTreino.setDataFim(dateFim);

            entityManager.persist(planoTreino);
            entityManager.flush();

            if (confirmAction("Deseja associar um personal trainer ao plano de treino? (s/n): ")) {
                PersonalTrainer personalTrainer = getPersonalTrainerDetails(scanner, entityManager);
                if (personalTrainer != null) {
                    planoTreino.setPersonalTrainer(personalTrainer);
                    entityManager.merge(planoTreino);
                    System.out.println("Personal trainer associado com sucesso.");
                } else {
                    System.out.println("Personal trainer não encontrado.");
                }
            }

            if (confirmAction("Deseja associar um cliente ao plano de treino? (s/n): ")) {
                Cliente cliente = getClienteDetails(scanner, entityManager);
                if (cliente != null) {
                    cliente.setPlanoTreino(planoTreino);
                    entityManager.merge(cliente);
                    System.out.println("Cliente associado ao plano de treino com sucesso.");
                } else {
                    System.out.println("Cliente não encontrado.");
                }
            }

            transaction.commit();
            System.out.println("Plano de treino criado com sucesso!");

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

    private static boolean confirmAction(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message);
        String resposta = scanner.nextLine();
        return resposta.equalsIgnoreCase("s");
    }

    private static PersonalTrainer getPersonalTrainerDetails(Scanner scanner, EntityManager entityManager) {
        System.out.print("Digite o nome do personal trainer: ");
        String nomePersonalTrainer = scanner.nextLine();
        System.out.print("Digite a especialidade do personal trainer: ");
        String especialidadePersonalTrainer = scanner.nextLine();
        System.out.print("Digite o telefone do personal trainer: ");
        String telefonePersonalTrainer = scanner.nextLine();

        return findPersonalByDetails(entityManager, nomePersonalTrainer, especialidadePersonalTrainer, telefonePersonalTrainer);
    }

    private static Cliente getClienteDetails(Scanner scanner, EntityManager entityManager) {
        System.out.print("Digite o nome do cliente: ");
        String nomeCliente = scanner.nextLine();
        System.out.print("Digite o telefone do cliente: ");
        String telefoneCliente = scanner.nextLine();

        return findClienteByDetails(entityManager, nomeCliente, telefoneCliente);
    }


    public static Cliente findClienteByDetails(EntityManager entityManager, String nome, String telefone) {
        try {
            return entityManager.createQuery(
                            "SELECT c FROM Cliente c WHERE c.nome = :nome AND c.telefone = :telefone", Cliente.class)
                    .setParameter("nome", nome)
                    .setParameter("telefone", telefone)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nenhum cliente encontrado com as informações fornecidas.");
            return null;
        }
    }


    public static PersonalTrainer findPersonalByDetails(EntityManager entityManager, String nome, String especialidade, String telefone) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM PersonalTrainer p WHERE p.nome = :nome AND p.especialidade = :especialidade AND p.telefone = :telefone", PersonalTrainer.class)
                    .setParameter("nome", nome)
                    .setParameter("especialidade", especialidade)
                    .setParameter("telefone", telefone)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nenhum personal trainer encontrado com as informações fornecidas.");
            return null;
        }
    }

    public static void updatePlanoTreino() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite a descrição do plano de treino a ser atualizado: ");
            String descricaoPlano = scanner.nextLine();

            PlanoTreino planoTreino = findPlanoByDescricao(entityManager, descricaoPlano);

            if (planoTreino != null) {
                System.out.println("Plano de treino encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Descrição atual: " + planoTreino.getDescricao() + ". Nova descrição (ou pressione Enter para manter): ");
                String descricao = scanner.nextLine();
                if (!descricao.isEmpty()) {
                    planoTreino.setDescricao(descricao);
                }

                System.out.print("Data de início atual: " + formatDate(planoTreino.getDataInicio())
                        + ". Nova data de início (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String novaDataInicioStr = scanner.nextLine();
                if (!novaDataInicioStr.isEmpty()) {
                    Date novaDataInicio = parseDate(novaDataInicioStr);
                    if (novaDataInicio != null) {
                        planoTreino.setDataInicio(novaDataInicio);
                    } else {
                        System.out.println("Formato de data inválido.");
                    }
                }

                System.out.print("Data de fim atual: " + formatDate(planoTreino.getDataFim())
                        + ". Nova data de fim (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String novaDataFimStr = scanner.nextLine();
                if (!novaDataFimStr.isEmpty()) {
                    Date novaDataFim = parseDate(novaDataFimStr);
                    if (novaDataFim != null) {
                        planoTreino.setDataFim(novaDataFim);
                    } else {
                        System.out.println("Formato de data inválido.");
                    }
                }

                if (planoTreino.getPersonalTrainer() != null) {
                    PersonalTrainer personalTrainerAtual = planoTreino.getPersonalTrainer();
                    System.out.println("Personal Trainer atual: ");
                    System.out.println("Nome: " + personalTrainerAtual.getNome());
                    System.out.println("Especialidade: " + personalTrainerAtual.getEspecialidade());
                    System.out.println("Telefone: " + personalTrainerAtual.getTelefone());
                } else {
                    System.out.println("Nenhum Personal Trainer vinculado atualmente.");
                }

                System.out.print("Deseja (1) vincular um novo personal trainer, (2) desassociar o personal trainer atual, ou (3) manter o atual? Digite 1, 2 ou 3: ");
                String opcaoPersonalTrainer = scanner.nextLine();

                if (opcaoPersonalTrainer.equals("1")) {
                    System.out.print("Digite o nome do personal trainer: ");
                    String nomePersonalTrainer = scanner.nextLine();
                    System.out.print("Digite a especialidade do personal trainer: ");
                    String especialidadePersonalTrainer = scanner.nextLine();
                    System.out.print("Digite o telefone do personal trainer: ");
                    String telefonePersonalTrainer = scanner.nextLine();

                    PersonalTrainer personalTrainer = findPersonalByNomeEspecialidadeTelefone(
                            entityManager, nomePersonalTrainer, especialidadePersonalTrainer, telefonePersonalTrainer);

                    if (personalTrainer != null) {
                        planoTreino.setPersonalTrainer(personalTrainer);
                        System.out.println("Personal trainer vinculado com sucesso.");
                    } else {
                        System.out.println("Personal trainer não encontrado.");
                    }
                } else if (opcaoPersonalTrainer.equals("2")) {
                    planoTreino.setPersonalTrainer(null);
                    System.out.println("Personal trainer desassociado do plano de treino.");
                }

                if (planoTreino.getCliente() != null) {
                    Cliente clienteAtual = planoTreino.getCliente();
                    System.out.println("Cliente atual associado ao plano de treino: ");
                    System.out.println("Nome: " + clienteAtual.getNome());
                } else {
                    System.out.println("Nenhum cliente vinculado atualmente.");
                }

                System.out.print("Deseja (1) vincular um novo cliente, (2) desassociar o cliente atual, ou (3) manter o atual? Digite 1, 2 ou 3: ");
                String opcaoCliente = scanner.nextLine();

                if (opcaoCliente.equals("1")) {
                    System.out.print("Digite o nome do cliente: ");
                    String nomeCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNome(entityManager, nomeCliente);
                    if (cliente != null) {
                        planoTreino.setCliente(cliente);
                        System.out.println("Cliente vinculado com sucesso.");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                } else if (opcaoCliente.equals("2")) {
                    planoTreino.setCliente(null);
                    System.out.println("Cliente desassociado do plano de treino.");
                }

                entityManager.merge(planoTreino);
                System.out.println("Plano de treino atualizado com sucesso!");

                transaction.commit();
            } else {
                System.out.println("Plano de treino não encontrado.");
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


    private static String formatDate(Date date) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private static Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public static PersonalTrainer findPersonalByNomeEspecialidadeTelefone(EntityManager entityManager, String nome, String especialidade, String telefone) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM PersonalTrainer p WHERE p.nome = :nome AND p.especialidade = :especialidade AND p.telefone = :telefone", PersonalTrainer.class)
                    .setParameter("nome", nome)
                    .setParameter("especialidade", especialidade)
                    .setParameter("telefone", telefone)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nenhum personal trainer encontrado com os dados fornecidos.");
            return null;
        }
    }

    public static void deletePlanoTreino() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite a descrição do plano de treino a ser deletado: ");
            String descricaoPlano = scanner.nextLine();

            Date dataInicio = null;
            Date dataFim = null;

            System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy) ou deixe em branco: ");
            String dataInicioStr = scanner.nextLine();
            if (!dataInicioStr.trim().isEmpty()) {
                try {
                    dataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataInicioStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de início. Por favor, use o formato dd/MM/yyyy.");
                    return;
                }
            }

            System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy) ou deixe em branco: ");
            String dataFimStr = scanner.nextLine();
            if (!dataFimStr.trim().isEmpty()) {
                try {
                    dataFim = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de fim. Por favor, use o formato dd/MM/yyyy.");
                    return;
                }
            }

            PlanoTreino planoTreino = findPlanoByDescricaoEData(entityManager, descricaoPlano, dataInicio, dataFim);

            if (planoTreino != null) {
                boolean planoComCliente = planoTreino.getCliente() != null;

                boolean planoComPersonalTrainer = planoTreino.getPersonalTrainer() != null;

                if (planoComCliente || planoComPersonalTrainer) {
                    System.out.println("O plano de treino não pode ser deletado porque está associado a um ou mais clientes ou personal trainers.");
                    transaction.rollback();
                } else {
                    entityManager.remove(planoTreino);
                    transaction.commit();
                    System.out.println("Plano de treino deletado com sucesso!");
                }
            } else {
                System.out.println("Plano de treino não encontrado.");
            }

        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException nestedException) {
                    nestedException.printStackTrace();
                }
            }
            exception.printStackTrace();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }



    public static PlanoTreino findPlanoByDescricaoEData(EntityManager entityManager, String descricaoPlano, Date dataInicio, Date dataFim) {
        String jpql = "SELECT p FROM PlanoTreino p WHERE p.descricao = :descricao";
        if (dataInicio != null) {
            jpql += " AND p.dataInicio >= :dataInicio";
        }
        if (dataFim != null) {
            jpql += " AND p.dataFim <= :dataFim";
        }

        TypedQuery<PlanoTreino> query = entityManager.createQuery(jpql, PlanoTreino.class);
        query.setParameter("descricao", descricaoPlano);
        if (dataInicio != null) {
            query.setParameter("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            query.setParameter("dataFim", dataFim);
        }

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }



    public static PlanoTreino findPlanoByDescricao(EntityManager entityManager, String descricao) {
        try {
            return entityManager.createQuery(
                            "SELECT p FROM PlanoTreino p WHERE p.descricao = :descricao", PlanoTreino.class)
                    .setParameter("descricao", descricao)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nenhum plano de treino encontrado com a descrição fornecida.");
            return null;
        }
    }


    public static void readPlanoTreino(EntityManager entityManager) {
        List<PlanoTreino> planosTreino = findAllPlanosTreino(entityManager);

        if (planosTreino == null || planosTreino.isEmpty()) {
            System.out.println("Nenhum plano de treino encontrado.");
        } else {
            System.out.println("Lista de Planos de Treino:");
            for (PlanoTreino planoTreino : planosTreino) {
                System.out.println("=======================================");
                System.out.println("Descrição: " + planoTreino.getDescricao());
                System.out.println("Data de Início: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataInicio()));
                System.out.println("Data de Fim: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataFim()));

                if (planoTreino.getPersonalTrainer() != null) {
                    PersonalTrainer personalTrainer = planoTreino.getPersonalTrainer();
                    System.out.println("Personal Trainer: " + personalTrainer.getNome());
                    System.out.println("Especialidade: " + personalTrainer.getEspecialidade());
                } else {
                    System.out.println("Personal Trainer: Não atribuído.");
                }

                if (planoTreino.getCliente() != null) {
                    Cliente cliente = planoTreino.getCliente();
                    System.out.println("Cliente: " + cliente.getNome());
                } else {
                    System.out.println("Cliente: Não atribuído.");
                }

                System.out.println("=======================================");
            }
        }
    }


    private static List<PlanoTreino> findAllPlanosTreino(EntityManager entityManager) {
        TypedQuery<PlanoTreino> query = entityManager.createQuery("SELECT p FROM PlanoTreino p", PlanoTreino.class);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static Cliente findClienteByNome(EntityManager entityManager, String nome) {
        Query query = entityManager.createQuery("SELECT c FROM Cliente c WHERE c.nome = :nome");
        query.setParameter("nome", nome);
        try {
            return (Cliente) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}

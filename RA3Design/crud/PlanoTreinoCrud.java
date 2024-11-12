package RA3Design.crud;

import RA3Design.model.Cliente;
import RA3Design.model.PersonalTrainer;
import RA3Design.model.PlanoTreino;
import jakarta.persistence.*;
import java.text.SimpleDateFormat;
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

            Date dataInicio = null;
            while (dataInicio == null) {
                System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy): ");
                String dataInicioStr = scanner.nextLine();
                try {
                    dataInicio = new SimpleDateFormat("dd/MM/yyyy").parse(dataInicioStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de início.");
                }
            }
            planoTreino.setDataInicio(dataInicio);

            Date dataFim = null;
            while (dataFim == null) {
                System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
                String dataFimStr = scanner.nextLine();
                try {
                    dataFim = new SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de fim.");
                }
            }
            planoTreino.setDataFim(dataFim);

            entityManager.persist(planoTreino);
            entityManager.flush();

            System.out.print("Deseja associar um personal trainer ao plano de treino? (s/n): ");
            String respostaPersonalTrainer = scanner.nextLine();
            if (respostaPersonalTrainer.equalsIgnoreCase("s")) {
                System.out.print("Digite o nome do personal trainer: ");
                String nomePersonalTrainer = scanner.nextLine();

                PersonalTrainer personalTrainer = findPersonalByNome(entityManager, nomePersonalTrainer);

                if (personalTrainer != null) {
                    planoTreino.setPersonalTrainer(personalTrainer);
                    entityManager.merge(planoTreino);
                    System.out.println("Personal trainer associado com sucesso.");
                } else {
                    System.out.println("Personal trainer não encontrado.");
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


    public static void updatePlanoTreino() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite a descrição ou ID do plano de treino a ser atualizado: ");
            String descricaoOuIdPlano = scanner.nextLine();

            System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy): ");
            String dataInicioStr = scanner.nextLine();
            Date dataInicio = null;
            if (!dataInicioStr.isEmpty()) {
                try {
                    dataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataInicioStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de início. Por favor, use o formato dd/MM/yyyy.");
                }
            }

            System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
            String dataFimStr = scanner.nextLine();
            Date dataFim = null;
            if (!dataFimStr.isEmpty()) {
                try {
                    dataFim = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de fim. Por favor, use o formato dd/MM/yyyy.");
                }
            }

            PlanoTreino planoTreino = findPlanoByIdOuDescricao(entityManager, descricaoOuIdPlano, dataInicio, dataFim);

            if (planoTreino != null) {
                System.out.println("Plano de treino encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Descrição atual: " + planoTreino.getDescricao() + ". Nova descrição (ou pressione Enter para manter): ");
                String descricao = scanner.nextLine();
                if (!descricao.isEmpty()) {
                    planoTreino.setDescricao(descricao);
                }

                System.out.print("Data de início atual: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataInicio())
                        + ". Nova data de início (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String novaDataInicioStr = scanner.nextLine();
                if (!novaDataInicioStr.isEmpty()) {
                    try {
                        Date novaDataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(novaDataInicioStr);
                        planoTreino.setDataInicio(novaDataInicio);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a nova data de início. Por favor, use o formato dd/MM/yyyy.");
                    }
                }

                System.out.print("Data de fim atual: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataFim())
                        + ". Nova data de fim (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String novaDataFimStr = scanner.nextLine();
                if (!novaDataFimStr.isEmpty()) {
                    try {
                        Date novaDataFim = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(novaDataFimStr);
                        planoTreino.setDataFim(novaDataFim);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a nova data de fim. Por favor, use o formato dd/MM/yyyy.");
                    }
                }

                System.out.print("Deseja atualizar o personal trainer associado ao plano de treino? (s/n): ");
                String respostaPersonalTrainer = scanner.nextLine();
                if (respostaPersonalTrainer.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do personal trainer: ");
                    String nomePersonalTrainer = scanner.nextLine();

                    PersonalTrainer personalTrainer = findPersonalByNome(entityManager, nomePersonalTrainer);

                    if (personalTrainer != null) {
                        planoTreino.setPersonalTrainer(personalTrainer);
                        System.out.println("Personal trainer associado com sucesso.");
                    } else {
                        System.out.println("Personal trainer não encontrado, mantendo o associado atual.");
                    }
                }

                System.out.print("Deseja atualizar o cliente associado ao plano de treino? (s/n): ");
                String respostaCliente = scanner.nextLine();
                if (respostaCliente.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do cliente: ");
                    String nomeCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNome(entityManager, nomeCliente);
                    if (cliente != null) {
                        planoTreino.setCliente(cliente);
                        System.out.println("Cliente associado com sucesso.");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
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
    public static PersonalTrainer findPersonalByNome(EntityManager entityManager, String nome) {
        try {
            return entityManager.createQuery("SELECT p FROM PersonalTrainer p WHERE p.nome = :nome", PersonalTrainer.class)
                    .setParameter("nome", nome)
                    .getSingleResult();
        } catch (NoResultException e) {
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

            System.out.print("Digite a descrição ou o ID do plano de treino a ser deletado: ");
            String descricaoOuIdPlano = scanner.nextLine();

            System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy): ");
            String dataInicioStr = scanner.nextLine();
            Date dataInicio = null;
            if (!dataInicioStr.isEmpty()) {
                try {
                    dataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataInicioStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de início. Por favor, use o formato dd/MM/yyyy.");
                }
            }

            System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
            String dataFimStr = scanner.nextLine();
            Date dataFim = null;
            if (!dataFimStr.isEmpty()) {
                try {
                    dataFim = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de fim. Por favor, use o formato dd/MM/yyyy.");
                }
            }

            PlanoTreino planoTreino = findPlanoByIdOuDescricao(entityManager, descricaoOuIdPlano, dataInicio, dataFim);

            if (planoTreino != null) {
                long countClientesComPlano = countClientesComPlano(entityManager, planoTreino);
                long countPersonalTrainersComPlano = countPersonalTrainersComPlano(entityManager, planoTreino);

                if (countClientesComPlano > 0 || countPersonalTrainersComPlano > 0) {
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
            throw exception;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static PlanoTreino findPlanoByIdOuDescricao(EntityManager entityManager, String descricaoOuIdPlano, Date dataInicio, Date dataFim) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM PlanoTreino p WHERE (p.descricao = :descricaoOuIdPlano OR p.nome = :descricaoOuIdPlano)");

        Long idPlano = null;
        try {
            idPlano = Long.parseLong(descricaoOuIdPlano);
            jpql = new StringBuilder("SELECT p FROM PlanoTreino p WHERE p.id = :idPlano");
        } catch (NumberFormatException e) {
        }

        if (dataInicio != null) {
            jpql.append(" AND p.dataInicio >= :dataInicio");
        }
        if (dataFim != null) {
            jpql.append(" AND p.dataFim <= :dataFim");
        }

        TypedQuery<PlanoTreino> query = entityManager.createQuery(jpql.toString(), PlanoTreino.class);

        if (idPlano != null) {
            query.setParameter("idPlano", idPlano);
        } else {
            query.setParameter("descricaoOuIdPlano", descricaoOuIdPlano);
        }

        if (dataInicio != null) {
            query.setParameter("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            query.setParameter("dataFim", dataFim);
        }

        List<PlanoTreino> planos = query.getResultList();
        return planos.isEmpty() ? null : planos.get(0);
    }



    private static long countClientesComPlano(EntityManager entityManager, PlanoTreino planoTreino) {
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.planoTreino = :planoTreino");
        query.setParameter("planoTreino", planoTreino);
        return (long) query.getSingleResult();
    }

    private static long countPersonalTrainersComPlano(EntityManager entityManager, PlanoTreino planoTreino) {
        Query query = entityManager.createQuery("SELECT COUNT(p) FROM PersonalTrainer p WHERE p.planoTreino = :planoTreino");
        query.setParameter("planoTreino", planoTreino);
        return (long) query.getSingleResult();
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

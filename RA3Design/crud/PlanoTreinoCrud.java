package RA3Design.crud;

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

                System.out.print("Data de início atual: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataInicio())
                        + ". Nova data de início (dd/MM/yyyy) (ou pressione Enter para manter): ");
                String novaDataInicioStr = scanner.nextLine();
                if (!novaDataInicioStr.isEmpty()) {
                    try {
                        Date novaDataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(novaDataInicioStr);
                        planoTreino.setDataInicio(novaDataInicio);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a nova data de início.");
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
                        System.out.println("Erro ao converter a nova data de fim.");
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
                        entityManager.merge(planoTreino);
                        System.out.println("Personal trainer associado com sucesso.");
                    } else {
                        System.out.println("Personal trainer não encontrado.");
                    }
                }

                entityManager.merge(planoTreino);
                System.out.println("Plano de treino atualizado com sucesso!");
            } else {
                System.out.println("Plano de treino não encontrado.");
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

    private static PlanoTreino findPlanoByDescricao(EntityManager entityManager, String descricao) {
        try {
            return entityManager.createQuery("SELECT p FROM PlanoTreino p WHERE p.descricao = :descricao", PlanoTreino.class)
                    .setParameter("descricao", descricao)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static PersonalTrainer findPersonalByNome(EntityManager entityManager, String nome) {
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

            System.out.print("Digite a descrição do plano de treino a ser deletado: ");
            String descricaoPlano = scanner.nextLine();

            PlanoTreino planoTreino = findPlanoByDescricao(entityManager, descricaoPlano);

            if (planoTreino != null) {
                entityManager.remove(planoTreino);
                transaction.commit();
                System.out.println("Plano de treino deletado com sucesso!");
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
    public static void readPlanoTreino() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

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

        } finally {
            if (entityManager != null) {
                entityManager.close();
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

    public static void readPlanoTreinoById(Long id) {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            PlanoTreino planoTreino = findPlanoTreinoById(entityManager, id);

            if (planoTreino == null) {
                System.out.println("Plano de treino não encontrado.");
            } else {
                System.out.println("Plano de Treino Encontrado:");
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

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private static PlanoTreino findPlanoTreinoById(EntityManager entityManager, Long id) {
        return entityManager.find(PlanoTreino.class, id);
    }


}

package RA3Design.crud;

import RA3Design.model.Cliente;
import RA3Design.model.PersonalTrainer;
import RA3Design.model.PlanoTreino;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                    planoTreino.setDataInicio(dataInicio);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de início.");
                }
            }

            Date dataFim = null;
            while (dataFim == null) {
                System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
                String dataFimStr = scanner.nextLine();
                try {
                    dataFim = new SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
                    planoTreino.setDataFim(dataFim);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de fim.");
                }
            }

            entityManager.persist(planoTreino);
            entityManager.flush();

            System.out.print("Deseja associar um cliente ao plano de treino? (s/n): ");
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
                    cliente.setPlanoTreino(planoTreino);
                    planoTreino.setCliente(cliente);
                    entityManager.merge(cliente);
                    entityManager.merge(planoTreino);
                    System.out.println("Cliente associado com sucesso!");
                }
            }

            System.out.print("Deseja associar um personal trainer ao plano de treino? (s/n): ");
            String respostaPersonal = scanner.nextLine();
            if (respostaPersonal.equalsIgnoreCase("s")) {
                System.out.print("Digite o nome do personal trainer: ");
                String nomePersonal = scanner.nextLine();

                System.out.print("Digite a especialidade do personal trainer: ");
                String especialidade = scanner.nextLine();

                System.out.print("Digite o telefone do personal trainer: ");
                String telefonePersonal = scanner.nextLine();

                PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidade,
                        telefonePersonal);
                if (personalTrainer != null) {
                    planoTreino.setPersonalTrainer(personalTrainer);
                    personalTrainer.getPlanosTreino().add(planoTreino);
                    entityManager.merge(personalTrainer);
                    entityManager.merge(planoTreino);
                    System.out.println("Personal trainer associado com sucesso!");
                } else {
                    System.out.println("Personal trainer com os parâmetros fornecidos não encontrado.");
                }
            }
            transaction.commit();
            System.out.println("Plano de Treino criado com sucesso!");
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

            System.out.print("Digite a descrição do plano de treino que deseja adicionar: ");
            String descricaoPlano = scanner.nextLine();

            System.out.print("Digite a data de início do plano de treino (dd/MM/yyyy): ");
            String dataInicioStr = scanner.nextLine();
            Date dataInicio = null;
            try {
                dataInicio = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataInicioStr);
            } catch (java.text.ParseException e) {
                System.out.println("Erro ao converter a data de início.");
            }

            System.out.print("Digite a data de fim do plano de treino (dd/MM/yyyy): ");
            String dataFimStr = scanner.nextLine();
            Date dataFim = null;
            try {
                dataFim = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dataFimStr);
            } catch (java.text.ParseException e) {
                System.out.println("Erro ao converter a data de fim.");
            }

            PlanoTreino planoTreino = findPlanoByDescricaoData(entityManager, descricaoPlano, dataInicio, dataFim);

            if (planoTreino != null) {
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

                System.out.print("Deseja atualizar o cliente do Plano de Treino? (s/n): ");
                String respostaCliente = scanner.nextLine();
                if (respostaCliente.equalsIgnoreCase("s")) {
                    Cliente cliente = planoTreino.getCliente();
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

                System.out.print("Deseja adicionar um novo relacionamento de cliente ao plano de treino? (s/n): ");
                String respostaCliente1 = scanner.nextLine();
                if (respostaCliente1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do cliente para associar: ");
                    String nomeCliente = scanner.nextLine();

                    System.out.print("Digite o e-mail do cliente para associar: ");
                    String emailCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);
                    if (cliente != null) {
                        cliente.setPlanoTreino(planoTreino);
                        planoTreino.setCliente(cliente);
                        entityManager.merge(cliente);
                        entityManager.merge(planoTreino);
                        System.out.println("Relacionamento de cliente adicionado com sucesso!");
                    } else {
                        System.out.println("Cliente com os parâmetros fornecidos não encontrado.");
                    }
                }


                System.out.print("Deseja remover o relacionamento de cliente do plano de treino? (s/n): ");
                String respostaRemoverPlano = scanner.nextLine();
                if (respostaRemoverPlano.equalsIgnoreCase("s")) {
                    if (planoTreino.getCliente() != null) {
                        Cliente cliente = planoTreino.getCliente();

                        cliente.setPlanoTreino(null);
                        planoTreino.setCliente(null);

                        entityManager.merge(cliente);
                        entityManager.merge(planoTreino);

                        System.out.println("Relacionamento de cliente removido com sucesso!");
                    } else {
                        System.out.println("Este plano de treino não possui um cliente associado.");
                    }
                }

                System.out.print("Deseja atualizar o personal trainer do Plano de Treino? (s/n): ");
                String respostaPersonal = scanner.nextLine();
                if (respostaPersonal.equalsIgnoreCase("s")) {
                    PersonalTrainer personalTrainer = planoTreino.getPersonalTrainer();
                    if (personalTrainer != null) {

                        System.out.print("Nome atual do personal trainer: " + personalTrainer.getNome() +
                                ". Novo nome (ou pressione Enter para manter): ");
                        String novoNome = scanner.nextLine();
                        if (!novoNome.isEmpty()) {
                            personalTrainer.setNome(novoNome);
                        }

                        System.out.print("Especialidade atual do personal trainer: " + personalTrainer.getEspecialidade() +
                                ". Nova especialidade (ou pressione Enter para manter): ");
                        String novaEspecialidade = scanner.nextLine();
                        if (!novaEspecialidade.isEmpty()) {
                            personalTrainer.setEspecialidade(novaEspecialidade);
                        }

                        System.out.print("Telefone atual do personal trainer: " + personalTrainer.getTelefone() +
                                ". Novo Telefone (ou pressione Enter para manter): ");
                        String novoTelefone = scanner.nextLine();
                        if (!novoTelefone.isEmpty()) {
                            personalTrainer.setTelefone(novoTelefone);
                        }

                        entityManager.merge(personalTrainer);
                        System.out.println("Dados do personal trainer atualizados com sucesso!");

                    } else {
                        System.out.println("Este plano de treino não possui personal trainer associado.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de personal trainer ao plano de treino? (s/n): ");
                String respostaPersonal1 = scanner.nextLine();
                if (respostaPersonal1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do personal trainer para associar: ");
                    String nomePersonal = scanner.nextLine();

                    System.out.print("Digite a especialidade do personal trainer para associar: ");
                    String especialidadePersonal = scanner.nextLine();

                    System.out.print("Digite o telefone do personal trainer para associar: ");
                    String telefonePersonal = scanner.nextLine();

                    PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidadePersonal, telefonePersonal);
                    if (personalTrainer != null) {
                        personalTrainer.getPlanosTreino().add(planoTreino);
                        planoTreino.setPersonalTrainer(personalTrainer);
                        entityManager.merge(personalTrainer);
                        entityManager.merge(planoTreino);
                        System.out.println("Relacionamento de personal trainer adicionado com sucesso!");
                    } else {
                        System.out.println("Personal trainer com os parâmetros fornecidos não encontrado.");
                    }
                }

                System.out.print("Deseja remover o relacionamento de personal trainer do plano de treino? (s/n): ");
                String respostaRemoverPersonal = scanner.nextLine();
                if (respostaRemoverPersonal.equalsIgnoreCase("s")) {
                    if (planoTreino.getPersonalTrainer() != null) {
                        PersonalTrainer personalTrainer = planoTreino.getPersonalTrainer();

                        personalTrainer.getPlanosTreino().remove(planoTreino);
                        planoTreino.setPersonalTrainer(null);

                        entityManager.merge(personalTrainer);
                        entityManager.merge(planoTreino);

                        System.out.println("Relacionamento de personal trainer removido com sucesso!");
                    } else {
                        System.out.println("Este plano de treino não possui um personal trainer associado.");
                    }
                }


                entityManager.merge(planoTreino);
                transaction.commit();
                System.out.println("Plano de treino atualizado com sucesso!");
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

    public static void deletePlanoTreino() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o ID do plano de treino a ser excluído: ");
            Long idPlanoTreino = scanner.nextLong();
            scanner.nextLine();  // Consumir a quebra de linha após o ID

            PlanoTreino planoTreino = entityManager.find(PlanoTreino.class, idPlanoTreino);

            if (planoTreino == null) {
                System.out.println("Plano de treino não encontrado.");
                return;
            }

            if (planoTreino.getCliente() != null || planoTreino.getPersonalTrainer() != null) {
                System.out.println("Não é possível excluir este plano de treino, pois ele possui um cliente ou personal trainer associado.");
                return;
            }

            System.out.println("Plano de treino encontrado: ID " + planoTreino.getId());
            System.out.print("Tem certeza que deseja excluir este plano de treino? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(entityManager.contains(planoTreino) ? planoTreino : entityManager.merge(planoTreino));
                transaction.commit();
                System.out.println("Plano de treino excluído com sucesso!");
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
                    System.out.println("ID: " + planoTreino.getId());
                    System.out.println("Descrição: " + planoTreino.getDescricao());
                    System.out.println("Data de Início: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataInicio()));
                    System.out.println("Data de Fim: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataFim()));

                    if (planoTreino.getCliente() != null) {
                        Cliente cliente = planoTreino.getCliente();
                        System.out.println("Cliente Associado:");
                        System.out.println("Nome: " + cliente.getNome());
                        System.out.println("E-mail: " + cliente.getEmail());
                        System.out.println("Telefone: " + cliente.getTelefone());
                    } else {
                        System.out.println("Cliente Associado: Não atribuído.");
                    }

                    if (planoTreino.getPersonalTrainer() != null) {
                        PersonalTrainer personalTrainer = planoTreino.getPersonalTrainer();
                        System.out.println("Personal Trainer Associado:");
                        System.out.println("Nome: " + personalTrainer.getNome());
                        System.out.println("Especialidade: " + personalTrainer.getEspecialidade());
                        System.out.println("Telefone: " + personalTrainer.getTelefone());
                    } else {
                        System.out.println("Personal Trainer Associado: Não atribuído.");
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

    private static PersonalTrainer findPersonalByDetails(EntityManager entityManager, String nome, String especialidade,
                                                         String telefone) {
        Query query = entityManager.createQuery("SELECT p FROM PersonalTrainer p WHERE p.nome = :nome AND " +
                "p.especialidade = :especialidade AND p.telefone = :telefone");
        query.setParameter("nome", nome);
        query.setParameter("especialidade", especialidade);
        query.setParameter("telefone", telefone);
        try {
            return (PersonalTrainer) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static PlanoTreino findPlanoByDescricaoData(EntityManager entityManager, String descricao, Date dataInicio,
                                                        Date dataFim) {
        Query query = entityManager.createQuery("SELECT p FROM PlanoTreino p WHERE p.descricao = :descricao " +
                "AND p.dataInicio = :dataInicio AND p.dataFim = :dataFim");
        query.setParameter("descricao", descricao);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        try {
            return (PlanoTreino) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
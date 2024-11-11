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

public class PersonalTrainerCrud {
    public static void createPersonalTrainer() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            PersonalTrainer personalTrainer = new PersonalTrainer();

            System.out.print("Digite o nome do personal trainer: ");
            personalTrainer.setNome(scanner.nextLine());

            System.out.print("Digite a especialidade do personal trainer: ");
            personalTrainer.setEspecialidade(scanner.nextLine());

            System.out.print("Digite o telefone do personal trainer: ");
            personalTrainer.setTelefone(scanner.nextLine());

            entityManager.persist(personalTrainer);
            entityManager.flush();

            System.out.print("Deseja associar clientes ao personal trainer? (s/n): ");
            String respostaClientes = scanner.nextLine();
            if (respostaClientes.equalsIgnoreCase("s")) {
                List<Cliente> clientesAssociados = new ArrayList<>();
                String continuar;
                do {
                    System.out.print("Digite o nome do cliente para associar: ");
                    String nomeCliente = scanner.nextLine();

                    System.out.print("Digite o e-mail do cliente para associar: ");
                    String emailCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);

                    if (cliente != null) {
                        clientesAssociados.add(cliente);
                        cliente.setPersonalTrainer(personalTrainer);
                        entityManager.merge(cliente);
                        System.out.println("Cliente " + cliente.getNome() + " associado com sucesso.");
                    } else {
                        System.out.println("Cliente não encontrado. Verifique os dados e tente novamente.");
                    }

                    System.out.print("Deseja associar outro cliente? (s/n): ");
                    continuar = scanner.nextLine();
                } while (continuar.equalsIgnoreCase("s"));

                personalTrainer.setClientes(clientesAssociados);
                entityManager.merge(personalTrainer);
            }

            System.out.print("Deseja associar planos de treino ao personal trainer? (s/n): ");
            String respostaPlano = scanner.nextLine();
            if (respostaPlano.equalsIgnoreCase("s")) {
                List<PlanoTreino> planosTreino = new ArrayList<>();
                String adicionarOutroPlano;
                do {
                    System.out.print("Digite a descrição do plano de treino: ");
                    String descricaoPlano = scanner.nextLine();

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

                    PlanoTreino planoTreino = findPlanoByDescricaoData(entityManager, descricaoPlano, dataInicio, dataFim);

                    if (planoTreino == null) {
                        System.out.println("Plano de treino não encontrado com a descrição e datas fornecidas.");
                    } else {
                        System.out.println("Plano de treino encontrado e associado ao personal trainer.");
                        planosTreino.add(planoTreino);
                        planoTreino.setPersonalTrainer(personalTrainer);
                        entityManager.merge(personalTrainer);
                    }

                    System.out.print("Deseja adicionar outro plano de treino? (s/n): ");
                    adicionarOutroPlano = scanner.nextLine();
                } while (adicionarOutroPlano.equalsIgnoreCase("s"));

                personalTrainer.setPlanoTreino(planosTreino);
                entityManager.merge(personalTrainer);
            }

            transaction.commit();
            System.out.println("Personal Trainer criado com sucesso!");

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


    public static void updatePersonalTrainer() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o nome do personal trainer a ser atualizado: ");
            String nomePersonal = scanner.nextLine();

            System.out.print("Digite a especialidade do personal trainer a ser atualizado: ");
            String especialidadePersonal = scanner.nextLine();

            System.out.print("Digite o telefone do personal trainer: ");
            String telefonePersonal = scanner.nextLine();

            PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidadePersonal, telefonePersonal);

            if (personalTrainer != null) {
                System.out.println("Personal trainer encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Nome atual: " + personalTrainer.getNome() + ". Novo nome (ou pressione Enter para manter): ");
                String nome = scanner.nextLine();
                if (!nome.isEmpty()) {
                    personalTrainer.setNome(nome);
                }

                System.out.print("Especialidade atual: " + personalTrainer.getEspecialidade() + ". Nova especialidade (ou pressione Enter para manter): ");
                String especialidade = scanner.nextLine();
                if (!especialidade.isEmpty()) {
                    personalTrainer.setEspecialidade(especialidade);
                }

                System.out.print("Telefone atual: " + personalTrainer.getTelefone() + ". Novo telefone (ou pressione Enter para manter): ");
                String telefone = scanner.nextLine();
                if (!telefone.isEmpty()) {
                    personalTrainer.setTelefone(telefone);
                }

                System.out.print("Deseja atualizar o plano de treino do Personal Trainer? (s/n): ");
                String respostaPlano = scanner.nextLine();
                if (respostaPlano.equalsIgnoreCase("s")) {
                    List<PlanoTreino> planosTreino = personalTrainer.getPlanosTreino();
                    if (planosTreino != null && !planosTreino.isEmpty()) {
                        for (PlanoTreino planoTreino : planosTreino) {

                            System.out.print("Descrição atual: " + planoTreino.getDescricao() +
                                    ". Nova descrição (ou pressione Enter para manter): ");
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

                            entityManager.merge(planoTreino);
                            System.out.println("Plano de treino atualizado com sucesso!");
                        }
                    } else {
                        System.out.println("Este personal trainer não possui planos de treino associados.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de plano de treino ao personal trainer? (s/n): ");
                String respostaPlano1 = scanner.nextLine();
                while (respostaPlano1.equalsIgnoreCase("s")) {
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
                        personalTrainer.getPlanosTreino().add(planoTreino);
                        planoTreino.setPersonalTrainer(personalTrainer);
                        entityManager.merge(personalTrainer);
                        entityManager.merge(planoTreino);
                        System.out.println("Relacionamento de plano de treino adicionado com sucesso!");
                    } else {
                        System.out.println("Plano de treino com os parâmetros fornecidos não encontrado.");
                    }

                    System.out.print("Deseja adicionar outro plano de treino? (s/n): ");
                    respostaPlano1 = scanner.nextLine();
                }



                System.out.print("Deseja atualizar os clientes do Personal Trainer? (s/n): ");
                String respostaCliente = scanner.nextLine();
                if (respostaCliente.equalsIgnoreCase("s")) {
                    List<Cliente> clientes = personalTrainer.getClientes();
                    if (clientes != null && !clientes.isEmpty()) {
                        for (Cliente cliente : clientes) {

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
                        }
                    } else {
                        System.out.println("Este Personal Trainer não possui clientes associados.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de cliente ao personal trainer? (s/n): ");
                String respostaCliente1 = scanner.nextLine();
                while (respostaCliente1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do cliente para associar: ");
                    String nomeCliente = scanner.nextLine();

                    System.out.print("Digite o e-mail do cliente para associar: ");
                    String emailCliente = scanner.nextLine();

                    Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);
                    if (cliente != null) {
                        personalTrainer.getClientes().add(cliente);
                        cliente.setPersonalTrainer(personalTrainer);
                        entityManager.merge(personalTrainer);
                        entityManager.merge(cliente);
                        System.out.println("Relacionamento de cliente adicionado com sucesso!");
                    } else {
                        System.out.println("Cliente com os parâmetros fornecidos não encontrado.");
                    }

                    System.out.print("Deseja adicionar outro cliente? (s/n): ");
                    respostaCliente1 = scanner.nextLine();
                }

                entityManager.merge(personalTrainer);

                transaction.commit();
                System.out.println("Personal Trainer atualizado com sucesso!");

            } else {
                System.out.println("Personal trainer não encontrado.");
            }

        } catch (Exception exception) {
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

    public static void deletePersonalTrainer() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o nome do personal trainer a ser excluído: ");
            String nomePersonal = scanner.nextLine();

            System.out.print("Digite a especialidade do personal trainer a ser excluído: ");
            String especialidadePersonal = scanner.nextLine();

            System.out.print("Digite o telefone do novo personal trainer: ");
            String telefonePersonal = scanner.nextLine();

            PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidadePersonal,
                    telefonePersonal);

            if (personalTrainer == null) {
                System.out.println("Personal trainer não encontrado.");
                return;
            }

            System.out.println("Personal trainer encontrado: " + personalTrainer.getNome() + " - " + personalTrainer.getEspecialidade());
            System.out.print("Tem certeza que deseja excluir este personal trainer? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                if (personalTrainer.getPlanosTreino() != null && !personalTrainer.getPlanosTreino().isEmpty()) {
                    for (PlanoTreino plano : personalTrainer.getPlanosTreino()) {
                        entityManager.remove(plano);
                    }
                }

                if (personalTrainer.getClientes() != null && !personalTrainer.getClientes().isEmpty()) {
                    for (Cliente cliente : personalTrainer.getClientes()) {
                        cliente.setPersonalTrainer(null);
                        entityManager.merge(cliente);
                    }
                }

                entityManager.remove(entityManager.contains(personalTrainer) ? personalTrainer : entityManager.merge(personalTrainer));
                transaction.commit();
                System.out.println("Personal trainer excluído com sucesso!");
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


    public static void readPersonalTrainer() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            List<PersonalTrainer> personalTrainers = findAllPersonalTrainers(entityManager);

            if (personalTrainers == null || personalTrainers.isEmpty()) {
                System.out.println("Nenhum personal trainer encontrado.");
            } else {
                System.out.println("Lista de Personal Trainers:");
                for (PersonalTrainer personalTrainer : personalTrainers) {
                    System.out.println("=======================================");
                    System.out.println("Nome: " + personalTrainer.getNome());
                    System.out.println("Especialidade: " + personalTrainer.getEspecialidade());
                    System.out.println("Telefone: " + personalTrainer.getTelefone());

                    if (personalTrainer.getClientes() != null && !personalTrainer.getClientes().isEmpty()) {
                        System.out.println("Clientes: ");
                        for (Cliente cliente : personalTrainer.getClientes()) {
                            System.out.println("Nome: " + cliente.getNome());
                            System.out.println("E-mail: " + cliente.getEmail());
                        }
                    } else {
                        System.out.println("Clientes: Não atribuídos.");
                    }

                    if (personalTrainer.getPlanosTreino() != null && !personalTrainer.getPlanosTreino().isEmpty()) {
                        System.out.println("Planos de Treino: ");
                        for (PlanoTreino planoTreino : personalTrainer.getPlanosTreino()) {
                            System.out.println("Descrição: " + planoTreino.getDescricao());
                            System.out.println("Data de Início: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataInicio()));
                            System.out.println("Data de Fim: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(planoTreino.getDataFim()));
                        }
                    } else {
                        System.out.println("Planos de Treino: Não atribuídos.");
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
    
    private static List<PersonalTrainer> findAllPersonalTrainers(EntityManager entityManager) {
        TypedQuery<PersonalTrainer> query = entityManager.createQuery("SELECT p FROM PersonalTrainer p", PersonalTrainer.class);
        try {
            return query.getResultList();
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
}

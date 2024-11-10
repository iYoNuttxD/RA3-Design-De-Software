package RA3Design.crud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import RA3Design.model.Cliente;
import RA3Design.model.PersonalTrainer;
import RA3Design.model.PlanoTreino;

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

        PersonalTrainerCrud personalTrainer = new PersonalTrainerCrud();

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
                System.out.print("Digite o ID do cliente para associar: ");
                int idCliente = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                Cliente cliente = entityManager.find(Cliente.class, idCliente);
                if (cliente != null) {
                    clientesAssociados.add(cliente);
                    cliente.setPersonalTrainer(personalTrainer);
                    personalTrainer.getClientes().add(cliente);
                    entityManager.merge(cliente);
                    System.out.println("Cliente associado com sucesso!");
                } else {
                    System.out.println("Cliente com o ID fornecido não encontrado.");
                }

                System.out.print("Deseja associar outro cliente? (s/n): ");
                continuar = scanner.nextLine();
            } while (continuar.equalsIgnoreCase("s"));

            personalTrainer.setClientes(clientesAssociados);
        }

        System.out.print("Deseja associar um plano de treino ao personal trainer? (s/n): ");
        String respostaPlano = scanner.nextLine();
        if (respostaPlano.equalsIgnoreCase("s")) {
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
            if (planoTreino != null) {
                personalTrainer.getPlanosTreino().add(planoTreino);
                planoTreino.setPersonalTrainer(personalTrainer);
                entityManager.merge(planoTreino);
                System.out.println("Plano de treino associado com sucesso!");
            } else {
                System.out.println("Plano de treino com os parâmetros fornecidos não encontrado.");
            }
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

        // Busca o PersonalTrainer pelo nome e especialidade
        PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidadePersonal);

        if (personalTrainer != null) {
            System.out.println("Personal trainer encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

            // Atualiza o nome
            System.out.print("Nome atual: " + personalTrainer.getNome() + ". Novo nome (ou pressione Enter para manter): ");
            String nome = scanner.nextLine();
            if (!nome.isEmpty()) {
                personalTrainer.setNome(nome);
            }

            // Atualiza a especialidade
            System.out.print("Especialidade atual: " + personalTrainer.getEspecialidade() + ". Nova especialidade (ou pressione Enter para manter): ");
            String especialidade = scanner.nextLine();
            if (!especialidade.isEmpty()) {
                personalTrainer.setEspecialidade(especialidade);
            }

            // Atualiza o telefone
            System.out.print("Telefone atual: " + personalTrainer.getTelefone() + ". Novo telefone (ou pressione Enter para manter): ");
            String telefone = scanner.nextLine();
            if (!telefone.isEmpty()) {
                personalTrainer.setTelefone(telefone);
            }

            // Persiste as mudanças no banco de dados
            entityManager.merge(personalTrainer);

            // Commit da transação
            transaction.commit();
            System.out.println("Personal Trainer atualizado com sucesso!");
        } else {
            System.out.println("Personal trainer não encontrado.");
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

            // Busca o PersonalTrainer pelo nome e especialidade
            PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidadePersonal);

            if (personalTrainer == null) {
                System.out.println("Personal trainer não encontrado.");
                return;
            }

            System.out.println("Personal trainer encontrado: " + personalTrainer.getNome() + " - " + personalTrainer.getEspecialidade());
            System.out.print("Tem certeza que deseja excluir este personal trainer? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                // Antes de excluir, verificamos se o PersonalTrainer tem clientes associados
                if (personalTrainer.getClientes() != null && !personalTrainer.getClientes().isEmpty()) {
                    System.out.println("Este personal trainer possui clientes associados. Não é possível excluí-lo.");
                } else {
                    entityManager.remove(personalTrainer);
                    transaction.commit();
                    System.out.println("Personal trainer excluído com sucesso!");
                }
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
}

package RA3Design.crud;

import RA3Design.model.Cliente;
import RA3Design.model.PersonalTrainer;
import RA3Design.model.PlanoTreino;
import RA3Design.model.Reserva;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ClienteCrud {
    public static void createCliente() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            Cliente cliente = new Cliente();

            System.out.print("Digite o nome do cliente: ");
            cliente.setNome(scanner.nextLine());

            System.out.print("Digite o e-mail do cliente: ");
            cliente.setEmail(scanner.nextLine());

            System.out.print("Digite o telefone do cliente: ");
            cliente.setTelefone(scanner.nextLine());

            Date dataNascimento = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            while (dataNascimento == null) {
                System.out.print("Digite a data de nascimento do cliente (dd/MM/yyyy): ");
                String dataNascimentoStr = scanner.nextLine();
                try {
                    dataNascimento = sdf.parse(dataNascimentoStr);
                    cliente.setData_nascimento(dataNascimento);
                } catch (java.text.ParseException e) {
                    System.out.println("Erro ao converter a data de nascimento. Formato esperado: dd/MM/yyyy");
                }
            }

            entityManager.persist(cliente);
            entityManager.flush();

            System.out.print("Deseja associar um plano de treino ao cliente? (s/n): ");
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

                if (planoTreino == null) {
                    System.out.println("Plano de treino com os parâmetros fornecidos não encontrado.");
                } else {
                    cliente.setPlanoTreino(planoTreino);
                    planoTreino.setCliente(cliente);
                    entityManager.merge(cliente);
                    entityManager.merge(planoTreino);
                    System.out.println("Plano de treino associado com sucesso!");
                }
            }

            System.out.print("Deseja associar um personal trainer ao cliente? (s/n): ");
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
                    cliente.setPersonalTrainer(personalTrainer);
                    personalTrainer.getClientes().add(cliente);
                    entityManager.merge(personalTrainer);
                    entityManager.merge(cliente);
                    System.out.println("Personal trainer associado com sucesso!");
                } else {
                    System.out.println("Personal trainer com os parâmetros fornecidos não encontrado.");
                }
            }

            System.out.print("Deseja associar reservas ao cliente? (s/n): ");
            String respostaReserva = scanner.nextLine();
            if (respostaReserva.equalsIgnoreCase("s")) {
                List<Reserva> reservasCliente = new ArrayList<>();
                String continuar;
                do {
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
                        reserva.setCliente(cliente);
                        reservasCliente.add(reserva);
                        entityManager.merge(reserva);
                        entityManager.merge(cliente);
                        System.out.println("Reserva associada com sucesso!");
                    } else {
                        System.out.println("Reserva com a data e hora fornecidas não encontrada.");
                    }

                    System.out.print("Deseja associar outra reserva? (s/n): ");
                    continuar = scanner.nextLine();
                } while (continuar.equalsIgnoreCase("s"));

                cliente.setReservas(reservasCliente);
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

    public static void updateCliente() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o nome do cliente a ser atualizado: ");
            String nomeCliente = scanner.nextLine();

            System.out.print("Digite o e-mail do cliente a ser atualizado: ");
            String emailCliente = scanner.nextLine();

            Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);

            if (cliente != null) {
                System.out.println("Cliente encontrado. Atualize as informações ou pressione Enter para manter o valor atual.");

                System.out.print("Nome atual: " + cliente.getNome() + ". Novo nome (ou pressione Enter para manter): ");
                String nome = scanner.nextLine();
                if (!nome.isEmpty()) {
                    cliente.setNome(nome);
                }

                System.out.print("E-mail atual: " + cliente.getEmail() + ". Novo e-mail (ou pressione Enter para manter): ");
                String email = scanner.nextLine();
                if (!email.isEmpty()) {
                    cliente.setEmail(email);
                }

                System.out.print("Telefone atual: " + cliente.getTelefone() + ". Novo telefone (ou pressione Enter para manter): ");
                String telefone = scanner.nextLine();
                if (!telefone.isEmpty()) {
                    cliente.setTelefone(telefone);
                }

                System.out.print("Data de nascimento atual: " + cliente.getData_nascimento() + ". Nova data de nascimento (dd/MM/yyyy ou pressione Enter para manter): ");
                String dataNascimentoStr = scanner.nextLine();
                if (!dataNascimentoStr.isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        Date dataNascimento = sdf.parse(dataNascimentoStr);
                        cliente.setData_nascimento(dataNascimento);
                    } catch (java.text.ParseException e) {
                        System.out.println("Erro ao converter a data de nascimento. Formato esperado: dd/MM/yyyy");
                    }
                }

                System.out.print("Deseja atualizar o plano de treino do cliente? (s/n): ");
                String respostaPlano = scanner.nextLine();
                if (respostaPlano.equalsIgnoreCase("s")) {
                    PlanoTreino planoTreino = cliente.getPlanoTreino();
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

                        entityManager.merge(planoTreino);
                        System.out.println("Plano de treino atualizado com sucesso!");
                    } else {
                        System.out.println("Este cliente não possui um plano de treino associado.");
                    }
                }


                System.out.print("Deseja adicionar um novo relacionamento de plano de treino ao cliente? (s/n): ");
                String respostaPlano1 = scanner.nextLine();
                if (respostaPlano1.equalsIgnoreCase("s")) {
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
                        cliente.setPlanoTreino(planoTreino);
                        planoTreino.setCliente(cliente);
                        entityManager.merge(cliente);
                        entityManager.merge(planoTreino);
                        System.out.println("Relaciomento de plano de treino adicionado com sucesso!");
                    } else {
                        System.out.println("Plano de treino com os parâmetros fornecidos não encontrado.");
                    }
                }

                System.out.print("Deseja remover o relacionamento de plano de treino do cliente? (s/n): ");
                String respostaRemoverPlano = scanner.nextLine();
                if (respostaRemoverPlano.equalsIgnoreCase("s")) {
                    if (cliente.getPlanoTreino() != null) {
                        PlanoTreino planoTreino = cliente.getPlanoTreino();

                        cliente.setPlanoTreino(null);
                        planoTreino.setCliente(null);

                        entityManager.merge(cliente);
                        entityManager.merge(planoTreino);

                        System.out.println("Relacionamento de plano de treino removido com sucesso!");
                    } else {
                        System.out.println("Este cliente não possui um plano de treino associado.");
                    }
                }

                System.out.print("Deseja atualizar o personal trainer do cliente? (s/n): ");
                String respostaPersonal = scanner.nextLine();
                if (respostaPersonal.equalsIgnoreCase("s")) {
                    PersonalTrainer personalTrainer = cliente.getPersonalTrainer();
                    if (personalTrainer != null) {
                        System.out.print("Nome atual: " + personalTrainer.getNome()
                                + ". Novo nome (ou pressione Enter para manter): ");
                        String nome1 = scanner.nextLine();
                        if (!nome1.isEmpty()) {
                            personalTrainer.setNome(nome1);
                        }

                        System.out.print("Especialidade atual: " + personalTrainer.getEspecialidade()
                                + ". Nova especialidade (ou pressione Enter para manter): ");
                        String especialidade = scanner.nextLine();
                        if (!especialidade.isEmpty()) {
                            personalTrainer.setEspecialidade(especialidade);
                        }

                        System.out.print("Telefone atual: " + personalTrainer.getTelefone()
                                + ". Novo telefone (ou pressione Enter para manter): ");
                        String telefone1 = scanner.nextLine();
                        if (!telefone1.isEmpty()) {
                            personalTrainer.setTelefone(telefone1);
                        }
                        entityManager.merge(personalTrainer);
                        System.out.println("Personal trainer atualizado com sucesso!");
                    } else {
                        System.out.println("Este cliente não possui um personal trainer associado.");
                    }
                }

                System.out.print("Deseja adicionar um novo relacionamento de personal trainer ao cliente? (s/n): ");
                String respostaPersonal1 = scanner.nextLine();
                if (respostaPersonal1.equalsIgnoreCase("s")) {
                    System.out.print("Digite o nome do personal trainer que deseja adicionar: ");
                    String nomePersonal = scanner.nextLine();

                    System.out.print("Digite a especialidade do personal trainer: ");
                    String especialidade = scanner.nextLine();

                    System.out.print("Digite o telefone do personal trainer: ");
                    String telefonePersonal = scanner.nextLine();

                    PersonalTrainer personalTrainer = findPersonalByDetails(entityManager, nomePersonal, especialidade, telefonePersonal);
                    if (personalTrainer != null) {
                        cliente.setPersonalTrainer(personalTrainer);
                        personalTrainer.getClientes().add(cliente);
                        entityManager.merge(cliente);
                        entityManager.merge(personalTrainer);
                        System.out.println("Relaciomento de personal trainer adicionado com sucesso!");
                    } else {
                        System.out.println("Personal trainer com os parâmetros fornecidos não encontrado.");
                    }
                }

                System.out.print("Deseja remover o relacionamento de personal trainer do cliente? (s/n): ");
                String respostaRemoverPersonal = scanner.nextLine();
                if (respostaRemoverPersonal.equalsIgnoreCase("s")) {
                    if (cliente.getPersonalTrainer() != null) {
                        PersonalTrainer personalTrainer = cliente.getPersonalTrainer();

                        cliente.setPersonalTrainer(null);
                        personalTrainer.getClientes().remove(cliente);

                        entityManager.merge(cliente);
                        entityManager.merge(personalTrainer);

                        System.out.println("Relacionamento de personal trainer removido com sucesso!");
                    } else {
                        System.out.println("Este cliente não possui um personal trainer associado.");
                    }
                }



                System.out.print("Deseja atualizar as reservas do cliente? (s/n): ");
                String respostaReserva = scanner.nextLine();
                if (respostaReserva.equalsIgnoreCase("s")) {
                    List<Reserva> reservasCliente = cliente.getReservas();

                    if (reservasCliente.isEmpty()) {
                        System.out.println("Este cliente não possui reservas associadas.");
                    } else {
                        for (Reserva reserva : reservasCliente) {
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

                System.out.print("Deseja adicionar um novo relacionamento de reserva ao cliente? (s/n): ");
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
                        cliente.getReservas().add(reserva);
                        reserva.setCliente(cliente);
                        entityManager.merge(cliente);
                        entityManager.merge(reserva);

                        System.out.println("Relacionamento de reserva adicionado com sucesso!");
                    } else {
                        System.out.println("Reserva com os parâmetros fornecidos não encontrada.");
                    }

                    System.out.print("Deseja adicionar outra reserva? (s/n): ");
                    respostaReserva1 = scanner.nextLine();
                }

                System.out.print("Deseja remover uma reserva do cliente? (s/n): ");
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
                    if (reserva != null && cliente.getReservas().contains(reserva)) {
                        cliente.getReservas().remove(reserva);
                        reserva.setCliente(null);

                        entityManager.merge(cliente);
                        entityManager.merge(reserva);

                        System.out.println("Reserva removida com sucesso!");
                    } else {
                        System.out.println("Reserva com os parâmetros fornecidos não encontrada ou não está associada a este cliente.");
                    }

                    System.out.print("Deseja remover outra reserva? (s/n): ");
                    respostaRemoverReserva = scanner.nextLine();
                }


                transaction.commit();
                System.out.println("Cliente atualizado com sucesso!");

            } else {
                System.out.println("Cliente não encontrado.");
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

    public static void deleteCliente() {
        Scanner scanner = new Scanner(System.in);
        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            System.out.print("Digite o nome do cliente a ser excluído: ");
            String nomeCliente = scanner.nextLine();

            System.out.print("Digite o e-mail do cliente a ser excluído: ");
            String emailCliente = scanner.nextLine();

            Cliente cliente = findClienteByNomeEmail(entityManager, nomeCliente, emailCliente);

            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }

            if (cliente.getPlanoTreino() != null) {
                System.out.println("O cliente possui um plano de treino associado. Exclua o plano de treino antes de excluir o cliente.");
                return;
            }

            if (cliente.getReservas() != null && !cliente.getReservas().isEmpty()) {
                System.out.println("O cliente possui reservas associadas. Exclua as reservas antes de excluir o cliente.");
                return;
            }

            if (cliente.getPersonalTrainer() != null) {
                System.out.println("O cliente possui um personal trainer associado. Remova o vínculo com o personal trainer antes de excluir o cliente.");
                return;
            }

            System.out.println("Cliente encontrado: " + cliente.getNome() + " - " + cliente.getEmail());
            System.out.print("Tem certeza que deseja excluir este cliente? (s/n): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("s")) {
                entityManager.remove(cliente);
                transaction.commit();
                System.out.println("Cliente excluído com sucesso!");
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

    public static void readClientes() {
        EntityManager entityManager = null;
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("academiaPU");
            entityManager = emFactory.createEntityManager();

            List<Cliente> clientes = findAllClientes(entityManager);

            if (clientes == null || clientes.isEmpty()) {
                System.out.println("Nenhum cliente encontrado.");
            } else {
                System.out.println("Lista de Clientes:");
                for (Cliente cliente : clientes) {
                    System.out.println("=======================================");
                    System.out.println("Nome: " + cliente.getNome());
                    System.out.println("E-mail: " + cliente.getEmail());
                    System.out.println("Telefone: " + cliente.getTelefone());
                    System.out.println("Data de Nascimento: " + cliente.getData_nascimento());
                    if (cliente.getPlanoTreino() != null) {
                        System.out.println("Plano de Treino: ");
                        System.out.println("Descrição: " + cliente.getPlanoTreino().getDescricao());
                        System.out.println("Data de Início: " + cliente.getPlanoTreino().getDataInicio());
                        System.out.println("Data de Fim: " + cliente.getPlanoTreino().getDataFim());
                    } else {
                        System.out.println("Plano de Treino: Não atribuído.");
                    }
                    if (cliente.getReservas() != null && !cliente.getReservas().isEmpty()) {
                        System.out.println("Reservas: ");
                        for (Reserva reserva : cliente.getReservas()) {
                            System.out.println("Data: " + reserva.getDataReserva());
                            System.out.println("Hora: " + reserva.getHoraReserva());
                        }
                    } else {
                        System.out.println("Reservas: Não atribuídas.");
                    }
                    if (cliente.getPersonalTrainer() != null) {
                        System.out.println("Personal Trainer: ");
                        System.out.println("Nome: " + cliente.getPersonalTrainer().getNome());
                        System.out.println("Especialidade: " + cliente.getPersonalTrainer().getEspecialidade());
                        System.out.println("Telefone: " + cliente.getPersonalTrainer().getTelefone());
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


    private static List<Cliente> findAllClientes(EntityManager entityManager) {
        TypedQuery<Cliente> query = entityManager.createQuery("SELECT c FROM Cliente c", Cliente.class);
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

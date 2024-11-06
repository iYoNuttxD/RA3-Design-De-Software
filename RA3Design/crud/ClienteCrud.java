package RA3Design.crud;

import RA3Design.model.Cliente;
import jakarta.persistence.*;

import java.util.Date;
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

            System.out.print("Digite a data de nascimento do cliente (dd/MM/yyyy): ");
            String dataNascimentoStr = scanner.nextLine();
            Date dataNascimento = null;
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                dataNascimento = sdf.parse(dataNascimentoStr);
            } catch (java.text.ParseException e) {
                System.out.println("Erro ao converter a data de nascimento. Formato esperado: dd/MM/yyyy");
            }
            cliente.setData_nascimento(dataNascimento);

            entityManager.persist(cliente);
            entityManager.flush();
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
}

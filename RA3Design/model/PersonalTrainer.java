package RA3Design.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class PersonalTrainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;
    private String especialidade;
    private String telefone;

    @OneToMany(mappedBy = "personalTrainer")
    private List<Cliente> clientes;

    public PersonalTrainer(String nome, String especialidade, String telefone, List<Cliente> clientes) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.clientes = clientes;
    }

    public PersonalTrainer() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}

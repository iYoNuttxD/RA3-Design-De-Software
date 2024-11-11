package RA3Design.model;

import RA3Design.model.Cliente;
import RA3Design.model.PlanoTreino;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class PersonalTrainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String especialidade;
    private String telefone;

    @OneToMany(mappedBy = "personalTrainer")
    private List<PlanoTreino> planosTreino;

    @OneToMany(mappedBy = "personalTrainer")
    private List<Cliente> clientes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<PlanoTreino> getPlanosTreino() {
        return planosTreino;
    }

    public void setPlanoTreino(List<PlanoTreino> planosTreino) {
        this.planosTreino = planosTreino;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}

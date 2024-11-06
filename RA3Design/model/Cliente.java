package RA3Design.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Date;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String nome;
    private String email;
    private String telefone;
    private Date data_nascimento;

    @OneToOne
    @JoinColumn(name = "planotreino_id")
    private PlanoTreino planoTreino;

    @OneToMany(mappedBy = "cliente")
    private List<Reserva> reservas;

    @ManyToOne
    @JoinColumn(name = "personaltrainer_id")
    private PersonalTrainer personalTrainer;

    public Cliente(int id, String nome, String email, String telefone, Date data_nascimento, PlanoTreino planoTreino,
                   List<Reserva> reservas, PersonalTrainer personalTrainer) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.data_nascimento = data_nascimento;
        this.planoTreino = planoTreino;
        this.reservas = reservas;
        this.personalTrainer = personalTrainer;
    }

    // Getters e Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(Date data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public PlanoTreino getPlanoTreino() {
        return planoTreino;
    }

    public void setPlanoTreino(PlanoTreino planoTreino) {
        this.planoTreino = planoTreino;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public PersonalTrainer getPersonalTrainer() {
        return personalTrainer;
    }

    public void setPersonalTrainer(PersonalTrainer personalTrainer) {
        this.personalTrainer = personalTrainer;
    }
}

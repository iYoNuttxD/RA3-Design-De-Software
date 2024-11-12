package RA3Design.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private boolean disponibilidade;

    @OneToMany(mappedBy = "equipamento")
    private List<Reserva> reservas;

    public Equipamento(Long id, String nome, String categoria, boolean disponibilidade, List<Reserva> reservas) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.disponibilidade = disponibilidade;
        this.reservas = reservas;
    }

    public Equipamento(){}

    // Getters e Setters
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}

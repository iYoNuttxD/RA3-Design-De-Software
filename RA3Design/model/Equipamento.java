package RA3Design.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;
    private String descricao;
    private int quantidade;

    @OneToMany(mappedBy = "equipamento")
    private List<Reserva> reservas;

    public Equipamento(String nome, String descricao, int quantidade, List<Reserva> reservas) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.reservas = reservas;
    }

    public Equipamento() {}

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public void setCategoria(String s) {
    }

    public void setDisponibilidade(boolean b) {
    }
}

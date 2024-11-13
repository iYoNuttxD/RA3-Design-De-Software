package RA3Design.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private Date DataReserva;
    private Date HoraReserva;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;


    public Reserva(Date DataReserva, Date HoraReserva){
        this.DataReserva = DataReserva;
        this.HoraReserva = HoraReserva;
    }

    public Reserva(){}

    public int getId() {
        return id;
    }

    public Date getDataReserva() {
        return DataReserva;
    }

    public Date getHoraReserva() {
        return HoraReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setDataReserva(Date dataReserva) {
        DataReserva = dataReserva;
    }

    public void setHoraReserva(Date horaReserva) {
        HoraReserva = horaReserva;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }
}

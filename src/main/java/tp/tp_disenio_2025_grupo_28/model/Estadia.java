package tp.tp_disenio_2025_grupo_28.model;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoEstadia;

@Entity
@Table(name = "estadia")
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadia")
    private Integer idEstadia;

    // ----- Fechas y horas -----
    @Column(name = "hora_check_in")
    private Time horaCheckIn;

    @Column(name = "hora_check_out")
    private Time horaCheckOut;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_check_in")
    private Date fechaCheckIn;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_check_out")
    private Date fechaCheckOut;

    // ----- Estado -----
    @Enumerated(EnumType.STRING)
    //@Column(name = "estadoEstadia")
    @Column(name = "estado_estadia")
    private EstadoEstadia estado;

    // @Enumerated(EnumType.STRING)
    // private TipoEstadia tipo;
    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable = true)
    private ResponsablePago responsablePago;
    // BD: Tabla intermedia Estadia_Servicio
    @ManyToMany
    @JoinTable(
            name = "estadia_servicio",
            joinColumns = @JoinColumn(name = "id_estadia"),
            inverseJoinColumns = @JoinColumn(name = "id_servicio")
    )
    private List<Servicio> servicios;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    public Estadia() {
    }

    // Opcional: constructor completo
    public Estadia(Time horaCheckIn, Time horaCheckOut, Date fechaCheckIn, Date fechaCheckOut,
            EstadoEstadia estado, ResponsablePago responsablePago) {
        this.horaCheckIn = horaCheckIn;
        this.horaCheckOut = horaCheckOut;
        this.fechaCheckIn = fechaCheckIn;
        this.fechaCheckOut = fechaCheckOut;
        this.estado = estado;
        // this.tipo = tipo;
        this.responsablePago = responsablePago;
    }

    public Integer getIdEstadia() {
        return idEstadia;
    }

    public void setIdEstadia(Integer idEstadia) {
        this.idEstadia = idEstadia;
    }

    public Time getHoraCheckIn() {
        return horaCheckIn;
    }

    public void setHoraCheckIn(Time horaCheckIn) {
        this.horaCheckIn = horaCheckIn;
    }

    public Time getHoraCheckOut() {
        return horaCheckOut;
    }

    public void setHoraCheckOut(Time horaCheckOut) {
        this.horaCheckOut = horaCheckOut;
    }

    public Date getFechaCheckIn() {
        return fechaCheckIn;
    }

    public void setFechaCheckIn(Date fechaCheckIn) {
        this.fechaCheckIn = fechaCheckIn;
    }

    public Date getFechaCheckOut() {
        return fechaCheckOut;
    }

    public void setFechaCheckOut(Date fechaCheckOut) {
        this.fechaCheckOut = fechaCheckOut;
    }

    public EstadoEstadia getEstado() {
        return estado;
    }

    public void setEstado(EstadoEstadia estado) {
        this.estado = estado;
    }

    /*  public TipoEstadia getTipo() {
        return tipo;
    }

    public void setTipo(TipoEstadia tipo) {
        this.tipo = tipo;
    }*/
    public ResponsablePago getResponsablePago() {
        return responsablePago;
    }

    public void setResponsablePago(ResponsablePago responsablePago) {
        this.responsablePago = responsablePago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

}

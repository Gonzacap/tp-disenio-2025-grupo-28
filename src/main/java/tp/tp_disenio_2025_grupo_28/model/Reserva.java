package tp.tp_disenio_2025_grupo_28.model;

import java.util.ArrayList;
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    private String nombre;
    private String apellido;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_desde", nullable = false)
    private Date fechaDesde;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_hasta", nullable = false)
    private Date fechaHasta;

    @ManyToMany
    @JoinTable(
            name = "reserva_habitacion",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "numero_habitacion")
    )
    private List<Habitacion> habitaciones = new ArrayList<>();

    // @OneToMany(cascade = CascadeType.ALL)
    // @JoinColumn(name = "id_reserva")
    // private List<PersonaFisica> acompanantes;
    public Reserva() {
    }

    public Reserva(String nombre, String apellido, String telefono, Date fechaDesde, Date fechaHasta, List<Habitacion> habitaciones/*, List<PersonaFisica> acompanantes*/) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.estado = EstadoReserva.generada; // según tu enum
        this.habitaciones = habitaciones != null ? habitaciones : new ArrayList<>();
        // this.acompanantes = acompanantes != null ? acompanantes : new ArrayList<>();
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public Date getFechaDesde() {
        return this.fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return this.fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    // public List<PersonaFisica> getAcompanantes() {
    //     return acompanantes;
    // }
    // public void setAcompanantes(List<PersonaFisica> acompanantes) {
    //     this.acompanantes = acompanantes;
    // }
}

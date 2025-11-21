package tp.tp_disenio_2025_grupo_28.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

@Entity
@Table(name = "Reserva")
@Inheritance(strategy = InheritanceType.JOINED)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;
    private String nombre;
    private String apellido;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    // relacion con habitaciones: una reserva puede tener varias habitaciones
    @ManyToMany
    @JoinTable(
            name = "reserva_habitacion",
            joinColumns = @JoinColumn(name = "id_reserva"),
            inverseJoinColumns = @JoinColumn(name = "numero_habitacion")
    )
    private List<Habitacion> habitaciones;

    // acompañantes (solo datos básicos)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_reserva")
    private List<PersonaFisica> acompanantes;

    public Reserva(Integer idReserva, String nombre, String apellido, String telefono, EstadoReserva estado, List<Habitacion> habitaciones, List<PersonaFisica> acompanantes) {
        this.acompanantes = acompanantes;
        this.apellido = apellido;
        this.estado = estado;
        this.habitaciones = habitaciones;
        this.idReserva = idReserva;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Reserva() {

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

    public List<PersonaFisica> getAcompanantes() {
        return acompanantes;
    }

    public void setAcompanantes(List<PersonaFisica> acompanantes) {
        this.acompanantes = acompanantes;
    }

}

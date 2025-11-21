package tp.tp_disenio_2025_grupo_28.model;

import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;
import tp.tp_disenio_2025_grupo_28.model.*;
import java.util.*;
import java.util.stream.*;
import jakarta.persistence.*;

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

    // acompañantes (solo datos básicos)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_reserva")
    private List<PersonaFisica> acompanantes;



    private List<Habitacion> habitaciones;

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
    public List<Habitacion> getHabitacion() {
        return habitacion;
    }
    public void setHabitacion(List<Habitacion> habitacion) {
        this.habitacion = habitacion;
    }
    public List<PersonaFisica> getAcompañantes() {
        return acompañantes;
    }
    public void setAcompañantes(List<PersonaFisica> acompañantes) {
        this.acompañantes = acompañantes;
    }
}

package tp.tp_disenio_2025_grupo_28.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;

@Entity
@Table(name = "habitacion")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_habitacion")
    private Integer numeroHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHabitacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHabitacion estado;

    @Column(name = "numCamasSimples")
    private Integer numCamasSimples;

    @Column(name = "numCamasDobles")
    private Integer numCamasDobles;

    private Integer capacidad;

    @Column(name = "descuentoPorEstadiaLarga")
    private BigDecimal descuentoPorEstadiaLarga;

    public Habitacion() {
    }

    public Habitacion(Integer numeroHabitacion, TipoHabitacion tipo, EstadoHabitacion estado, Integer numCamasSimples,
            Integer numCamasDobles, Integer capacidad, BigDecimal descuentoPorEstadiaLarga) {
        this.numeroHabitacion = numeroHabitacion;
        this.tipo = tipo;
        this.estado = estado;
        this.numCamasSimples = numCamasSimples;
        this.numCamasDobles = numCamasDobles;
        this.capacidad = capacidad;
        this.descuentoPorEstadiaLarga = descuentoPorEstadiaLarga;
    }

    public Integer getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Integer getNumCamasSimples() {
        return numCamasSimples;
    }

    public void setNumCamasSimples(Integer numCamasSimples) {
        this.numCamasSimples = numCamasSimples;
    }

    public Integer getNumCamasDobles() {
        return numCamasDobles;
    }

    public void setNumCamasDobles(Integer numCamasDobles) {
        this.numCamasDobles = numCamasDobles;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getDescuentoPorEstadiaLarga() {
        return descuentoPorEstadiaLarga;
    }

    public void setDescuentoPorEstadiaLarga(BigDecimal descuentoPorEstadiaLarga) {
        this.descuentoPorEstadiaLarga = descuentoPorEstadiaLarga;
    }

}

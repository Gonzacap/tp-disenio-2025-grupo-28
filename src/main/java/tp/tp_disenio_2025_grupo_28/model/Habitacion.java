package tp.tp_disenio_2025_grupo_28.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import tp.tp_disenio_2025_grupo_28.model.enums.*;

@Entity
@Table(name = "habitacion")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_habitacion")
    private Integer numeroHabitacion;

    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoHabitacion tipo;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoHabitacion estado;

    @Column(name = "num_camas_simples")
    private Integer numCamasSimples;

    @Column(name = "num_camas_dobles")
    private Integer numCamasDobles;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "descuento_por_estadia_larga", precision = 10, scale = 2)
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

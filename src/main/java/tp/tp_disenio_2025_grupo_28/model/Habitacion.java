package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;

public class Habitacion {

    private Integer id;

    private Integer numeroHabitacion;
    @Enumerated(EnumType.STRING)
    private EstadoHabitacion estado;
    @Enumerated(EnumType.STRING)
    private TipoHabitacion tipo;
    private Integer numCamasDobles;
    private Integer numCamasSimples;
    private Integer capacidad;
    private Double descuentoPorEstadiaLarga;

    public Habitacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public Integer getNumCamasDobles() {
        return numCamasDobles;
    }

    public void setNumCamasDobles(Integer numCamasDobles) {
        this.numCamasDobles = numCamasDobles;
    }

    public Integer getNumCamasSimples() {
        return numCamasSimples;
    }

    public void setNumCamasSimples(Integer numCamasSimples) {
        this.numCamasSimples = numCamasSimples;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Double getDescuentoPorEstadiaLarga() {
        return descuentoPorEstadiaLarga;
    }

    public void setDescuentoPorEstadiaLarga(Double descuentoPorEstadiaLarga) {
        this.descuentoPorEstadiaLarga = descuentoPorEstadiaLarga;
    }

}

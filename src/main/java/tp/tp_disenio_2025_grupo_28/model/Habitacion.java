package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;

public class Habitacion {

    private Integer id;

    private Integereger numeroHabitacion;
    @Enumerated(EnumType.STRING)
    private EstadoHabitacion estado;
    @Enumerated(EnumType.STRING)
    private TipoHabitacion tipo;
    private Integereger numCamasDobles;
    private Integereger numCamasSimples;
    private Integereger capacidad;
    private Double descuentoPorEstadiaLarga;

    public Habitacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integereger getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integereger numeroHabitacion) {
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

    public Integereger getNumCamasDobles() {
        return numCamasDobles;
    }

    public void setNumCamasDobles(Integereger numCamasDobles) {
        this.numCamasDobles = numCamasDobles;
    }

    public Integereger getNumCamasSimples() {
        return numCamasSimples;
    }

    public void setNumCamasSimples(Integereger numCamasSimples) {
        this.numCamasSimples = numCamasSimples;
    }

    public Integereger getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integereger capacidad) {
        this.capacidad = capacidad;
    }

    public Double getDescuentoPorEstadiaLarga() {
        return descuentoPorEstadiaLarga;
    }

    public void setDescuentoPorEstadiaLarga(Double descuentoPorEstadiaLarga) {
        this.descuentoPorEstadiaLarga = descuentoPorEstadiaLarga;
    }

}

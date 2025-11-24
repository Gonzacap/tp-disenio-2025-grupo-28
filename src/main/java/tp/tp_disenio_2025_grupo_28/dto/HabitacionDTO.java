package tp.tp_disenio_2025_grupo_28.dto;

import java.math.BigDecimal;
// import java.util.*;
import tp.tp_disenio_2025_grupo_28.model.enums.*;

public class HabitacionDTO {

    private Integer numeroHabitacion;
    private TipoHabitacion tipo;
    private EstadoHabitacion estado;
    private Integer numCamasSimples;
    private Integer numCamasDobles;
    private Integer capacidad;
    private BigDecimal descuentoPorEstadiaLarga;

    // Constructores
    public HabitacionDTO() {
    }

    public Integer getNumeroHabitacion() {
        return this.numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public TipoHabitacion getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public EstadoHabitacion getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Integer getNumCamasSimples() {
        return this.numCamasSimples;
    }

    public void setNumCamasSimples(Integer numCamasSimples) {
        this.numCamasSimples = numCamasSimples;
    }

    public Integer getNumCamasDobles() {
        return this.numCamasDobles;
    }

    public void setNumCamasDobles(Integer numCamasDobles) {
        this.numCamasDobles = numCamasDobles;
    }

    public Integer getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getDescuentoPorEstadiaLarga() {
        return this.descuentoPorEstadiaLarga;
    }

    public void setDescuentoPorEstadiaLarga(BigDecimal descuentoPorEstadiaLarga) {
        this.descuentoPorEstadiaLarga = descuentoPorEstadiaLarga;
    }
}

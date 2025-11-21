package tp.tp_disenio_2025_grupo_28.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;

@Entity
@Table(name = "EstadoHabitacionPeriodo")
public class EstadoHabitacionPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEstadoHabitacionPeriodo")
    private Integer idEstadoHabitacionPeriodo;
    @Enumerated(EnumType.STRING)
    private EstadoHabitacion estado;

    @Column(name = "numeroHabitacion")
    private Integer numeroHabitacion;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    public EstadoHabitacionPeriodo() {
    }

    public Integer getIdEstadoHabitacionPeriodo() {
        return idEstadoHabitacionPeriodo;
    }

    public void setIdEstadoHabitacionPeriodo(Integer idEstadoHabitacionPeriodo) {
        this.idEstadoHabitacionPeriodo = idEstadoHabitacionPeriodo;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Integer getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(Integer numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

}

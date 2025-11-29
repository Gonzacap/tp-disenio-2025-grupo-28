package tp.tp_disenio_2025_grupo_28.model;

import java.util.Date;

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
    private Date fechaDesde;
    private Date fechaHasta;

    public EstadoHabitacionPeriodo() {
    }

    public EstadoHabitacionPeriodo(EstadoHabitacion estado, Date fechaDesde, Date fechaHasta, Integer numeroHabitacion) {
        this.estado = estado;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;

        this.numeroHabitacion = numeroHabitacion;
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

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

}

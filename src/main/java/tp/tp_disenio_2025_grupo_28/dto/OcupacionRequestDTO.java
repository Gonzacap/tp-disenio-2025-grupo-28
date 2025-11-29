package tp.tp_disenio_2025_grupo_28.dto;

import java.util.Date;

public class OcupacionRequestDTO {

    private Integer numeroHabitacion;
    private Date fechaDesde;
    private Date fechaHasta;

    public OcupacionRequestDTO() {
    }

    public OcupacionRequestDTO(Date fechaDesde, Date fechaHasta, Integer numeroHabitacion) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.numeroHabitacion = numeroHabitacion;
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

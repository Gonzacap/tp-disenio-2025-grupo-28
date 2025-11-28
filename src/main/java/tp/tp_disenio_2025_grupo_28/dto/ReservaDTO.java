package tp.tp_disenio_2025_grupo_28.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

public class ReservaDTO {

    private Integer idReserva;
    private String nombre;
    private String apellido;
    private String telefono;
    private EstadoReserva estado;
    private Date fechaDesde;
    private Date fechaHasta;

    private List<HabitacionDTO> habitaciones = new ArrayList<>();

    public ReservaDTO() {
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

    public List<HabitacionDTO> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<HabitacionDTO> habitaciones) {
        this.habitaciones = habitaciones;
    }
}

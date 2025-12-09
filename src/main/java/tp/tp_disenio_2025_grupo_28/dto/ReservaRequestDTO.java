package tp.tp_disenio_2025_grupo_28.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;

//esto es para los pasos 8-9 del cu 04
public class ReservaRequestDTO {

    private String nombre;
    private String apellido;
    private String telefono;
    private List<ReservaHabitacionDTO> habitaciones; // numeroHabitacion
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaDesde;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaHasta;
    private TipoHabitacion tipoHabitacion;

    public ReservaRequestDTO() {

    }

    public ReservaRequestDTO(String nombre, String apellido, String telefono,
            Date fechaDesde, Date fechaHasta,
            List<ReservaHabitacionDTO> habitaciones, TipoHabitacion tipoHabitacion) {
        this.apellido = apellido;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.habitaciones = habitaciones;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoHabitacion = tipoHabitacion;
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

    public List<ReservaHabitacionDTO> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<ReservaHabitacionDTO> habitaciones) {
        this.habitaciones = habitaciones;
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

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

}

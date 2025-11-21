package tp.tp_disenio_2025_grupo_28.dto;

import java.time.LocalDate;
import java.util.List;

//esto es para los pasos 8-9 del cu 04
public class ReservaRequestDTO {

    private String nombre;
    private String apellido;
    private String telefono;
    private List<Integer> habitaciones; // numeroHabitacion
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    public ReservaRequestDTO(String nombre, String apellido, String telefono, LocalDate fechaDesde, LocalDate fechaHasta, List<Integer> habitaciones) {
        this.apellido = apellido;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.habitaciones = habitaciones;
        this.nombre = nombre;
        this.telefono = telefono;
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

    public List<Integer> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Integer> habitaciones) {
        this.habitaciones = habitaciones;
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

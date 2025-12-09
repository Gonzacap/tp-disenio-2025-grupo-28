package tp.tp_disenio_2025_grupo_28.model.enums;

public enum EstadoHabitacion {

    disponible("disponible"),
    ocupada("ocupada"),
    reservada("reservada"),
    mantenimiento("mantenimiento");

    private final String nombre;

    EstadoHabitacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

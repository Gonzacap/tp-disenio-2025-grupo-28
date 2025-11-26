package tp.tp_disenio_2025_grupo_28.model.enums;

public enum TipoHabitacion {
    dobleSuperior("Doble Superior"), 
    superiorFamilyPlan("Superior Family Plan"),
    suiteDoble("Suite Doble"),
    individualEstandar("Individual Estandar"),
    dobleEstandar("Doble Estandar");

    private final String nombre;

    TipoHabitacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

package tp.tp_disenio_2025_grupo_28.model;

public class PersonaJuridica extends ResponsablePago {

    protected Long id;

    public PersonaJuridica() {
    }

    public PersonaJuridica(String cuit, String razonSocial, int telefono, Direccion direccion) {
        super(cuit, razonSocial, telefono, direccion);
    }

    @Override
    public boolean esMayorDeEdad() {
        // No aplica, pero se devuelve true por compatibilidad
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

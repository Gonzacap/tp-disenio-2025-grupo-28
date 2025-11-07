package tp.tp_disenio_2025_grupo_28.model;

public class PersonaJuridica extends ResponsablePago {

    protected Integer id;

    public PersonaJuridica() {
    }

    public PersonaJuridica(String cuit, String razonSocial, Integer telefono, Direccion direccion) {
        super(cuit, razonSocial, telefono, direccion);
    }

    @Override
    public boolean esMayorDeEdad() {
        // No aplica, pero se devuelve true por compatibilidad
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

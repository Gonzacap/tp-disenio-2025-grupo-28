package tp.tp_disenio_2025_grupo_28.model;

public class Pais {

    private int id;
    private String nombre;

    public Pais() {
    }

    public Pais(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

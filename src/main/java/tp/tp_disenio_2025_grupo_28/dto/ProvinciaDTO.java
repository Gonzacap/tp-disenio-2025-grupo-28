package tp.tp_disenio_2025_grupo_28.dto;

public class ProvinciaDTO {

    private String nombre;
    private PaisDTO pais;

    public ProvinciaDTO() {
    }

    public ProvinciaDTO(String nombre, PaisDTO pais) {
        this.nombre = nombre;
        this.pais = pais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PaisDTO getPais() {
        return pais;
    }

    public void setPais(PaisDTO pais) {
        this.pais = pais;
    }

}

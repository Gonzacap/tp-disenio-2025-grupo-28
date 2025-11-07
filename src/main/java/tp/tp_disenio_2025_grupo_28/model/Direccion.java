package tp.tp_disenio_2025_grupo_28.model;

public class Direccion {

    private int id;
    private String direccion;
    private int numero;
    private String depto;
    private String piso;
    private String nacionalidad;
    private Localidad localidad;

    public Direccion() {
    }

    public Direccion(String direccion, int numero, String depto, String piso, String nacionalidad, Localidad localidad) {

        this.direccion = direccion;
        this.numero = numero;
        this.depto = depto;
        this.piso = piso;
        this.nacionalidad = nacionalidad;
        this.localidad = localidad;
    }

    public Direccion modificarDireccion(String direccion, int numero, String depto, String piso, String nacionalidad) {
        this.direccion = direccion;
        this.numero = numero;
        this.depto = depto;
        this.piso = piso;
        this.nacionalidad = nacionalidad;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

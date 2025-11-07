package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;

@Entity
@Table(name = "responsablepago")
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected String cuit;
    protected String razonSocial;
    protected Integer telefono;
    protected Direccion direccion;

    public ResponsablePago() {
    }

    public ResponsablePago(String cuit, String razonSocial, Integer telefono, Direccion direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = null;
    }

    public abstract boolean esMayorDeEdad();

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

}

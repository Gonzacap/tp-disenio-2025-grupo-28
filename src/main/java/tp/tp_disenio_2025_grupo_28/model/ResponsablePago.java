package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;

@Entity
@Table(name = "responsablepago")
public abstract class ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    protected String cuit;
    protected String razonSocial;
    protected int telefono;
    protected Direccion direccion;

    public ResponsablePago() {
    }

    public ResponsablePago(String cuit, String razonSocial, int telefono, Direccion direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = direccion;
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

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

}

package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;

@Entity
@Table(name = "responsable_pago")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResponsablePago {

    @Id
    @Column(length = 20)
    private String cuit;

    @Column(name = "razon_social", length = 100)
    private String razonSocial;

    private Integer telefono;

    @ManyToOne
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    public ResponsablePago() {}

    public ResponsablePago(String cuit, String razonSocial, Integer telefono, Direccion direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y setters
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

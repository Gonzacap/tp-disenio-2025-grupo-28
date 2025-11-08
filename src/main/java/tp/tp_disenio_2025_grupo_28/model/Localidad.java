package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "localidad")
public class Localidad {

    private Integer id;

    private String nombre;
    private String codigoPostal;
    private Provincia provincia;

    public Localidad() {
    }

    public Localidad(String nombre, String codigoPostal, Provincia provincia) {
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
        this.provincia = provincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "localidad")
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String codigo_postal;

    @ManyToOne
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;

    public Localidad() {
    }

    public Localidad(String nombre, String codigo_postal, Provincia provincia) {
        this.nombre = nombre;
        this.codigo_postal = codigo_postal;
        this.provincia = provincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getcodigo_postal() {
        return codigo_postal;
    }

    public void setcodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
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

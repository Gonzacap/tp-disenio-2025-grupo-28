package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numUsuario;

    private String nombre;
    private String apellido;
    private Integer dni;
    private String contrasena;

    public Usuario() {

    }

    public Usuario(String apellido, String contrasena, Integer dni, String nombre, Integer numUsuario) {
        this.apellido = apellido;
        this.contrasena = contrasena;
        this.dni = dni;
        this.nombre = nombre;
        this.numUsuario = numUsuario;
    }

    public Integer getNumUsuario() {
        return numUsuario;
    }

    public void setNumUsuario(Integer numUsuario) {
        this.numUsuario = numUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

}

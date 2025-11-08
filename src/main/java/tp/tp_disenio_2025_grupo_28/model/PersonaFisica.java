package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;
import java.util.Date;

// import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;


@Entity
@Table(name = "personafisica")
public class PersonaFisica extends ResponsablePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String nombre;
    protected String apellido;
    // protected TipoDocumento tipoDocumento;
    protected String tipoDocumento;
    protected String documento;
    protected Date fechaNacimiento;

    public PersonaFisica() {
    }

    public PersonaFisica(String cuit, String razonSocial, Integer telefono, Direccion direccion,
            String nombre, String apellido, String tipoDocumento,
            String documento, Date fechaNacimiento) {
        super(cuit, razonSocial, telefono, direccion);
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        // this.fechaNacimiento = fechaNacimiento;
    }

    public boolean esMayorDeEdad() {
        if (fechaNacimiento == null) return false;

        long edadEnMs = new Date().getTime() - fechaNacimiento.getTime();
        int edad = (int) (edadEnMs / (1000L * 60 * 60 * 24 * 365));
        return edad >= 18;
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

    // public TipoDocumento getTipoDocumento() {
    //     return tipoDocumento;
    // }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    // public void setTipoDocumento(TipoDocumento tipoDocumento) {
    //     this.tipoDocumento = tipoDocumento;
    // }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    // public Date getFechaNacimiento() {
    //     return fechaNacimiento;
    // }

    // public void setFechaNacimiento(Date fechaNacimiento) {
    //     this.fechaNacimiento = fechaNacimiento;
    // }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

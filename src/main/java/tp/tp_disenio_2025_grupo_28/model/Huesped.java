package tp.tp_disenio_2025_grupo_28.model;

import java.util.Date;

import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

public class Huesped extends PersonaFisica {

    private String posicionFrenteAlIva;
    private String email;
    private String ocupacion;

    public Huesped() {
    }

    public Huesped(String cuit, String razonSocial, int telefono, Direccion direccion,
            String nombre, String apellido, TipoDocumento tipoDocumento,
            String documento, Date fechaNacimiento,
            String posicionFrenteAlIva, String email, String ocupacion) {
        super(cuit, razonSocial, telefono, direccion, nombre, apellido, tipoDocumento, documento, fechaNacimiento);
        this.posicionFrenteAlIva = posicionFrenteAlIva;
        this.email = email;
        this.ocupacion = ocupacion;
    }

    public String getPosicionFrenteAlIva() {
        return posicionFrenteAlIva;
    }

    public void setPosicionFrenteAlIva(String posicionFrenteAlIva) {
        this.posicionFrenteAlIva = posicionFrenteAlIva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

}

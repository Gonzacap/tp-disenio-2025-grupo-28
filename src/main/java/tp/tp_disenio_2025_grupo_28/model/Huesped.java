package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;
import java.util.*;


import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

@Entity
@Table(name = "huesped")
@PrimaryKeyJoinColumn(name = "cuit") // usa la PK de persona_fisica
public class Huesped extends PersonaFisica {

    @Column(name = "posicion_frente_al_iva", length = 50)
    private String posicionFrenteAlIva;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String ocupacion;

    // --- Constructores ---
    public Huesped() {
    }

    public Huesped(String cuit, String razonSocial, Integer telefono, Direccion direccion,
                   String nombre, String apellido, TipoDocumento tipoDocumento,
                   String documento, Date fechaNacimiento,
                   String posicionFrenteAlIva, String email, String ocupacion) {

        super(cuit, razonSocial, telefono, direccion, nombre, apellido, tipoDocumento, documento, fechaNacimiento);

        this.posicionFrenteAlIva = posicionFrenteAlIva;
        this.email = email;
        this.ocupacion = ocupacion;
    }

    // --- Getters y Setters ---

    public String getPosicionFrenteAlIva() {
        return this.posicionFrenteAlIva;
    }

	public void setPosicionFrenteAlIva(String posicionFrenteAlIva) {
		this.posicionFrenteAlIva = posicionFrenteAlIva;
	}

	public Integer getTelefono() {
		return this.telefono;
	}

	public void setTelefono(Integer telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOcupacion() {
		return this.ocupacion;
	}

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
}

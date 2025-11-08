package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "huesped")
public class Huesped {

    @Id
    private String cuit; // PK y FK al mismo tiempo

    @OneToOne
    @MapsId // Usa el mismo ID que la entidad relacionada
    @JoinColumn(name = "cuit") // FK a persona_fisica.cuit
    private PersonaFisica personaFisica;

    private String posicionFrenteAlIva;
    private Integer telefono;
    private String email;
    private String ocupacion;

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


    public Huesped() {}

    public Huesped(PersonaFisica personaFisica, String posicionFrenteAlIva, Integer telefono, String email, String ocupacion) {
        this.personaFisica = personaFisica;
        this.cuit = personaFisica.getCuit();
        this.posicionFrenteAlIva = posicionFrenteAlIva;
        this.telefono = telefono;
        this.email = email;
        this.ocupacion = ocupacion;
    }

    
}

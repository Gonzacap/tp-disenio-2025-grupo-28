package tp.tp_disenio_2025_grupo_28.dto;

import java.util.Date;

public class PersonaFisicaDTO {
    private Integer idPersonaFisica;
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String documento;
    private Integer idResponsablePago;

	public Integer getIdPersonaFisica() {
		return this.idPersonaFisica;
	}

	public void setIdPersonaFisica(Integer idPersonaFisica) {
		this.idPersonaFisica = idPersonaFisica;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getTipoDocumento() {
		return this.tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDocumento() {
		return this.documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public Integer getIdResponsablePago() {
		return this.idResponsablePago;
	}

	public void setIdResponsablePago(Integer idResponsablePago) {
		this.idResponsablePago = idResponsablePago;
	}


}

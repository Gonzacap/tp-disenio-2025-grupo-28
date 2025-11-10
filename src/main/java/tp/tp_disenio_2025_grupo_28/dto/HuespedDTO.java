package tp.tp_disenio_2025_grupo_28.dto;

import java.util.*;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

public class HuespedDTO {
    private String nombre;
    private String apellido;
    private TipoDocumento tipoDocumento;
    private String documento;
    private Date fechaNacimiento;
    private String cuit;
    private String posicionFrenteAlIva;
    private Date fechaNacimiento;
    private String telefono;
    private String email;
    private String ocupacion;

    // Datos de dirección (aplanados)
    private String direccion;
    private String numero;
    private String depto;
    private String piso;
    private String localidad;
    private String codigoPostal;
    private String provincia;
    private String pais;

    // Constructores
    public HuespedDTO() {}

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getPosicionFrenteAlIva() { return posicionFrenteAlIva; }
    public void setPosicionFrenteAlIva(String posicionFrenteAlIva) { this.posicionFrenteAlIva = posicionFrenteAlIva; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDepto() { return depto; }
    public void setDepto(String depto) { this.depto = depto; }

    public String getPiso() { return piso; }
    public void setPiso(String piso) { this.piso = piso; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}

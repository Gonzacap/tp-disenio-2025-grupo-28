package tp.tp_disenio_2025_grupo_28.dto;

import java.util.Date;

public class ResponsablePagoDTO {
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String documento;
    private String cuit;
    private String posicionFrenteAlIva;
    private Date fechaNacimiento;
    private Integer telefono;
    private String email;
    private String ocupacion;

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public String getPosicionFrenteAlIva() { return posicionFrenteAlIva; }
    public void setPosicionFrenteAlIva(String posicionFrenteAlIva) { this.posicionFrenteAlIva = posicionFrenteAlIva; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Integer getTelefono() { return telefono; }
    public void setTelefono(Integer telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
}

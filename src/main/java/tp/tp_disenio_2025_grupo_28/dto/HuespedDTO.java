package tp.tp_disenio_2025_grupo_28.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

public class HuespedDTO {

    private Integer id;
    private String nombre;
    private String apellido;
    private TipoDocumento tipoDocumento;
    private String documento;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;
    private String cuit;
    private String posicionFrenteAlIva;
    private String telefono;
    private String email;
    private String ocupacion;

    private DireccionDTO direccion;

    public HuespedDTO() {
        this.direccion = new DireccionDTO();
        this.direccion.setLocalidad(new LocalidadDTO());
        this.direccion.getLocalidad().setProvincia(new ProvinciaDTO());
        this.direccion.getLocalidad().getProvincia().setPais(new PaisDTO());
    }

    // Getters y setters
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

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getPosicionFrenteAlIva() {
        return posicionFrenteAlIva;
    }

    public void setPosicionFrenteAlIva(String posicionFrenteAlIva) {
        this.posicionFrenteAlIva = posicionFrenteAlIva;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

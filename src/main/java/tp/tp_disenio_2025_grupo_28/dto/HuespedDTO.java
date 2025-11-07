package tp.tp_disenio_2025_grupo_28.dto;

import java.sql.Date;

import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

public class HuespedDTO {

    public String nombre;
    public String apellido;
    public TipoDocumento tipoDocumento;
    public String documento;
    public String cuit;
    public String posicionFrenteAlIva;
    public Date fechaNacimiento;
    public int telefono;
    public String email;
    public String ocupacion;
    public Direccion direccion;
}

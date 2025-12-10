package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona_juridica")
@PrimaryKeyJoinColumn(name = "id")
public class PersonaJuridica extends ResponsablePago {

    public PersonaJuridica() {
    }

    public PersonaJuridica(String cuit, String razonSocial, String telefono, Direccion direccion) {
        super(cuit, razonSocial, telefono, direccion);
    }
}

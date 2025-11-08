package tp.tp_disenio_2025_grupo_28.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_juridica")
public class PersonaJuridica extends ResponsablePago {

    public PersonaJuridica() {
    }

    public PersonaJuridica(String cuit, String razonSocial, Integer telefono, Direccion direccion) {
        super(cuit, razonSocial, telefono, direccion);
    }
}

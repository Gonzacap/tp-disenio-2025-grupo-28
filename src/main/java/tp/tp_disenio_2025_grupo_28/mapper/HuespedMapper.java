package tp.tp_disenio_2025_grupo_28.mapper;

import tp.tp_disenio_2025_grupo_28.dto.HuespedDTO;
import tp.tp_disenio_2025_grupo_28.model.*;

public class HuespedMapper {

    public static HuespedDTO toDTO(Huesped h) {
        if (h == null) return null;

        HuespedDTO dto = new HuespedDTO();
        dto.setNombre(h.getNombre());
        dto.setApellido(h.getApellido());
        dto.setTipoDocumento(h.getTipoDocumento());
        dto.setDocumento(h.getDocumento());
        dto.setFechaNacimiento(h.getFechaNacimiento());
        dto.setEmail(h.getEmail());
        dto.setTelefono(h.getTelefono() != null ? h.getTelefono().toString() : "");
        dto.setOcupacion(h.getOcupacion());
        dto.setPosicionFrenteAlIva(h.getPosicionFrenteAlIva());

        // Navegamos las relaciones con null-safety
        Direccion d = h.getDireccion();
        if (d != null) {
            dto.setDireccion(d.getDireccion());
            Localidad loc = d.getLocalidad();
            if (loc != null) {
                dto.setLocalidad(loc.getNombre());
                Provincia prov = loc.getProvincia();
                if (prov != null) {
                    dto.setProvincia(prov.getNombre());
                    Pais pais = prov.getPais();
                    if (pais != null)
                        dto.setPais(pais.getNombre());
                }
            }
        }
        return dto;
    }

    public static Huesped toEntity(HuespedDTO dto) {
        if (dto == null) return null;

        Huesped h = new Huesped();
        h.setNombre(dto.getNombre());
        h.setApellido(dto.getApellido());
        h.setTipoDocumento(dto.getTipoDocumento());
        h.setDocumento(dto.getDocumento());
        h.setFechaNacimiento(dto.getFechaNacimiento());
        h.setEmail(dto.getEmail());
        h.setOcupacion(dto.getOcupacion());
        h.setPosicionFrenteAlIva(dto.getPosicionFrenteAlIva());
        h.setTelefono(dto.getTelefono());

        // direccion (Pais, Provincia, etc.), se setea en el service

        return h;
    }
}

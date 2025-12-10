package tp.tp_disenio_2025_grupo_28.mapper;

import tp.tp_disenio_2025_grupo_28.dto.DireccionDTO;
import tp.tp_disenio_2025_grupo_28.dto.HuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.LocalidadDTO;
import tp.tp_disenio_2025_grupo_28.dto.ProvinciaDTO;
import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.Localidad;
import tp.tp_disenio_2025_grupo_28.model.Pais;
import tp.tp_disenio_2025_grupo_28.model.Provincia;

public class HuespedMapper {

    public static HuespedDTO toDTO(Huesped h) {
        if (h == null) {
            return null;
        }

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
        dto.setCuit(h.getCuit());
        dto.setId(h.getId());

        Direccion d = h.getDireccion();
        if (d != null) {
            DireccionDTO dDto = dto.getDireccion();
            dDto.setDireccion(d.getDireccion());
            dDto.setNumero(d.getNumero());
            dDto.setDepto(d.getDepto());
            dDto.setPiso(d.getPiso());
            dDto.setNacionalidad(d.getNacionalidad());

            Localidad loc = d.getLocalidad();
            if (loc != null) {
                LocalidadDTO locDto = dDto.getLocalidad();
                locDto.setNombre(loc.getNombre());
                locDto.setCodigoPostal(loc.getCodigoPostal());

                Provincia prov = loc.getProvincia();
                if (prov != null) {
                    ProvinciaDTO provDto = locDto.getProvincia();
                    provDto.setNombre(prov.getNombre());

                    Pais pais = prov.getPais();
                    if (pais != null) {
                        provDto.getPais().setNombre(pais.getNombre());
                    }
                }
            }
        }

        return dto;
    }

    public static Huesped toEntity(HuespedDTO dto) {
        if (dto == null) {
            return null;
        }

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
        h.setCuit(dto.getCuit());
        h.setId(dto.getId());

        // direccion (Pais, Provincia, etc.), se setea en el service
        return h;
    }
}

package tp.tp_disenio_2025_grupo_28.mapper;

import tp.tp_disenio_2025_grupo_28.dto.HabitacionDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;

public class HabitacionMapper {

    public static HabitacionDTO toDTO(Habitacion h) {
        if (h == null) return null;

        HabitacionDTO dto = new HabitacionDTO();
        dto.setNumeroHabitacion(h.getNumeroHabitacion());
        dto.setTipo(h.getTipo());
        dto.setEstado(h.getEstado());
        dto.setNumCamasSimples(h.getNumCamasSimples());
        dto.setNumCamasDobles(h.getNumCamasDobles());
        dto.setCapacidad(h.getCapacidad());
        dto.setDescuentoPorEstadiaLarga(h.getDescuentoPorEstadiaLarga());

        return dto;
    }

    public static Habitacion toEntity(HabitacionDTO dto) {
        if (dto == null) return null;

        Habitacion h = new Habitacion();
        h.setNumeroHabitacion(dto.getNumeroHabitacion());
        h.setTipo(dto.getTipo());
        h.setEstado(dto.getEstado());
        h.setNumCamasSimples(dto.getNumCamasSimples());
        h.setNumCamasDobles(dto.getNumCamasDobles());
        h.setCapacidad(dto.getCapacidad());
        h.setDescuentoPorEstadiaLarga(dto.getDescuentoPorEstadiaLarga());

        return h;
    }
}

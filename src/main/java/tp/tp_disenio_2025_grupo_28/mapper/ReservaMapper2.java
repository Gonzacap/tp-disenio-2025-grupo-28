package tp.tp_disenio_2025_grupo_28.mapper;

import java.util.ArrayList;
import java.util.List;

import tp.tp_disenio_2025_grupo_28.dto.ReservaDTO;
import tp.tp_disenio_2025_grupo_28.dto.HabitacionDTO;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;

public class ReservaMapper2 {

    public static ReservaDTO toDTO(Reserva r) {
        if (r == null) return null;

        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(r.getIdReserva());
        dto.setNombre(r.getNombre());
        dto.setApellido(r.getApellido());
        dto.setTelefono(r.getTelefono());
        dto.setEstado(r.getEstado());
        dto.setFechaDesde(r.getFechaDesde());
        dto.setFechaHasta(r.getFechaHasta());

        List<HabitacionDTO> habitacionesDTO = new ArrayList<>();
        if (r.getHabitaciones() != null) {
            for (Habitacion h : r.getHabitaciones()) {
                habitacionesDTO.add(HabitacionMapper.toDTO(h));
            }
        }
        dto.setHabitaciones(habitacionesDTO);

        return dto;
    }

    public static Reserva toEntity(ReservaDTO dto) {
        if (dto == null) return null;

        Reserva r = new Reserva();
        r.setIdReserva(dto.getIdReserva());
        r.setNombre(dto.getNombre());
        r.setApellido(dto.getApellido());
        r.setTelefono(dto.getTelefono());
        r.setEstado(dto.getEstado());
        r.setFechaDesde(dto.getFechaDesde());
        r.setFechaHasta(dto.getFechaHasta());

        List<Habitacion> habitaciones = new ArrayList<>();
        if (dto.getHabitaciones() != null) {
            for (HabitacionDTO hDTO : dto.getHabitaciones()) {
                habitaciones.add(HabitacionMapper.toEntity(hDTO));
            }
        }
        r.setHabitaciones(habitaciones);

        return r;
    }
}

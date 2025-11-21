package tp.tp_disenio_2025_grupo_28.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaResponseDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

//DTO-->Entidad
//Entidad --> DTO
@Component
public class ReservaMapper {

    public Reserva toEntity(ReservaRequestDTO dto, List<Habitacion> habitaciones) {
        Reserva r = new Reserva();
        r.setNombre(dto.getNombre());
        r.setApellido(dto.getApellido());
        r.setTelefono(dto.getTelefono());
        r.setHabitaciones(habitaciones);
        r.setEstado(EstadoReserva.generada);
        return r;
    }

    public ReservaResponseDTO toDTO(Reserva reserva) {

        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setEstado(reserva.getEstado().name());
        dto.setHabitacionesReservadas(
                reserva.getHabitaciones().stream()
                        .map(Habitacion::getNumeroHabitacion)
                        .toList()
        );
        return dto;
    }
}

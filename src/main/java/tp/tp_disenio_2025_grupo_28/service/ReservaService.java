package tp.tp_disenio_2025_grupo_28.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp.tp_disenio_2025_grupo_28.dto.ReservaHabitacionDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaResponseDTO;
import tp.tp_disenio_2025_grupo_28.mapper.ReservaMapper;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.Usuario;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    private HabitacionRepository habitacionRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private EstadoHabitacionPeriodoRepository periodoRepo;

    @Autowired
    private ReservaMapper mapper;
    @Autowired
    private EstadoHabitacionPeriodoService estadoPeriodoService;

    //CU04
    public ReservaResponseDTO reservar(ReservaRequestDTO dto, Usuario usuario) {
        //Evaluamos la precondicion del cu
        if (usuario == null) {
            throw new RuntimeException("Debe estar autenticado");
        }

        validarDisponibilidadReserva(dto);
        List<Integer> numerosHabitacion = dto.getHabitaciones().stream().map(ReservaHabitacionDTO::getNumeroHabitacion).toList();
        List<Habitacion> habitaciones = habitacionRepo.findAllByNumeroHabitacionIn(numerosHabitacion);

        if (habitaciones.size() != dto.getHabitaciones().size()) {
            throw new RuntimeException("Una o más habitaciones no existen");
        }
        //Creamos la reserva
        Reserva reserva = mapper.toEntity(dto, habitaciones);
        reservaRepo.save(reserva);
        // Registrar períodos RESERVADOS (CU04 paso 4)
        for (Habitacion h : habitaciones) {
            estadoPeriodoService.registrarReserva(
                    h.getNumeroHabitacion(),
                    dto.getFechaDesde(),
                    dto.getFechaHasta()
            );
        }

        return mapper.toDTO(reserva);
    }
    //CU04 --> PASO 3 Y 3.B

    private void validarDisponibilidadReserva(ReservaRequestDTO dto) {
        //Valido que todas las reservas tienen el mismo rango de fechas
        //cuando reserva habitaciones a la vez 
        Date baseDesde = dto.getFechaDesde();
        Date baseHasta = dto.getFechaHasta();

        for (ReservaHabitacionDTO h : dto.getHabitaciones()) {
            if (!h.getFechaDesde().equals(baseDesde) || !h.getFechaHasta().equals(baseHasta)) {
                throw new RuntimeException("Todas las habitaciones deben tener el mismo rango de fechas.");
            }
        }
        //Valido disponibilidad actual
        for (ReservaHabitacionDTO nroHab : dto.getHabitaciones()) {

            boolean disponible = estadoPeriodoService.estaDisponible(
                    nroHab.getNumeroHabitacion(),
                    nroHab.getFechaDesde(),
                    nroHab.getFechaHasta()
            );

            if (!disponible) {
                throw new RuntimeException("La habitación " + nroHab.getNumeroHabitacion() + " no está disponible en el período seleccionado"
                );
            }
        }
    }
}

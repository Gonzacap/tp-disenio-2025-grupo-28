package tp.tp_disenio_2025_grupo_28.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaResponseDTO;
import tp.tp_disenio_2025_grupo_28.mapper.ReservaMapper;
import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.Usuario;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
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

    public ReservaResponseDTO reservar(ReservaRequestDTO dto, Usuario usuario) {
        //Evaluamos la precondicion del cu
        /*(usuario == NULL){
            throw new RuntimeException("Debe estar autenticado");
        }*/
        //buscamos disponibilidad cu 05
        List<Habitacion> disponibles = habitacionRepo.buscarHabitacionesDisponibles(dto.getFechaDesde(), dto.getFechaHasta());
        //validamos que esten todas disponibles
        for (Integer nro : dto.getHabitaciones()) {
            boolean esta = disponibles.stream().anyMatch(h -> h.getNumeroHabitacion().equals(nro));
            if (!esta) {
                throw new RuntimeException("La habitacion " + nro + " no está disponible.");
            }
        }

        //buscamos los objetos de las habitacion completos
        List<Habitacion> habitaciones = habitacionRepo.findAllById(dto.getHabitaciones());

        //Creamos la reserva
        Reserva reserva = mapper.toEntity(dto, habitaciones);
        reservaRepo.save(reserva);

        //Registramos los periodos
        for (Habitacion h : habitaciones) {
            EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo();
            p.setEstado(EstadoHabitacion.reservada);
            p.setNumeroHabitacion(h.getNumeroHabitacion());
            p.setFechaDesde(dto.getFechaDesde());
            p.setFechaHasta(dto.getFechaHasta());
            periodoRepo.save(p);
        }
        return mapper.toDTO(reserva);
    }
}

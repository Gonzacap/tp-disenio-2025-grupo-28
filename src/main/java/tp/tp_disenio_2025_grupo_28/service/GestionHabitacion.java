package tp.tp_disenio_2025_grupo_28.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.dto.*;
import tp.tp_disenio_2025_grupo_28.mapper.*;
import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.model.enums.*;
import tp.tp_disenio_2025_grupo_28.repository.*;

@Service
@Transactional
public class GestionHabitacion {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    // -----------------------
    // VALIDACION FECHAS
    // -----------------------
    public void validarFecha(Date fechaDesde, Date fechaHasta) {

        if (fechaDesde == null || fechaHasta == null)
            throw new IllegalArgumentException("Debe ingresar ambas fechas.");

        if (fechaDesde.after(fechaHasta))
            throw new IllegalArgumentException("La fecha 'Desde' no puede ser mayor que 'Hasta'.");
    }


    // -----------------------
    // ESTADO DE HABITACIONES
    // -----------------------
    public List<HabitacionEstadoDTO> mostrarEstadoHabitaciones(Date fechaDesde, Date fechaHasta) {

        validarFecha(fechaDesde, fechaHasta);

        List<Habitacion> habitaciones = habitacionRepository.findAll();

        List<Reserva> reservas = reservaRepository.findReservasEntreFechas(fechaDesde, fechaHasta);

        // resultado final
        List<HabitacionEstadoDTO> resultado = new ArrayList<>();

        for (Habitacion hab : habitaciones) {

            HabitacionEstadoDTO dto = new HabitacionEstadoDTO();
            dto.setNumeroHabitacion(hab.getNumeroHabitacion());
            dto.setTipo(hab.getTipo().name());

            Map<Date, String> mapaEstados = new LinkedHashMap<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaDesde);

            while (!cal.getTime().after(fechaHasta)) {

                Date dia = cal.getTime();

                String estado = "disponible";

                // buscar si hay una reserva que toque este día
                for (Reserva r : reservas) {
                    if (r.getHabitaciones().contains(hab)) {

                        if (!dia.before(r.getFechaDesde()) && !dia.after(r.getFechaHasta())) {

                            if (r.getEstado() == EstadoReserva.confirmada)
                                estado = "reservada";
                            else if (r.getEstado() == EstadoReserva.cumplida)
                                estado = "ocupada";
                            else if (r.getEstado() == EstadoReserva.cancelada)
                                estado = "disponible";
                        }
                    }
                }

                mapaEstados.put(dia, estado);

                cal.add(Calendar.DATE, 1);
            }

            dto.setEstadosPorFecha(mapaEstados);
            resultado.add(dto);
        }

        return resultado;
    }
}

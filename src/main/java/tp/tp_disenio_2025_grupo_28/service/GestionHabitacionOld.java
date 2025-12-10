package tp.tp_disenio_2025_grupo_28.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
@Transactional
public class GestionHabitacionOld {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public void validarFecha(Date fechaDesde, Date fechaHasta) {

        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }

        if (fechaDesde.after(fechaHasta)) {
            throw new IllegalArgumentException("La fecha Desde no puede ser posterior a la fecha Hasta.");
        }
    }

    public List<Map<String, Object>> obtenerHabitacionPorTipoMockup() {
        return Arrays.stream(TipoHabitacion.values())
                .map(tipo -> Map.of(
                "nombre", tipo.getNombre(),
                "habitaciones", List.of()
        )).collect(Collectors.toList());
    }

    public List<Habitacion> obtenerHabitaciones() {

        // Traemos las habitaciones directamente como entidades
        return habitacionRepository
                .findAllByOrderByTipoAscNumeroHabitacionAsc();
    }

    public List<Map<String, Object>> obtenerHabitacionPorTipo(List<Habitacion> habitaciones) {

        return Arrays.stream(TipoHabitacion.values())
                .map(tipo -> {

                    // Filtramos habitaciones del tipo actual
                    List<Integer> nums = habitaciones.stream()
                            .filter(h -> h.getTipo().equals(tipo))
                            .map(Habitacion::getNumeroHabitacion)
                            .toList();

                    // Creamos el Map para este tipo
                    return Map.<String, Object>of(
                            "nombre", tipo.getNombre(),
                            "habitaciones", nums
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Date> generarDiasEntre(Date desde, Date hasta) {
        List<Date> dias = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(desde);

        while (!c.getTime().after(hasta)) {
            dias.add(c.getTime());
            c.add(Calendar.DATE, 1);
        }

        return dias;
    }

    public List<Map<String, Object>> grilla(
            List<Map<String, Object>> habitacionesPorTipo,
            List<Habitacion> habitaciones,
            List<Date> dias
    ) {

        // Traemos reservas que afectan a las habitaciones del hotel
        List<Reserva> reservas = reservaRepository.findByEstadoNot(EstadoReserva.cancelada);

        // Preprocesamos reservas para buscar rápido
        Map<Integer, List<Reserva>> reservasPorHabitacion = new HashMap<>();

        for (Reserva r : reservas) {
            for (Habitacion h : r.getHabitaciones()) {
                reservasPorHabitacion
                        .computeIfAbsent(h.getNumeroHabitacion(), x -> new ArrayList<>())
                        .add(r);
            }
        }

        List<Map<String, Object>> salida = new ArrayList<>();

        for (Date dia : dias) {

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", dia);

            List<Map<String, Object>> estadosPorTipo = new ArrayList<>();

            // Iteramos por tipo de habitación (Individual, Doble, etc)
            for (Map<String, Object> tipoHab : habitacionesPorTipo) {

                String nombreTipo = (String) tipoHab.get("nombre");
                List<Integer> numeros = (List<Integer>) tipoHab.get("habitaciones");

                List<String> estados = new ArrayList<>();

                for (Integer numero : numeros) {

                    // Buscamos la entidad Habitacion para ese número
                    Habitacion h = habitaciones.stream()
                            .filter(x -> x.getNumeroHabitacion().equals(numero))
                            .findFirst()
                            .orElse(null);

                    if (h == null) {
                        estados.add("FUERA_SERVICIO");
                        continue;
                    }

                    // Estado por defecto
                    String estado = "DISPONIBLE";

                    // Verificamos reservas que afecten este día
                    List<Reserva> reservasDeEstaHab
                            = reservasPorHabitacion.getOrDefault(h.getNumeroHabitacion(), List.of());

                    for (Reserva r : reservasDeEstaHab) {
                        boolean afecta = !dia.before(r.getFechaDesde())
                                && !dia.after(r.getFechaHasta());

                        if (afecta) {
                            estado = r.getEstado().name(); // OCUPADA / RESERVADA / etc
                            break;
                        }
                    }

                    estados.add(estado);
                }

                Map<String, Object> bloqueTipo = new HashMap<>();
                bloqueTipo.put("tipo", nombreTipo);
                bloqueTipo.put("estados", estados);

                estadosPorTipo.add(bloqueTipo);
            }

            fila.put("estadosPorTipo", estadosPorTipo);
            salida.add(fila);
        }

        return salida;
    }

}

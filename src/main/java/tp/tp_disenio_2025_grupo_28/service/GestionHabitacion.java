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

import tp.tp_disenio_2025_grupo_28.dto.OcupacionHuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.mapper.OcupacionMapper;
import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
@Transactional
public class GestionHabitacion {

    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private EstadoHabitacionPeriodoRepository estadoPeriodoRepository;
    @Autowired
    private EstadoHabitacionPeriodoService estadoPeriodoService;
    @Autowired
    private HuespedRepository huespedRepository;

    private Map<String, List<PersonaFisica>> ocupantesAsignados = new HashMap<>();

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
                        "habitaciones", List.of()))
                .collect(Collectors.toList());
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
                            "habitaciones", nums);
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
            List<Date> dias) {

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
                    List<Reserva> reservasDeEstaHab = reservasPorHabitacion.getOrDefault(h.getNumeroHabitacion(),
                            List.of());

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
                bloqueTipo.put("habitaciones", numeros); // PRUEBA: BORRAR SI NO FUNCIONA
                estadosPorTipo.add(bloqueTipo);
            }
            fila.put("estadosPorTipo", estadosPorTipo);
            salida.add(fila);
        }

        return salida;
    }

    public Habitacion buscarPorNumero(Integer numero) {
        return habitacionRepository.findById(numero).orElse(null);
    }

    public boolean estaDisponible(Integer nroHabitacion, Date desde, Date hasta) {
        List<Reserva> reservas = reservaRepository.findByHabitacion(nroHabitacion);
        if (reservas == null || reservas.isEmpty()) {
            return true;
        }
        for (Reserva r : reservas) {

            Date rDesde = r.getFechaDesde();
            Date rHasta = r.getFechaHasta();
            // Si las habitaciones se solapan no estan disponibles
            boolean seSolapa = !(hasta.before(rDesde) || desde.after(rHasta));
            if (seSolapa) {
                return false;
            }
        }
        return true; // ningun choque, esta disponible

    }

    public boolean existeHabitacion(Integer numero) {
        return habitacionRepository.existsById(numero);
    }

    public List<Integer> habitacionesInexistentes(List<Integer> numeros) {
        return numeros.stream()
                .filter(n -> !habitacionRepository.existsById(n))
                .toList();
    }

    public List<Integer> habitacionesNoDisponibles(List<Integer> numeros, Date desde, Date hasta) {

        List<Integer> noDisp = new ArrayList<>();

        for (Integer n : numeros) {
            if (!estaDisponible(n, desde, hasta)) {
                noDisp.add(n);
            }
        }
        return noDisp;
    }

    // funciones del caso de uso 15
    public void ocuparHabitacion(Integer idReserva, OcupacionRequestDTO request, OcupacionHuespedDTO huespedes) {

        validarFecha(request.getFechaDesde(), request.getFechaHasta());
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva con ID " + idReserva));

        boolean habitacionPertenece = reserva.getHabitaciones().stream()
                .anyMatch(h -> h.getNumeroHabitacion().equals(request.getNumeroHabitacion()));

        if (!habitacionPertenece) {
            throw new IllegalArgumentException("La habitación "
                    + request.getNumeroHabitacion() + " no está asociada a esta reserva.");
        }

        boolean disponible = estadoPeriodoService.estaDisponible(
                request.getNumeroHabitacion(),
                request.getFechaDesde(),
                request.getFechaHasta());

        if (!disponible) {
            throw new IllegalStateException("La habitación ya está ocupada/reservada en ese periodo.");
        }

        // Crear periodo de ocupación
        EstadoHabitacionPeriodo periodo = OcupacionMapper.toPeriodo(request);
        estadoPeriodoRepository.save(periodo);

        // GUARDAR LOS OCUPANTES SELECCIONADOS
        List<PersonaFisica> ocupantes = new ArrayList<>();

        PersonaFisica responsable = reservaRepository.buscarPersonaFisicaPorId(huespedes.getIdHuesped());
        ocupantes.add(responsable);

        if (huespedes.getIdAcompanantes() != null) {
            for (Integer idAcomp : huespedes.getIdAcompanantes()) {
                PersonaFisica acomp = reservaRepository.buscarPersonaFisicaPorId(idAcomp);
                ocupantes.add(acomp);
            }
        }

        String key = idReserva + "_" + request.getNumeroHabitacion();
        ocupantesAsignados.put(key, ocupantes);

        // Cambiar estado de la reserva
        if (reserva.getEstado() == EstadoReserva.confirmada) {
            reserva.setEstado(EstadoReserva.cumplida);
            reservaRepository.save(reserva);
        }
    }

    public List<EstadoHabitacionPeriodo> buscarOcupacionesPorHabitacionYFecha(Integer numeroHabitacion, Date fecha) {

        return estadoPeriodoRepository.findByNumeroHabitacion(numeroHabitacion)
                .stream()
                .filter(p -> !fecha.before(p.getFechaDesde()) && !fecha.after(p.getFechaHasta())).toList();
    }

    public List<EstadoHabitacionPeriodo> buscarOcupacionesPorRango(Integer numeroHabitacion, Date desde, Date hasta) {

        return estadoPeriodoRepository.findByNumeroHabitacion(numeroHabitacion).stream()
                .filter(p -> {
                    boolean solapa = !(hasta.before(p.getFechaDesde()) || desde.after(p.getFechaHasta()));
                    return solapa && p.getEstado() != EstadoHabitacion.disponible;
                }).toList();
    }

    // Grilla
    public EstadoHabitacion obtenerEstadoEnFecha(Integer numeroHabitacion, Date fecha) {

        List<EstadoHabitacionPeriodo> periodos = buscarOcupacionesPorHabitacionYFecha(numeroHabitacion, fecha);

        if (periodos.isEmpty()) {
            return EstadoHabitacion.disponible;
        }

        // Si hay varios, nos quedamos con el más "restrictivo"
        return periodos.stream()
                .map(EstadoHabitacionPeriodo::getEstado)
                .filter(e -> e != EstadoHabitacion.disponible)
                .findFirst()
                .orElse(EstadoHabitacion.disponible);
    }

    public EstadoHabitacionPeriodo obtenerOcupacionActual(Integer numeroHabitacion) {
        Date hoy = new Date();

        return buscarOcupacionesPorHabitacionYFecha(numeroHabitacion, hoy)
                .stream()
                .filter(p -> p.getEstado() == EstadoHabitacion.ocupada)
                .findFirst()
                .orElse(null);
    }

    public boolean existeOcupacionEnRango(Integer numeroHabitacion, Date desde, Date hasta) {
        return !buscarOcupacionesPorRango(numeroHabitacion, desde, hasta).isEmpty();
    }

    public void liberarHabitacion(Integer numeroHabitacion, Date fecha) {

        EstadoHabitacionPeriodo periodo = obtenerOcupacionActual(numeroHabitacion);

        if (periodo == null) {
            throw new IllegalStateException("La habitación no está ocupada actualmente.");
        }

        periodo.setFechaHasta(fecha);
        estadoPeriodoRepository.save(periodo);

        Habitacion h = habitacionRepository.findById(numeroHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitación inexistente."));

        h.setEstado(EstadoHabitacion.disponible);
        habitacionRepository.save(h);
    }

    public List<PersonaFisica> obtenerOcupantesAsignados(Integer reservaId, Integer numeroHabitacion) {
        String key = reservaId + "_" + numeroHabitacion;
        return ocupantesAsignados.getOrDefault(key, new ArrayList<>());
    }

    public Integer calcularLugaresDisponibles(Integer numeroHabitacion, Integer reservaId) {
        Habitacion h = habitacionRepository.findById(numeroHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no existe"));

        int capacidad = h.getCapacidad();

        List<PersonaFisica> asignados = obtenerOcupantesAsignados(reservaId, numeroHabitacion);

        return capacidad - asignados.size();
    }

    public Map<String, String> obtenerResponsableReserva(Integer reservaId) {
        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva inexistente"));

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", r.getNombre());
        datos.put("apellido", r.getApellido());
        datos.put("telefono", r.getTelefono());

        return datos;
    }

    public List<PersonaFisica> buscarOcupantes(String nombre, String apellido, TipoDocumento tipoDocumento,
            String numeroDocumento) {
        return huespedRepository.buscarHuespedes(nombre, apellido, tipoDocumento, numeroDocumento);
    }

}

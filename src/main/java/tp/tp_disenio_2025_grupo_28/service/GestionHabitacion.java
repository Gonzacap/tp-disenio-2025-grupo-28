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
import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.PersonaFisicaRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
// @Transactional
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
    private PersonaFisicaRepository personaFisicaRepository;
    @Autowired
    private EstadiaService estadiaService;

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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<Map<String, Object>> grilla(List<Map<String, Object>> habitacionesPorTipo, List<Habitacion> habitaciones, List<Date> dias) {

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

    @Transactional(readOnly = true)
    public Habitacion buscarPorNumero(Integer numero) {
        return habitacionRepository.findById(numero).orElse(null);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public boolean existeHabitacion(Integer numero) {
        return habitacionRepository.existsById(numero);
    }

    @Transactional(readOnly = true)
    public List<Integer> habitacionesNoDisponibles(List<Integer> numeros, Date desde, Date hasta) {

        List<Integer> noDisp = new ArrayList<>();

        for (Integer n : numeros) {
            if (!estaDisponible(n, desde, hasta)) {
                noDisp.add(n);
            }
        }
        return noDisp;
    }

    // ----------------- CU15: OCUPAR HABITACIÓN -----------------
    /*  @Transactional
    public void ocuparHabitacion(Integer idReserva, OcupacionRequestDTO request, OcupacionHuespedDTO huespedes, boolean forzar) {

        validarFecha(request.getFechaDesde(), request.getFechaHasta());

        // ---- CHEQUEO DE DISPONIBILIDAD ----
        Reserva reservaExistente = null;
        if (idReserva != null) {
            reservaExistente = reservaRepository.findById(idReserva)
                    .orElse(null); // no lanzar excepción
        }

        if (reservaExistente != null && !reservaExistente.getIdReserva().equals(idReserva) && !forzar) {
            throw new IllegalStateException(
                    "La habitación está ocupada o reservada por otra reserva en ese período."
            );
        }

        // ---- BUSCAR RESERVA ----
        Reserva reserva = null;
        if (idReserva != null) {
            reserva = reservaRepository.findById(idReserva).orElse(null); // no lanzar excepción
            if (reserva == null) {
                PersonaFisica responsable = personaFisicaRepository.findById(huespedes.getIdHuesped()).orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado"));
                List<PersonaFisica> acompanantes = new ArrayList<>();
                if (huespedes.getIdAcompanantes() != null) {
                    for (String idAc : huespedes.getIdAcompanantes()) {
                        PersonaFisica acomp = personaFisicaRepository.findById(idAc)
                                .orElseThrow(() -> new IllegalArgumentException("Acompañante no encontrado"));
                        acompanantes.add(acomp);
                    }
                }
                Habitacion hab = habitacionRepository.findById(request.getNumeroHabitacion())
                        .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));

                //Creamos una nueva reserva
                Reserva nueva = new Reserva();
                nueva.setNombre(responsable.getNombre());
                nueva.setApellido(responsable.getApellido());
                nueva.setTelefono(responsable.getTelefono());
                nueva.setHabitaciones(List.of(hab));
                nueva.setAcompanantes(acompanantes);
                nueva.setFechaDesde(request.getFechaDesde());
                nueva.setFechaHasta(request.getFechaHasta());
                nueva.setEstado(EstadoReserva.generada);
                nueva = reservaRepository.save(nueva);
                //Registramos el periodo como ocupada
                EstadoHabitacionPeriodo periodo = new EstadoHabitacionPeriodo();
                periodo.setNumeroHabitacion(hab.getNumeroHabitacion());
                periodo.setEstado(EstadoHabitacion.ocupada);
                periodo.setFechaDesde(request.getFechaDesde());
                periodo.setFechaHasta(request.getFechaHasta());
                estadoPeriodoRepository.save(periodo);
                //Creamos la estadia basada en la reserva creada
                estadiaService.crearDesdeReserva(nueva, request.getFechaDesde(), responsable);

            }
        }

        // ---- VALIDAR PERTENECE A LA RESERVA ----
        if (reserva != null) {
            boolean habitacionPertenece = reserva.getHabitaciones().stream()
                    .anyMatch(h -> h.getNumeroHabitacion().equals(request.getNumeroHabitacion()));
            if (!habitacionPertenece) {
                throw new IllegalArgumentException("La habitación no pertenece a esta reserva.");
            }
        }

        // ---- VALIDAR CAPACIDAD ----
        Habitacion hab = habitacionRepository.findById(request.getNumeroHabitacion())
                .orElseThrow();

        int cantidadOcupantes = 1;
        if (huespedes != null && huespedes.getIdAcompanantes() != null) {
            cantidadOcupantes += huespedes.getIdAcompanantes().size();
        }

        if (hab.getCapacidad() != null && cantidadOcupantes > hab.getCapacidad()) {
            throw new IllegalStateException("No hay capacidad suficiente para esa habitación.");
        }

        // ---- REGISTRAR OCUPACIÓN ----
        estadoPeriodoService.ocupar(request.getNumeroHabitacion(), request.getFechaDesde(), request.getFechaHasta());

        // ---- ASIGNAR RESPONSABLE Y ACOMPAÑANTES ----
        PersonaFisica responsable = null;
        if (huespedes != null && huespedes.getIdHuesped() != null) {
            responsable = personaFisicaRepository.findById(huespedes.getIdHuesped())
                    .orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado"));

            String key = ((idReserva != null) ? idReserva.toString() : "00000")
                    + "_" + request.getNumeroHabitacion();

            List<PersonaFisica> ocupantes = new ArrayList<>();
            ocupantes.add(responsable);

            // Acompañantes
            List<PersonaFisica> listaAcompanantes = new ArrayList<>();
            if (huespedes.getIdAcompanantes() != null) {
                for (String idAc : huespedes.getIdAcompanantes()) {
                    PersonaFisica acomp = personaFisicaRepository.findById(idAc)
                            .orElseThrow(() -> new IllegalArgumentException("Acompañante no encontrado"));
                    listaAcompanantes.add(acomp);
                }
                ocupantes.addAll(listaAcompanantes);
            }

            //reserva.setAcompanantes(listaAcompanantes);
            reservaService.agregarAcompanantesAreserva(reserva.getIdReserva(), listaAcompanantes);
            reservaRepository.save(reserva);
            if (reserva != null) {
                reserva.setAcompanantes(listaAcompanantes);
                reservaRepository.save(reserva);
            }

            ocupantesAsignados.put(key, ocupantes);
        }

        // ---- CREAR ESTADÍA ----
        Estadia estadia = estadiaService.crearDesdeReserva(reserva, request.getFechaDesde(), responsable);

        // ---- ACTUALIZAR ESTADO DE RESERVA ----
        if (reserva != null && reserva.getEstado() == EstadoReserva.confirmada) {
            reserva.setEstado(EstadoReserva.cumplida);
            reservaRepository.save(reserva);
        }
    }*/

    //METEDO NUEVO, TENIENDO EN CUENTA LOS FLUJOS ALTERNATIVOS
    @Transactional
    public void ocuparHabitacion(Integer idReserva, OcupacionRequestDTO request, OcupacionHuespedDTO huespedes, boolean forzar) {
        validarFecha(request.getFechaDesde(), request.getFechaHasta());
        Reserva reservaExistente = buscarReservaParaOcupar(request.getNumeroHabitacion(), request.getFechaDesde(), request.getFechaDesde()) != null
                ? reservaRepository.findById(buscarReservaParaOcupar(request.getNumeroHabitacion(),
                        request.getFechaDesde(), request.getFechaHasta(), false))
                        .orElse(null)
                : null;

        //Si no hay reserva y no se forza, lanzar excepcion
        if (reservaExistente != null && !forzar) {
            throw new IllegalStateException("La habitación está ocupada o reservada por otra reserva en ese período.");
        }
        //buscamos la reserva asociada al idReserva
        Reserva reserva = null;
        if (idReserva != null) {
            reserva = reservaRepository.findById(idReserva).orElse(null);
        }
        //si no hay reserva creamos una  nueva
        if (reserva == null) {
            PersonaFisica responsable = personaFisicaRepository.findById(huespedes.getIdHuesped())
                    .orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado"));

            List<PersonaFisica> acompanantes = new ArrayList<>();
            if (huespedes.getIdAcompanantes() != null && !huespedes.getIdAcompanantes().isEmpty()) {
                List<PersonaFisica> listaAcomp = personaFisicaRepository.findAllById(huespedes.getIdAcompanantes());
                if (listaAcomp.size() != huespedes.getIdAcompanantes().size()) {
                    throw new IllegalArgumentException("Uno o más acompañantes no encontrados");
                }
                acompanantes.addAll(listaAcomp);
            }
            Habitacion hab = habitacionRepository.findById(request.getNumeroHabitacion()).orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
            int totalOcupantes = 1 + acompanantes.size();
            if (hab.getCapacidad() != null && totalOcupantes > hab.getCapacidad()) {
                throw new IllegalStateException("No hay capacidad suficiente para esa habitación.");
            }
            //CREAMOS UNA NUEVA RESERVA
            reserva = new Reserva();
            reserva.setNombre(responsable.getNombre());
            reserva.setApellido(responsable.getApellido());
            reserva.setTelefono(responsable.getTelefono());
            reserva.setHabitaciones(List.of(hab));
            reserva.setAcompanantes(acompanantes);
            reserva.setFechaDesde(request.getFechaDesde());
            reserva.setFechaHasta(request.getFechaHasta());
            reserva.setEstado(EstadoReserva.generada);
            reserva = reservaRepository.save(reserva);
        } else {
            // 4. Validar que la habitación pertenezca a la reserva
            boolean habitacionPertenece = reserva.getHabitaciones().stream()
                    .anyMatch(h -> h.getNumeroHabitacion().equals(request.getNumeroHabitacion()));
            if (!habitacionPertenece) {
                throw new IllegalArgumentException("La habitación no pertenece a esta reserva.");
            }

            // Validar capacidad de la habitación
            Habitacion hab = habitacionRepository.findById(request.getNumeroHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
            int cantidadOcupantes = 1 + (huespedes.getIdAcompanantes() != null ? huespedes.getIdAcompanantes().size() : 0);
            if (hab.getCapacidad() != null && cantidadOcupantes > hab.getCapacidad()) {
                throw new IllegalStateException("No hay capacidad suficiente para esa habitación.");
            }
        }
//Registrar ocupacion en el periodo
        try {
            estadoPeriodoService.ocupar(request.getNumeroHabitacion(), request.getFechaDesde(), request.getFechaHasta());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo registrar la ocupación. Intente nuevamente.", e);
        }
        // Asignar responsable y acompañantes
        PersonaFisica responsable = personaFisicaRepository.findById(huespedes.getIdHuesped())
                .orElseThrow(() -> new IllegalArgumentException("Responsable no encontrado"));

        List<PersonaFisica> ocupantes = new ArrayList<>();
        ocupantes.add(responsable);

        if (huespedes.getIdAcompanantes() != null && !huespedes.getIdAcompanantes().isEmpty()) {
            List<PersonaFisica> listaAcomp = personaFisicaRepository.findAllById(huespedes.getIdAcompanantes());
            ocupantes.addAll(listaAcomp);
            reserva.setAcompanantes(listaAcomp);
            reservaRepository.save(reserva);
            // reservaService.agregarAcompanantesAreserva(reserva.getIdReserva(), listaAcomp);
        }

        String key = ((idReserva != null) ? idReserva.toString() : "0") + "_" + request.getNumeroHabitacion();
        ocupantesAsignados.put(key, ocupantes);

        // Crear estadía basada en la reserva
        Estadia estadia = estadiaService.crearDesdeReserva(reserva, request.getFechaDesde(), responsable);

        //  Actualizar estado de la reserva si estaba confirmada
        if (reserva.getEstado() == EstadoReserva.confirmada) {
            reserva.setEstado(EstadoReserva.cumplida);
            reservaRepository.save(reserva);
        }

    }

    // ----------------- OBTENER OCUPANTES -----------------
    public List<PersonaFisica> obtenerOcupantesAsignados(Integer idReserva, Integer numeroHabitacion) {
        String key = idReserva + "_" + numeroHabitacion;
        return ocupantesAsignados.getOrDefault(key, List.of());
    }

    // ----------------- BUSCAR RESERVA PARA OCUPAR -----------------
    @Transactional(readOnly = true)
    public Reserva buscarReservaParaOcupar(Integer numeroHab, Date fechaDesde, Date fechaHasta) {
        List<Reserva> reservas = reservaRepository.findByHabitacion(numeroHab);
        if (reservas == null || reservas.isEmpty()) {
            return null;
        }

        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.cancelada) {
                continue;
            }

            boolean seSolapa = !(fechaHasta.before(r.getFechaDesde()) || fechaDesde.after(r.getFechaHasta()));
            if (seSolapa) {
                return r;
            }
        }
        return null;
    }

    // ----------------- BUSCAR RESERVA PARA OCUPAR (RETORNA ID) -----------------
    @Transactional(readOnly = true)
    public Integer buscarReservaParaOcupar(Integer numeroHab, Date fechaDesde, Date fechaHasta, boolean permitirSinReserva) {
        Reserva r = buscarReservaParaOcupar(numeroHab, fechaDesde, fechaHasta); // método interno que ya tenemos
        if (r != null) {
            return r.getIdReserva();
        }
        return permitirSinReserva ? 0 : null;
    }

}

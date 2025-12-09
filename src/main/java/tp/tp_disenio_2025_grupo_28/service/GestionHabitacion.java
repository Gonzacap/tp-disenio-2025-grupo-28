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

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.PersonaFisicaRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
@Transactional(readOnly = true)
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

    //CASO DE USO 05
    //GRILLA DE ESTADOS CU05
    public void validarFecha(Date desde, Date hasta) {

        //Ambas fechas nulas
        if (desde == null && hasta == null) {
            throw new IllegalArgumentException("Debe ingresar la fecha Desde y la fecha Hasta.");
        }

        //Desde nula
        if (desde == null) {
            throw new IllegalArgumentException("Debe ingresar la fecha Desde.");
        }

        //Hasta nula
        if (hasta == null) {
            throw new IllegalArgumentException("Debe ingresar la fecha Hasta.");
        }

        //Normalizar fechas
        Date fechaDesde = limpiarHora(desde);
        Date fechaHasta = limpiarHora(hasta);
        Date hoy = limpiarHora(new Date());

        // Desde < hoy  (HOY SÍ ES VÁLIDO)
        if (fechaDesde.before(hoy)) {
            throw new IllegalArgumentException("La fecha Desde no puede ser anterior a la fecha actual.");
        }

        //Hasta < hoy
        if (fechaHasta.before(hoy)) {
            throw new IllegalArgumentException("La fecha Hasta no puede ser anterior a la fecha actual.");
        }

        //Hasta < Desde
        if (fechaHasta.before(fechaDesde)) {
            throw new IllegalArgumentException("La fecha Hasta debe ser posterior o igual a la fecha Desde.");
        }
    }

    private Date limpiarHora(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
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

    //GRILLA OPTIMIZADA
    @Transactional(readOnly = true)
    public List<Map<String, Object>> grilla(List<Map<String, Object>> habitacionesPorTipo,
            List<Habitacion> habitaciones, List<Date> dias, Date desde, Date hasta) {

        // Traer TODOS los estados del rango
        List<EstadoHabitacionPeriodo> periodos = estadoPeriodoRepository.findEnRango(desde, hasta);

        //  Indexar por habitación
        Map<Integer, List<EstadoHabitacionPeriodo>> porHabitacion = new HashMap<>();

        for (EstadoHabitacionPeriodo p : periodos) {
            Integer nro = p.getNumeroHabitacion();

            porHabitacion.computeIfAbsent(nro, k -> new ArrayList<>()).add(p);
        }

        List<Map<String, Object>> salida = new ArrayList<>();

        // 3️⃣ Armado de grilla
        for (Date dia : dias) {

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", dia);

            List<Map<String, Object>> estadosPorTipo = new ArrayList<>();

            for (Map<String, Object> tipoHab : habitacionesPorTipo) {

                String nombreTipo = (String) tipoHab.get("nombre");
                List<Integer> numeros = (List<Integer>) tipoHab.get("habitaciones");

                List<String> estados = new ArrayList<>();

                for (Integer nro : numeros) {

                    String estado = "DISPONIBLE";

                    for (EstadoHabitacionPeriodo p
                            : porHabitacion.getOrDefault(nro, List.of())) {

                        if (!dia.before(p.getFechaDesde())
                                && !dia.after(p.getFechaHasta())) {

                            estado = p.getEstado().name();
                            break;
                        }
                    }
                    estados.add(estado);
                }

                estadosPorTipo.add(Map.of(
                        "tipo", nombreTipo,
                        "habitaciones", numeros,
                        "estados", estados
                ));
            }

            fila.put("estadosPorTipo", estadosPorTipo);
            salida.add(fila);
        }

        return salida;
    }

    /* @Transactional(readOnly = true)
    public List<Map<String, Object>> grilla(
            List<Map<String, Object>> habitacionesPorTipo,
            List<Habitacion> habitaciones,
            List<Date> dias) {

        List<Map<String, Object>> salida = new ArrayList<>();

        for (Date dia : dias) {

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", dia);

            List<Map<String, Object>> estadosPorTipo = new ArrayList<>();

            for (Map<String, Object> tipoHab : habitacionesPorTipo) {

                String nombreTipo = (String) tipoHab.get("nombre");
                List<Integer> numeros = (List<Integer>) tipoHab.get("habitaciones");

                List<String> estados = new ArrayList<>();

                for (Integer numeroHab : numeros) {

                    //buscamos estado por PERIODO
                    List<EstadoHabitacionPeriodo> periodos
                            = estadoPeriodoRepository.findPeriodosSuperpuestos(numeroHab, dia, dia);

                    String estado = "DISPONIBLE";

                    if (!periodos.isEmpty()) {
                        // si hay varios, cualquiera distinto de DISPONIBLE bloquea
                        EstadoHabitacionPeriodo p = periodos.get(0);
                        estado = p.getEstado().name().toUpperCase();
                    }

                    estados.add(estado);
                }

                Map<String, Object> bloqueTipo = new HashMap<>();
                bloqueTipo.put("tipo", nombreTipo);
                bloqueTipo.put("habitaciones", numeros);
                bloqueTipo.put("estados", estados);

                estadosPorTipo.add(bloqueTipo);
            }

            fila.put("estadosPorTipo", estadosPorTipo);
            salida.add(fila);
        }

        return salida;
    }*/
 /* @Transactional(readOnly = true)
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
    }*/
    @Transactional(readOnly = true)
    public Habitacion buscarPorNumero(Integer numero) {
        return habitacionRepository.findById(numero).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(Integer nroHabitacion, Date desde, Date hasta) {
        return estadoPeriodoService.estaDisponible(nroHabitacion, desde, hasta);
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
    /* 
    // ----------------- CU15: OCUPAR HABITACIÓN -----------------
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
        // ocupantesAsignados.put(key, ocupantes);

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
     */
}

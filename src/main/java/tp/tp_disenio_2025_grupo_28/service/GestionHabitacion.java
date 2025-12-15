package tp.tp_disenio_2025_grupo_28.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
@Transactional(readOnly = true)
public class GestionHabitacion {

    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private EstadoHabitacionPeriodoRepository estadoPeriodoRepository;
    @Autowired
    private EstadoHabitacionPeriodoService estadoPeriodoService;
    @Autowired
    private ReservaRepository reservaRepository;

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

        // Armado de grilla
        for (Date dia : dias) {

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", dia);

            List<Map<String, Object>> estadosPorTipo = new ArrayList<>();

            for (Map<String, Object> tipoHab : habitacionesPorTipo) {

                String nombreTipo = (String) tipoHab.get("nombre");
                List<Integer> numeros = (List<Integer>) tipoHab.get("habitaciones");

                List<String> estados = new ArrayList<>();

                for (Integer nro : numeros) {

                    List<EstadoHabitacionPeriodo> periodosHab
                            = porHabitacion.getOrDefault(nro, List.of());

                    EstadoHabitacion estado = estadoPeriodoService.estadoEnDia(nro, dia, periodosHab);

                    estados.add(estado.name());
                }

                estadosPorTipo.add(Map.of("tipo", nombreTipo, "habitaciones", numeros, "estados", estados));
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
    //CU15

    @Transactional(readOnly = true)
    public boolean habitacionTieneReserva(Integer numeroHabitacion, Date desde, Date hasta) {
        return reservaRepository.existeSolapamiento(numeroHabitacion, desde, hasta);
    }

    //cu15
    public EnumSet<EstadoHabitacion> estadosEnRango(
            Integer numeroHabitacion, Date desde, Date hasta) {

        List<EstadoHabitacionPeriodo> periodos
                = estadoPeriodoRepository.findPeriodosSuperpuestos(
                        numeroHabitacion, desde, hasta);

        EnumSet<EstadoHabitacion> estados = EnumSet.noneOf(EstadoHabitacion.class);

        for (EstadoHabitacionPeriodo p : periodos) {
            estados.add(p.getEstado());
        }

        return estados;
    }

    @Transactional
    public void ocuparHabitacion(Integer numeroHabitacion, Date fechaDesde, Date fechaHasta) {
        EstadoHabitacionPeriodo periodo = new EstadoHabitacionPeriodo(EstadoHabitacion.ocupada, fechaDesde,
                fechaHasta, numeroHabitacion);

        estadoPeriodoRepository.save(periodo);
    }

}

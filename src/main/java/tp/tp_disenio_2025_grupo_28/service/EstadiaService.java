package tp.tp_disenio_2025_grupo_28.service;

import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.ResponsablePago;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoEstadia;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;
import tp.tp_disenio_2025_grupo_28.repository.EstadiaRepository;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.repository.PersonaFisicaRepository;
import tp.tp_disenio_2025_grupo_28.repository.ReservaRepository;

@Service
@Transactional
public class EstadiaService {

    @Autowired
    private EstadiaRepository estadiaRepository;
    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private PersonaFisicaRepository personaFisicaRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private HuespedRepository huespedRepository;

    public EstadiaService() {
    }

    //Crear una estadía desde una reserva (cuando el huésped llega).    
    public Estadia crearDesdeReserva(Reserva reserva, Date fechaCheckIn, PersonaFisica responsable) {
        Estadia e = new Estadia();
        e.setReserva(reserva);
        e.setEstado(EstadoEstadia.enCurso);
        e.setFechaCheckIn(fechaCheckIn);
        e.setHoraCheckIn(new Time(System.currentTimeMillis()));
        e.setFechaCheckOut(reserva.getFechaHasta()); // por defecto fin de reserva
        e.setResponsablePago(responsable);
        return estadiaRepository.save(e);
    }

    //Registrar check-in real (cuando el huésped se presenta en recepción).
    public Estadia realizarCheckIn(Integer idEstadia, Date fecha, Time hora) {
        Estadia e = estadiaRepository.findById(idEstadia).orElseThrow(() -> new IllegalArgumentException("No existe la estadía"));

        e.setFechaCheckIn(fecha);
        e.setHoraCheckIn(hora);
        e.setEstado(EstadoEstadia.enCurso);

        return estadiaRepository.save(e);
    }

    //Registrar check-out (huésped se retira).
    public Estadia realizarCheckOut(Integer idEstadia, Date fecha, Time hora) {
        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new IllegalArgumentException("No existe la estadía"));
        e.setFechaCheckOut(fecha);
        e.setHoraCheckOut(hora);
        e.setEstado(EstadoEstadia.finalizada);
        return estadiaRepository.save(e);
    }

    //Extender estadía (cambiar estado y fecha de salida).
    public Estadia extenderEstadia(Integer idEstadia, Date nuevaFechaSalida) {
        Estadia e = estadiaRepository.findById(idEstadia).orElseThrow(() -> new IllegalArgumentException("No existe la estadía"));
        e.setFechaCheckOut(nuevaFechaSalida);
        e.setEstado(EstadoEstadia.extendida);
        return estadiaRepository.save(e);
    }

    //Cancelar estadía (si la reserva fue cancelada).
    public Estadia cancelar(Integer idEstadia) {
        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new IllegalArgumentException("No existe la estadía"));

        e.setEstado(EstadoEstadia.cancelada);

        return estadiaRepository.save(e);
    }

    public Estadia obtenerPorId(Integer idEstadia) {
        return estadiaRepository.findById(idEstadia).orElse(null);
    }

    public Estadia obtenerPorIdReserva(Integer idReserva) {
        return estadiaRepository.findByReserva_IdReserva(idReserva);
    }

    /**
     *
     */
    public Reserva procesarReserva(
            Huesped huesped,
            Integer numeroHabitacion,
            Date desde,
            Date hasta,
            Integer idReserva
    ) {
        Habitacion hab = habitacionRepository.findById(numeroHabitacion)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // logica de ocupar habitacion
        Reserva reservaAUsar;

        if (idReserva == null) {
            System.out.println(">>> Ocupación directa (sin reserva). Se crea una nueva reserva.\n");
            reservaAUsar = crearReservaTemporal(huesped, hab, desde, hasta);
        } else {
            System.out.println(">>> Ocupacion con reserva. Se recupera la reserva.\n");
            reservaAUsar = reservaRepository.findById(idReserva)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        }

        System.out.println("Reserva: " + reservaAUsar + "\n");

        return reservaAUsar;
    }

    /**
     *
     */
    public Estadia iniciarCarga(
            Huesped huesped,
            Integer numeroHabitacion,
            Date desde,
            Date hasta,
            Reserva reserva
    ) {

        System.out.println(">>> Se crea la nueva estadia.\n");

        Estadia estadia = new Estadia();
        estadia.setFechaCheckIn(desde);
        estadia.setFechaCheckOut(hasta);
        estadia.setEstado(EstadoEstadia.enCurso);
        estadia.setReserva(reserva);

        // ResponsablePago se setea más adelante
        return estadiaRepository.save(estadia);
    }

    /**
     *
     */
    private Reserva crearReservaTemporal(Huesped huesped, Habitacion hab, Date desde, Date hasta) {

        Reserva r = new Reserva();

        // :TODO falta asociar la reserva con el huesped
        r.setNombre(huesped.getNombre());
        r.setApellido(huesped.getApellido());
        r.setTelefono(huesped.getTelefono());
        r.setFechaDesde(desde);
        r.setFechaHasta(hasta);
        r.setEstado(EstadoReserva.confirmada);
        r.getHabitaciones().add(hab);

        return reservaRepository.save(r);
    }

    /**
     *
     */
    public Estadia obtenerEstadia(Integer estadiaId) {
        return estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new RuntimeException("Estadia no encontrada"));
    }

    /**
     *
     */
    public Estadia asignarHuesped(Integer idEstadia, ResponsablePago responsable) {

        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new RuntimeException("Estadia no encontrada"));

        e.setResponsablePago(responsable);

        return estadiaRepository.save(e);
    }

    /**
     *
     */
    @Transactional
    public Reserva agregarAcompanantes(Integer idReserva, List<PersonaFisica> nuevos) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (nuevos != null && !nuevos.isEmpty()) {
            reserva.getAcompanantes().addAll(new HashSet<>(nuevos));
        }

        return reserva;
    }

    /**
     *
     */
    public Estadia confirmarEstadia(Integer idEstadia) {

        Estadia e = estadiaRepository.findById(idEstadia)
                .orElseThrow(() -> new RuntimeException("Estadia no encontrada"));

        e.setEstado(EstadoEstadia.enCurso);

        // Marcar habitaciones como ocupadas
        for (Habitacion h : e.getReserva().getHabitaciones()) {
            h.setEstado(EstadoHabitacion.ocupada);
        }

        return estadiaRepository.save(e);
    }

    public boolean validarCapacidadHabitacion(Integer numeroHabitacion, Integer nroAcompanantes) {
        Habitacion hab = habitacionRepository.findById(numeroHabitacion)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        System.out.println("\n validarCapacidadHabitacion capacidad " + hab.getCapacidad() + " \n");

        return nroAcompanantes <= hab.getCapacidad(); 
    }

}

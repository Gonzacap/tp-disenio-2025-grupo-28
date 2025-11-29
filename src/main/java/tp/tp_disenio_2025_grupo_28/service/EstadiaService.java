package tp.tp_disenio_2025_grupo_28.service;

import java.sql.Time;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoEstadia;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoEstadia;
import tp.tp_disenio_2025_grupo_28.repository.EstadiaRepository;

@Service
@Transactional
public class EstadiaService {

    @Autowired
    private EstadiaRepository estadiaRepository;
    @Autowired
    private GestionHabitacion gestionHabitacion;

    //Crear una estadía desde una reserva (cuando el huésped llega).    
    public Estadia crearDesdeReserva(Reserva reserva, TipoEstadia tipo) {

        Estadia e = new Estadia();
        e.setReserva(reserva);
        e.setEstado(EstadoEstadia.reservada);
        e.setFechaCheckIn(null);
        e.setHoraCheckIn(null);
        e.setTipo(tipo);

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

    public boolean estaDisponible(Habitacion habitacion, Date fechaDesde, Date fechaHasta) {
        return gestionHabitacion.estaDisponible(habitacion.getNumeroHabitacion(), fechaDesde, fechaHasta);
    }

}

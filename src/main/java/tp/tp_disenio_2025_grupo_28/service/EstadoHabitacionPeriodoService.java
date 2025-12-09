package tp.tp_disenio_2025_grupo_28.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;

@Service
@Transactional
public class EstadoHabitacionPeriodoService {

    private final EstadoHabitacionPeriodoRepository repo;

    @Autowired
    public EstadoHabitacionPeriodoService(EstadoHabitacionPeriodoRepository repo) {
        this.repo = repo;
    }

    //CU05 + CU04, UNA HABITACION ESTA DISPONIBLE SI Y SOLO SI
    //NO SOLAPA CON NINGUN PERIODO NO DISPONIBLE
    public boolean estaDisponible(Integer nroHab, Date desde, Date hasta) {

        List<EstadoHabitacionPeriodo> periodos = repo.findPeriodosSuperpuestos(nroHab, desde, hasta);

        for (EstadoHabitacionPeriodo p : periodos) {
            // CUALQUIER estado distinto de DISPONIBLE bloquea
            if (p.getEstado() != EstadoHabitacion.disponible) {
                return false;
            }
        }
        return true;
    }

    //REGISTRAR RESERVA --> CU04
    public void registrarReserva(Integer numeroHabitacion, Date desde, Date hasta) {
        EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo();
        p.setNumeroHabitacion(numeroHabitacion);
        p.setEstado(EstadoHabitacion.reservada);
        p.setFechaDesde(desde);
        p.setFechaHasta(hasta);
        repo.save(p);
    }

//registrar como ocupada
    public EstadoHabitacionPeriodo ocupar(Integer numeroHabitacion, Date desde, Date hasta) {
        EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo();
        p.setNumeroHabitacion(numeroHabitacion);
        p.setEstado(EstadoHabitacion.ocupada);
        p.setFechaDesde(desde);
        p.setFechaHasta(hasta);
        return repo.save(p);
    }

    public EstadoHabitacion estadoEnDia(Integer nroHab, Date dia, List<EstadoHabitacionPeriodo> periodosHab) {

        for (EstadoHabitacionPeriodo p : periodosHab) {
            if (!dia.before(p.getFechaDesde()) && !dia.after(p.getFechaHasta())) {

                // cualquier estado distinto de DISPONIBLE manda
                if (p.getEstado() != EstadoHabitacion.disponible) {
                    return p.getEstado();
                }
            }
        }
        return EstadoHabitacion.disponible;
    }
}

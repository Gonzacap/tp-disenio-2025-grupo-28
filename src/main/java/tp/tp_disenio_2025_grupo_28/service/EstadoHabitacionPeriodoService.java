package tp.tp_disenio_2025_grupo_28.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;
import tp.tp_disenio_2025_grupo_28.repository.EstadoHabitacionPeriodoRepository;

@Service
public class EstadoHabitacionPeriodoService {

    @Autowired
    private final EstadoHabitacionPeriodoRepository repo;

    public EstadoHabitacionPeriodoService(EstadoHabitacionPeriodoRepository repo) {
        this.repo = repo;
    }

    public boolean existeSuperposicion(Integer numHab, Date desde, Date hasta) {
        List<EstadoHabitacionPeriodo> res
                = repo.findPeriodosSuperpuestos(numHab, desde, hasta);
        return !res.isEmpty();
    }

    public void registrarPeriodo(Habitacion hab, EstadoHabitacion estado, Date desde, Date hasta) {
        EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo(estado, desde, hasta, hab.getNumeroHabitacion());
        repo.save(p);
    }

    public boolean estaDisponible(Integer numeroHabitacion, Date desde, Date hasta) {

        List<EstadoHabitacionPeriodo> periodos = repo.findByNumeroHabitacion(numeroHabitacion);

        for (EstadoHabitacionPeriodo p : periodos) {

            boolean seSolapa
                    = !p.getFechaHasta().before(desde)
                    && !p.getFechaDesde().after(hasta);

            if (seSolapa && p.getEstado() != EstadoHabitacion.disponible) {
                return false;
            }
        }

        return true;
    }

}

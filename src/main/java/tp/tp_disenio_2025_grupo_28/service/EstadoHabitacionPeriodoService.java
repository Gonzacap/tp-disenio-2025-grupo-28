package tp.tp_disenio_2025_grupo_28.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
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

    public void registrarPeriodo(Habitacion hab, EstadoHabitacion estado, Date desde, Date hasta) {
        EstadoHabitacionPeriodo p = new EstadoHabitacionPeriodo(estado, desde, hasta, hab.getNumeroHabitacion());
        repo.save(p);
    }

    public boolean estaDisponible(Integer numeroHabitacion, Date desde, Date hasta) {

        List<EstadoHabitacionPeriodo> periodos = repo.findByNumeroHabitacion(numeroHabitacion);

        for (EstadoHabitacionPeriodo p : periodos) {

            boolean seSolapa = !p.getFechaHasta().before(desde) && !p.getFechaDesde().after(hasta);

            if (seSolapa && p.getEstado() != EstadoHabitacion.disponible) {
                return false; // Cualquier estado NO disponible → ocupa la fecha
            }
        }
        return true;
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
}

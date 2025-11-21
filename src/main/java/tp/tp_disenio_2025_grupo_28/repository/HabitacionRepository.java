package tp.tp_disenio_2025_grupo_28.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tp.tp_disenio_2025_grupo_28.model.Habitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    @Query("""
       SELECT h FROM Habitacion h
       WHERE h.numeroHabitacion NOT IN (
             SELECT e.numeroHabitacion 
             FROM EstadoHabitacionPeriodo e
             WHERE (e.fechaDesde <= :hasta AND e.fechaHasta >= :desde)
       )
    """)
    List<Habitacion> buscarHabitacionesDisponibles(LocalDate desde, LocalDate hasta);
}

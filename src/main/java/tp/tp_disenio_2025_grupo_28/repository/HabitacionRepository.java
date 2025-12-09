package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {

    @Query("""
       SELECT h FROM Habitacion h
       WHERE h.numeroHabitacion NOT IN (
             SELECT e.numeroHabitacion 
             FROM EstadoHabitacionPeriodo e
             WHERE (e.fechaDesde <= :hasta AND e.fechaHasta >= :desde)
       )
    """)
    List<Habitacion> buscarHabitacionesDisponibles(Date desde, Date hasta);

    List<Habitacion> findAllByOrderByTipoAscNumeroHabitacionAsc();

    List<EstadoHabitacionPeriodo> findByNumeroHabitacion(Integer numeroHabitacion);

    public List<Habitacion> findAllByNumeroHabitacionIn(List<Integer> numerosHabitacion);

}

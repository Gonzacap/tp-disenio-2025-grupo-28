package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.enums.*;

@Repository
public interface EstadoHabitacionPeriodoRepository extends JpaRepository<EstadoHabitacionPeriodo, Integer> {

    List<EstadoHabitacionPeriodo> findByNumeroHabitacion(Integer numeroHabitacion);

    List<EstadoHabitacionPeriodo> findByNumeroHabitacionAndEstado(Integer numeroHabitacion, EstadoHabitacion estado);

    @Query("SELECT p FROM EstadoHabitacionPeriodo p "
            + "WHERE p.habitacion.numeroHabitacion = :numHab "
            + "AND (p.fechaDesde <= :fechaHasta AND p.fechaHasta >= :fechaDesde)")
    List<EstadoHabitacionPeriodo> findPeriodosSuperpuestos(Integer numHab, Date fechaDesde, Date fechaHasta);
}

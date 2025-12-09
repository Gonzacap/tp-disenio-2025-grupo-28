package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.EstadoHabitacionPeriodo;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoHabitacion;

@Repository
public interface EstadoHabitacionPeriodoRepository extends JpaRepository<EstadoHabitacionPeriodo, Integer> {

    List<EstadoHabitacionPeriodo> findByNumeroHabitacion(Integer numeroHabitacion);

    List<EstadoHabitacionPeriodo> findByNumeroHabitacionAndEstado(Integer numeroHabitacion, EstadoHabitacion estado);

    @Query("""
    SELECT p
    FROM EstadoHabitacionPeriodo p
    WHERE p.numeroHabitacion = :numHab
    AND p.fechaDesde <= :fechaHasta
    AND p.fechaHasta >= :fechaDesde
    """)
    List<EstadoHabitacionPeriodo> findPeriodosSuperpuestos(Integer numHab, Date fechaDesde, Date fechaHasta);

    @Query("""
    SELECT e
    FROM EstadoHabitacionPeriodo e
    WHERE e.fechaDesde <= :hasta
    AND e.fechaHasta >= :desde """)
    List<EstadoHabitacionPeriodo> findEnRango(@Param("desde") Date desde, @Param("hasta") Date hasta);

}

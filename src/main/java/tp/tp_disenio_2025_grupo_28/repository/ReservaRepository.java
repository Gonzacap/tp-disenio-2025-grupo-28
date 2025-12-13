package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(Date fechaHasta, Date fechaDesde);

    List<Reserva> findByEstadoNot(EstadoReserva estado);
    // Busca reservas asociadas a una habitación específica

    @Query("SELECT r FROM Reserva r JOIN r.habitaciones h "
            + "WHERE h.numeroHabitacion = :nro")
    List<Reserva> findByHabitacion(Integer nro);

    @Query("SELECT p FROM PersonaFisica p WHERE p.cuit = :id")
    PersonaFisica buscarPersonaFisicaPorId(@Param("id") Integer id);

    @Query("SELECT p FROM PersonaFisica p")
    List<PersonaFisica> buscarTodasLasPersonasFisicas();

    @Query("""
   SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
   FROM Reserva r JOIN r.habitaciones h
   WHERE h.numeroHabitacion = :numeroHabitacion
     AND r.fechaDesde <= :hasta
     AND r.fechaHasta >= :desde
""")
    boolean existeSolapamiento(@Param("numeroHabitacion") Integer numeroHabitacion,
            @Param("desde") Date desde,
            @Param("hasta") Date hasta);

    @Query("""
    SELECT r
    FROM Reserva r
    JOIN r.habitaciones h
    WHERE h.numeroHabitacion = :numeroHabitacion
      AND r.fechaDesde <= :hasta
      AND r.fechaHasta >= :desde""")
    List<Reserva> buscarReservasEnRango(
            @Param("numeroHabitacion") Integer numeroHabitacion,
            @Param("desde") Date desde,
            @Param("hasta") Date hasta
    );

    @Query("""
        SELECT r FROM Reserva r 
        JOIN r.habitaciones h
        WHERE h.numeroHabitacion = :numero
        AND r.fechaDesde <= :hasta
        AND r.fechaHasta >= :desde
        AND r.estado <> 'cancelada'
    """)
    List<Reserva> buscarSolapamientos(
            @Param("numero") Integer numeroHabitacion,
            @Param("desde") Date desde,
            @Param("hasta") Date hasta
    );

}

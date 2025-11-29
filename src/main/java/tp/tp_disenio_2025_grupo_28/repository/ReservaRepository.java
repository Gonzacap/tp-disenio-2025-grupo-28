package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tp.tp_disenio_2025_grupo_28.model.Reserva;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(Date fechaHasta, Date fechaDesde);

    List<Reserva> findByEstadoNot(EstadoReserva estado);
    // Busca reservas asociadas a una habitación específica

    @Query("SELECT r FROM Reserva r JOIN r.habitaciones h "
            + "WHERE h.numeroHabitacion = :nro")
    List<Reserva> findByHabitacion(Integer nro);

    public List<Reserva> findReservasSuperpuestas(Integer numero, Date desde, Date hasta);
}

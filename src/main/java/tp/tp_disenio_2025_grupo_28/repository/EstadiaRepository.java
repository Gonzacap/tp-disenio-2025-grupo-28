package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Estadia;
import tp.tp_disenio_2025_grupo_28.model.enums.EstadoEstadia;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Integer> {

    List<Estadia> findByEstado(EstadoEstadia estado);

    Estadia findByReserva_IdReserva(Integer idReserva);

    Optional<Estadia> findFirstByReserva_IdReserva(Integer idReserva);

}

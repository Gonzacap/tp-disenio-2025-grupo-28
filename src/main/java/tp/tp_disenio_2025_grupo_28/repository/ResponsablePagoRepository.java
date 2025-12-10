package tp.tp_disenio_2025_grupo_28.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.ResponsablePago;

@Repository
public interface ResponsablePagoRepository extends JpaRepository<ResponsablePago, Integer> {
}

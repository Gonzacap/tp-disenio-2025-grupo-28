package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer> {

    Optional<Pais> findByNombre(String nombre);
}

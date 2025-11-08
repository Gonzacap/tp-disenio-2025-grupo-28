package tp.tp_disenio_2025_grupo_28.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.tp_disenio_2025_grupo_28.model.Provincia;

import java.util.*;

public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {
    Optional<Provincia> findByNombre(String nombre);
}

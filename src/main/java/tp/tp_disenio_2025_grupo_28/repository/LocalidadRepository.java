package tp.tp_disenio_2025_grupo_28.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.tp_disenio_2025_grupo_28.model.Localidad;

import java.util.*;

public interface LocalidadRepository extends JpaRepository<Localidad, Integer> {
    Optional<Localidad> findByNombre(String nombre);
}

package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Localidad;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Integer> {

    Optional<Localidad> findByNombre(String nombre);
}

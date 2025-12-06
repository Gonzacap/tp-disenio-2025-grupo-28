package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.Localidad;

//import java.util.*;
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    public Optional<Direccion> findByDireccionAndNumeroAndPisoAndDeptoAndLocalidad(String direccion, Integer numero, String piso, String depto, Localidad localidad);

}

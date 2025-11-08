package tp.tp_disenio_2025_grupo_28.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

    Optional<Huesped> findByTipoDocumentoAndDocumento(TipoDocumento tipo, String documento);

}

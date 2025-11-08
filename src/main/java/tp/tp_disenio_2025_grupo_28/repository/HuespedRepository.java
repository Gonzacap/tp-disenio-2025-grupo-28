package tp.tp_disenio_2025_grupo_28.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

import java.util.*;

public interface HuespedRepository extends JpaRepository<Huesped, Integer> {
    Optional<Huesped> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipo, String numero);
}

package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, String> {

    Optional<Huesped> findByTipoDocumentoAndDocumento(TipoDocumento tipo, String documento);

    List<Huesped> findAllByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);

    List<Huesped> findByApellidoContainingIgnoreCase(String apellido);

    List<Huesped> findByNombreContainingIgnoreCase(String nombre);

    List<Huesped> findByDocumento(String documento);

    List<Huesped> findByEmailContainingIgnoreCase(String email);

    Huesped findFirstByDocumento(String documento);

    List<Huesped> findAllByTipoDocumento(TipoDocumento tipoDocumento);

}

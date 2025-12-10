package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

    Optional<Huesped> findByTipoDocumentoAndDocumento(TipoDocumento tipo, String documento);

    List<Huesped> findAllByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);

    List<Huesped> findByApellidoContainingIgnoreCase(String apellido);

    List<Huesped> findByNombreContainingIgnoreCase(String nombre);

    List<Huesped> findByDocumento(String documento);

    List<Huesped> findByEmailContainingIgnoreCase(String email);

    Huesped findFirstByDocumento(String documento);

    List<Huesped> findAllByTipoDocumento(TipoDocumento tipoDocumento);

    @Query("""
    SELECT h FROM Huesped h
    JOIN PersonaFisica pf ON pf.id = h.id
    WHERE (:nombre IS NULL OR :nombre = '' OR LOWER(pf.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
      AND (:apellido IS NULL OR :apellido = '' OR LOWER(pf.apellido) LIKE LOWER(CONCAT('%', :apellido, '%')))
      AND (:tipoDocumento IS NULL OR pf.tipoDocumento = :tipoDocumento)
      AND (:documento IS NULL OR :documento = '' OR pf.documento = :documento)
    """)
    List<Huesped> buscarHuespedes(
            @Param("apellido") String apellido,
            @Param("nombre") String nombre,
            @Param("tipoDocumento") TipoDocumento tipoDocumento,
            @Param("documento") String documento
    );

}

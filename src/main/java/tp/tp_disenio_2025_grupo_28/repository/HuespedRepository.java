package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

    Optional<Huesped> findByTipoDocumentoAndDocumento(TipoDocumento tipo, String documento);

    // NUEVO: búsqueda dinámica para CU15
    @Query("""
        SELECT pf 
        FROM PersonaFisica pf
        JOIN Huesped h ON h.personaFisica.id = pf.id
        WHERE (:nombre IS NULL OR LOWER(pf.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          AND (:apellido IS NULL OR LOWER(pf.apellido) LIKE LOWER(CONCAT('%', :apellido, '%')))
          AND (:tipoDocumento IS NULL OR pf.tipoDocumento = :tipoDocumento)
          AND (:numeroDocumento IS NULL OR pf.documento = :numeroDocumento)
    """)
    List<PersonaFisica> buscarHuespedes(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("tipoDocumento") TipoDocumento tipoDocumento,
            @Param("numeroDocumento") String numeroDocumento
    );
}

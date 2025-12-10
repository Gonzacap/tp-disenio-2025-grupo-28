package tp.tp_disenio_2025_grupo_28.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;

@Repository
public interface PersonaFisicaRepository extends JpaRepository<PersonaFisica, Integer> {

    // public Object findById(String idAc);
    
    List<PersonaFisica> findAllByTipoDocumentoAndDocumento(TipoDocumento tipoDocumento, String documento);

    List<PersonaFisica> findByApellidoContainingIgnoreCase(String apellido);

    List<PersonaFisica> findByNombreContainingIgnoreCase(String nombre);

    List<PersonaFisica> findByDocumento(String documento);

    PersonaFisica findFirstByDocumento(String documento);

    List<PersonaFisica> findAllByTipoDocumento(TipoDocumento tipoDocumento);
}

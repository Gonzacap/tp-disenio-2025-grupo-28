package tp.tp_disenio_2025_grupo_28.service;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DuplicateKeyException;

import org.springframework.stereotype.Service;

import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.model.*;
import java.util.*;


@Service
public class GestionHuesped {

    @Autowired
    private HuespedRepository huespedRepository;

    public Huesped registrarHuesped(Huesped nuevoHuesped) {
        
        List<String> errores = validarCampos(nuevoHuesped);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores: " + String.join(", ", errores));
        }

        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndDocumento(
                nuevoHuesped.getTipoDocumento(),
                nuevoHuesped.getDocumento()
        );

        if (existente.isPresent()) {
            throw new DuplicateKeyException("El huésped con ese documento ya existe");
        }

        return huespedRepository.save(nuevoHuesped);
    }

    private List<String> validarCampos(Huesped h) {
        List<String> errores = new ArrayList<>();

        if (h.getApellido() == null || h.getApellido().isBlank())
            errores.add("Apellido");
        if (h.getNombre() == null || h.getNombre().isBlank())
            errores.add("Nombres");
        if (h.getDocumento() == null || h.getTipoDocumento() == null)
            errores.add("Tipo y número de documento");
        if (h.getFechaNacimiento() == null)
            errores.add("Fecha de nacimiento");
        // if (h.getDireccion() == null)
        //     errores.add("Dirección");
        if (h.getTelefono() == null)
            errores.add("Teléfono");

        return errores;
    }
}

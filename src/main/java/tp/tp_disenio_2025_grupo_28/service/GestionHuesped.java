package tp.tp_disenio_2025_grupo_28.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tp.tp_disenio_2025_grupo_28.dto.HuespedDTO;
import tp.tp_disenio_2025_grupo_28.mapper.HuespedMapper;
import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.Localidad;
import tp.tp_disenio_2025_grupo_28.model.Pais;
import tp.tp_disenio_2025_grupo_28.model.Provincia;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.repository.DireccionRepository;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.repository.LocalidadRepository;
import tp.tp_disenio_2025_grupo_28.repository.PaisRepository;
import tp.tp_disenio_2025_grupo_28.repository.PersonaFisicaRepository;
import tp.tp_disenio_2025_grupo_28.repository.ProvinciaRepository;

@Service
@Transactional
public class GestionHuesped {

    @Autowired
    private HuespedRepository huespedRepository;
    @Autowired
    private PersonaFisicaRepository personaFisicaRepository;
    @Autowired
    private DireccionRepository direccionRepository;
    @Autowired
    private LocalidadRepository localidadRepository;
    @Autowired
    private ProvinciaRepository provinciaRepository;
    @Autowired
    private PaisRepository paisRepository;

    public Huesped registrarHuesped(Huesped nuevoHuesped) {

        // Validaciones
        List<String> errores = validarCampos(nuevoHuesped);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores: " + String.join(", ", errores));
        }

        // Verificar duplicados
        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndDocumento(
                nuevoHuesped.getTipoDocumento(),
                nuevoHuesped.getDocumento()
        );

        if (existente.isPresent()) {
            throw new DuplicateKeyException("El huésped con ese documento ya existe");
        }

        // Pais pais = paisRepository.save(nuevoHuesped.getDireccion().getLocalidad().getProvincia().getPais());
        Pais paisHuesped = nuevoHuesped.getDireccion().getLocalidad().getProvincia().getPais();
        Optional<Pais> paisExistente = paisRepository.findByNombre(paisHuesped.getNombre());

        Pais pais;
        if (paisExistente.isPresent()) {
            pais = paisExistente.get(); // usamos el país que ya existe
        } else {
            pais = paisRepository.save(paisHuesped); // lo guardamos si no existía
        }

        Provincia provinciaHuesped = nuevoHuesped.getDireccion().getLocalidad().getProvincia();
        Optional<Provincia> provinciaExistente = provinciaRepository.findByNombre(provinciaHuesped.getNombre());

        Provincia provincia;
        if (provinciaExistente.isPresent()) {
            provincia = provinciaExistente.get(); // usamos el país que ya existe
        } else {
            provinciaHuesped.setPais(pais);
            provincia = provinciaRepository.save(provinciaHuesped); // lo guardamos si no existía
        }

        Localidad localidadHuesped = nuevoHuesped.getDireccion().getLocalidad();
        Optional<Localidad> localidadExistente = localidadRepository.findByNombre(localidadHuesped.getNombre());

        Localidad localidad;
        if (localidadExistente.isPresent()) {
            localidad = localidadExistente.get(); // usamos el país que ya existe
        } else {
            localidadHuesped.setProvincia(provincia);
            localidad = localidadRepository.save(localidadHuesped); // lo guardamos si no existía
        }

        Direccion direccion = nuevoHuesped.getDireccion();
        direccion.setLocalidad(localidad);
        direccion = direccionRepository.save(direccion);

        nuevoHuesped.setDireccion(direccion);

        return huespedRepository.save(nuevoHuesped);
    }

    private List<String> validarCampos(Huesped h) {
        List<String> errores = new ArrayList<>();

        if (h.getApellido() == null || h.getApellido().isBlank()) {
            errores.add("Apellido");
        }
        if (h.getNombre() == null || h.getNombre().isBlank()) {
            errores.add("Nombres");
        }
        if (h.getDocumento() == null || h.getTipoDocumento() == null) {
            errores.add("Tipo y número de documento");
        }
        if (h.getFechaNacimiento() == null) {
            errores.add("Fecha de nacimiento");
        }
        if (h.getDireccion() == null) {
            errores.add("Dirección");
        }
        if (h.getTelefono() == null) {
            errores.add("Teléfono");
        }

        return errores;
    }

    public List<String> listarTipoDocumento() {
        return Arrays.stream(TipoDocumento.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public Direccion addDireccionToHuesped(Direccion direccion) {

        Pais paisHuesped = direccion.getLocalidad().getProvincia().getPais();
        Optional<Pais> paisExistente = paisRepository.findByNombre(paisHuesped.getNombre());

        Pais pais;
        if (paisExistente.isPresent()) {
            pais = paisExistente.get(); // usamos el país que ya existe
        } else {
            paisHuesped.setId(null);
            pais = paisRepository.save(paisHuesped); // lo guardamos si no existía
        }

        Provincia provinciaHuesped = direccion.getLocalidad().getProvincia();
        Optional<Provincia> provinciaExistente = provinciaRepository.findByNombre(provinciaHuesped.getNombre());

        Provincia provincia;
        if (provinciaExistente.isPresent()) {
            provincia = provinciaExistente.get(); // usamos el país que ya existe
        } else {
            provinciaHuesped.setPais(pais);
            provincia = provinciaRepository.save(provinciaHuesped); // lo guardamos si no existía
        }

        Localidad localidadHuesped = direccion.getLocalidad();
        Optional<Localidad> localidadExistente = localidadRepository.findByNombre(localidadHuesped.getNombre());

        Localidad localidad;
        if (localidadExistente.isPresent()) {
            localidad = localidadExistente.get(); // usamos el país que ya existe
        } else {
            localidadHuesped.setProvincia(provincia);
            localidad = localidadRepository.save(localidadHuesped); // lo guardamos si no existía
        }

        direccion.setLocalidad(localidad);
        return direccionRepository.save(direccion);
    }

    public HuespedDTO registrarNuevoHuesped(Huesped nuevoHuesped) {

        // Validaciones
        List<String> errores = validarCampos(nuevoHuesped);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores: " + String.join(", ", errores));
        }

        // Verificar duplicados
        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndDocumento(
                nuevoHuesped.getTipoDocumento(),
                nuevoHuesped.getDocumento()
        );

        if (existente.isPresent()) {
            throw new DuplicateKeyException("El huésped con ese documento ya existe");
        }

        Direccion direccionGuardada = addDireccionToHuesped(nuevoHuesped.getDireccion());
        nuevoHuesped.setDireccion(direccionGuardada);

        Huesped guardado = huespedRepository.save(nuevoHuesped);
        return HuespedMapper.toDTO(guardado);
    }

    public boolean existeDocumento(TipoDocumento tipo, String documento) {
        return huespedRepository
                .findByTipoDocumentoAndDocumento(tipo, documento)
                .isPresent();
    }

    public void guardarSinValidar(Huesped h) {
        huespedRepository.save(h);
    }

}

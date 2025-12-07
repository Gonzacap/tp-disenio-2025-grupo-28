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

    //camino que valida el NO duplicado = ACEPAR IGUALMENTE
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

    public List<String> validarCampos(Huesped h) {
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
//camino del flujo principal, cuando no hay DNI duplicado

    public Huesped registrarNuevoHuesped(Huesped nuevoHuesped) {

        // Validaciones
        List<String> errores = validarCampos(nuevoHuesped);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores: " + String.join(", ", errores));
        }

        // Verificar duplicados
        Optional<Huesped> existente = huespedRepository.findByTipoDocumentoAndDocumento(
                nuevoHuesped.getTipoDocumento(),
                nuevoHuesped.getDocumento());

        if (existente.isPresent()) {
            throw new DuplicateKeyException("El huésped con ese documento ya existe");
        }

        // Dirección completa (reusar o crear)
        Direccion direccion = obtenerOcrearDireccion(nuevoHuesped);
        nuevoHuesped.setDireccion(direccion);

        return huespedRepository.save(nuevoHuesped);
    }

    public boolean existeDocumento(TipoDocumento tipo, String documento) {
        return huespedRepository
                .findByTipoDocumentoAndDocumento(tipo, documento)
                .isPresent();
    }

    public void guardarSinValidar(Huesped h) {
        huespedRepository.save(h);
    }

    // cu 2
    public List<Huesped> buscarHuespedFinal(
            String apellido,
            String nombre,
            TipoDocumento tipoDocumento,
            String documento) {

        List<Huesped> candidatos = new ArrayList<>();

        if (documento != null && !documento.isBlank()) {

            if (tipoDocumento != null) {
                candidatos.addAll(
                        huespedRepository.findAllByTipoDocumentoAndDocumento(tipoDocumento, documento));
            } else {
                Huesped h = huespedRepository.findFirstByDocumento(documento);
                if (h != null) {
                    candidatos.add(h);
                }
            }

        } else {

            if (apellido != null && !apellido.isBlank()) {
                candidatos.addAll(huespedRepository.findByApellidoContainingIgnoreCase(apellido));
            }

            if (nombre != null && !nombre.isBlank()) {
                candidatos.addAll(huespedRepository.findByNombreContainingIgnoreCase(nombre));
            }

            if (tipoDocumento != null) {
                candidatos.addAll(huespedRepository.findAllByTipoDocumento(tipoDocumento));
            }
        }

        if (candidatos.isEmpty()) {
            return candidatos;
        }

        return candidatos.stream()
                .filter(h -> apellido == null || apellido.isBlank()
                || h.getApellido().toLowerCase().contains(apellido.toLowerCase()))
                .filter(h -> nombre == null || nombre.isBlank()
                || h.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(h -> tipoDocumento == null
                || h.getTipoDocumento() == tipoDocumento)
                .filter(h -> documento == null || documento.isBlank()
                || h.getDocumento().equals(documento))
                .distinct()
                .toList();
    }

    public Huesped buscarUnicoPorDocumento(String documento) {
        return huespedRepository.findFirstByDocumento(documento);
    }

    //METODOS AUXILIAR PARA DIRECCION
    public Direccion obtenerOcrearDireccion(Huesped nuevoHuesped) {
        Pais paisHuesped = nuevoHuesped.getDireccion().getLocalidad().getProvincia().getPais();
        Optional<Pais> paisExistente = paisRepository.findByNombre(paisHuesped.getNombre());

        Pais pais;
        if (paisExistente.isPresent()) {
            pais = paisExistente.get();
        } else {
            pais = paisRepository.save(paisHuesped);
        }

        Provincia provinciaHuesped = nuevoHuesped.getDireccion().getLocalidad().getProvincia();
        Optional<Provincia> provinciaExistente = provinciaRepository.findByNombre(provinciaHuesped.getNombre());

        Provincia provincia;
        if (provinciaExistente.isPresent()) {
            provincia = provinciaExistente.get();
        } else {
            provinciaHuesped.setPais(pais);
            provincia = provinciaRepository.save(provinciaHuesped);
        }

        Localidad localidadHuesped = nuevoHuesped.getDireccion().getLocalidad();
        Optional<Localidad> localidadExistente = localidadRepository.findByNombre(localidadHuesped.getNombre());

        Localidad localidad;
        if (localidadExistente.isPresent()) {
            localidad = localidadExistente.get();
        } else {
            localidadHuesped.setProvincia(provincia);
            localidad = localidadRepository.save(localidadHuesped);
        }

        Direccion direccion = nuevoHuesped.getDireccion();
        direccion.setLocalidad(localidad);

        return direccionRepository.save(direccion);
    }
}

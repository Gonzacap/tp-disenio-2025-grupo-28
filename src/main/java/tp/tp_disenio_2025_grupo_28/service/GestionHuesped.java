package tp.tp_disenio_2025_grupo_28.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
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
        //CUIT tecnico, para la PK de Huesped
        if (nuevoHuesped.getCuit() == null || nuevoHuesped.getCuit().isBlank()) {
            String cuitTecnico = "H-"
                    + nuevoHuesped.getTipoDocumento()
                    + "-"
                    + nuevoHuesped.getDocumento();
            nuevoHuesped.setCuit(cuitTecnico);
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
        Date hoy = new Date();

        if (h.getApellido() == null || h.getApellido().isBlank()) {
            errores.add("Apellido");
        }

        if (h.getNombre() == null || h.getNombre().isBlank()) {
            errores.add("Nombres");
        }

        if (h.getTipoDocumento() == null || h.getDocumento() == null || h.getDocumento().isBlank()) {
            errores.add("Tipo y número de documento");
        }

        if (h.getFechaNacimiento() == null) {
            errores.add("Fecha de nacimiento");
        } else if (h.getFechaNacimiento().after(hoy)) {
            errores.add("La fecha de nacimiento no puede ser posterior a la fecha actual");
        }

        if (h.getDireccion() == null) {
            errores.add("Dirección");
        } else {
            if (h.getDireccion().getNacionalidad() == null || h.getDireccion().getNacionalidad().isBlank()) {
                errores.add("Nacionalidad");
            }
        }

        if (h.getTelefono() == null || h.getTelefono().isBlank()) {
            errores.add("Teléfono");
        }

        if (("MONOTRIBUTO".equals(h.getPosicionFrenteAlIva())
                || "RESPONSABLE_INSCRIPTO".equals(h.getPosicionFrenteAlIva()))
                && (h.getCuit() == null || h.getCuit().isBlank())) {

            errores.add("CUIT obligatorio para " + h.getPosicionFrenteAlIva());
            return errores; // no sigo si ya falta
        }

        if (h.getCuit() != null && !h.getCuit().isBlank()) {

            // CUIT técnico: se acepta sin validar
            if (h.getCuit().startsWith("H-")) {
                return errores;
            }

            String cuit = h.getCuit().replaceAll("-", "").trim();
            String dni = h.getDocumento();

            if (!cuit.matches("\\d{11}")) {
                errores.add("CUIT inválido (formato esperado: XX-XXXXXXXX-X)");
                return errores;
            }

            if (dni == null || !dni.matches("\\d+")) {
                errores.add("DNI inválido");
                return errores;
            }

            String dniDesdeCuit = cuit.substring(2, 10);

            if (!dniDesdeCuit.equals(dni)) {
                errores.add("El CUIT no coincide con el DNI ingresado");
                return errores;
            }

            if (!validarCuit(cuit)) { // ← SOLO NUMÉRICO
                errores.add("El CUIT ingresado no es válido");
            }
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
        //CUIT tecnico, para la PK de Huesped
        if (nuevoHuesped.getCuit() == null || nuevoHuesped.getCuit().isBlank()) {
            String cuitTecnico = "H-"
                    + nuevoHuesped.getTipoDocumento()
                    + "-"
                    + nuevoHuesped.getDocumento();
            nuevoHuesped.setCuit(cuitTecnico);
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

    public static boolean validarCuit(String cuit) {
        cuit = cuit.replaceAll("-", "");
        if (!cuit.matches("\\d{11}")) {
            return false;
        }

        int[] factores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;

        for (int i = 0; i < 10; i++) {
            suma += Character.getNumericValue(cuit.charAt(i)) * factores[i];
        }

        int resto = suma % 11;
        int digito = resto == 0 ? 0 : resto == 1 ? 9 : 11 - resto;

        return digito == Character.getNumericValue(cuit.charAt(10));
    }

    public List<PersonaFisica> findAllByCuitIn(List<String> ocupantes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    public List<PersonaFisica> buscarHuesped(
            String apellido,
            String nombre,
            TipoDocumento tipoDocumento,
            String documento) {

        // Obtengo personas que cumplen los criterios
        List<PersonaFisica> personas = buscarPersona(apellido, nombre, tipoDocumento, documento);

        if (personas.isEmpty()) {
            return List.of();
        }

        // Filtrar solo las que tienen registro de Huesped
        return personas.stream()
                .filter(p -> huespedRepository.existsById(p.getId()))
                .distinct()
                .toList();
    }

    /**
     *
     */
    public List<PersonaFisica> buscarPersona(
            String apellido,
            String nombre,
            TipoDocumento tipoDocumento,
            String documento) {

        // 1) Si tengo documento, hago búsqueda directa optimizada
        if (tieneContenido(documento)) {
            return buscarPersonaPorDocumento(tipoDocumento, documento).stream()
                    .filter(h -> coincide(h.getApellido(), apellido))
                    .filter(h -> coincide(h.getNombre(), nombre))
                    .filter(h -> tipoDocumento == null || h.getTipoDocumento() == tipoDocumento)
                    .distinct()
                    .toList();
        }

        // 2) Si no hay documento, hago búsqueda ampliada
        List<PersonaFisica> candidatos = new ArrayList<>();
        if (tieneContenido(apellido)) {
            candidatos.addAll(personaFisicaRepository.findByApellidoContainingIgnoreCase(apellido));
        }
        if (tieneContenido(nombre)) {
            candidatos.addAll(personaFisicaRepository.findByNombreContainingIgnoreCase(nombre));
        }
        if (tipoDocumento != null) {
            candidatos.addAll(personaFisicaRepository.findAllByTipoDocumento(tipoDocumento));
        }

        return candidatos.stream()
                .filter(h -> coincide(h.getApellido(), apellido))
                .filter(h -> coincide(h.getNombre(), nombre))
                .distinct()
                .toList();
    }

    private boolean tieneContenido(String s) {
        return s != null && !s.isBlank();
    }

    private boolean coincide(String valorEntidad, String filtro) {
        return !tieneContenido(filtro)
                || (valorEntidad != null
                && valorEntidad.toLowerCase().contains(filtro.toLowerCase()));
    }

    private List<PersonaFisica> buscarPersonaPorDocumento(TipoDocumento tipoDocumento, String documento) {
        if (tipoDocumento != null) {
            return personaFisicaRepository.findAllByTipoDocumentoAndDocumento(tipoDocumento, documento);
        }
        PersonaFisica p = personaFisicaRepository.findFirstByDocumento(documento);
        return p == null ? List.of() : List.of(p);
    }

    /**
     * Devuelve el Huesped asociado a una PersonaFisica
     */
    public Huesped obtenerHuesped(PersonaFisica persona) {

        if (persona == null || persona.getId() == null) {
            return null;
        }

        return huespedRepository.findById(persona.getId()).orElse(null);
    }

    /**
     * Busca los huespedes que contengan con los parametros de busqueda
     */
    public List<Huesped> buscarHuespedes(String apellido, String nombre, TipoDocumento tipoDocumento, String documento){
        return huespedRepository.buscarHuespedes(apellido, nombre, tipoDocumento, documento);
    };

}

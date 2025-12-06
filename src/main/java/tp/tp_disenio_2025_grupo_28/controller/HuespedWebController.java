/*package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.Localidad;
import tp.tp_disenio_2025_grupo_28.model.Pais;
import tp.tp_disenio_2025_grupo_28.model.Provincia;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.repository.LocalidadRepository;
import tp.tp_disenio_2025_grupo_28.repository.PaisRepository;
import tp.tp_disenio_2025_grupo_28.repository.ProvinciaRepository;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;

@Controller
@RequestMapping("/huespedes")
public class HuespedWebController {

    @Autowired
    private GestionHuesped gestionHuesped;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    // FORMULARIO INICIAL
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {

        Huesped h = new Huesped();
        Direccion d = new Direccion();
        Localidad loc = new Localidad();
        Provincia prov = new Provincia();
        Pais pais = new Pais();

        prov.setPais(pais);
        loc.setProvincia(prov);
        d.setLocalidad(loc);
        h.setDireccion(d);

        model.addAttribute("huesped", h);
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());

        return "huesped/huesped-form";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // GUARDAR HUÉSPED (CU09)
    // @PostMapping("/guardar")
    // public String guardarHuesped(
    //         @ModelAttribute("huesped") Huesped huesped,
    //         Model model) {
    //     StringBuilder errores = new StringBuilder();
    //     if (esVacio(huesped.getNombre())) {
    //         errores.append("Debe ingresar el Nombre.\n");
    //     }
    //     if (esVacio(huesped.getApellido())) {
    //         errores.append("Debe ingresar el Apellido.\n");
    //     }
    //     if (huesped.getTipoDocumento() == null) {
    //         errores.append("Debe seleccionar el Tipo de documento.\n");
    //     }
    //     if (esVacio(huesped.getDocumento())) {
    //         errores.append("Debe ingresar el Número de documento.\n");
    //     }
    //     if (huesped.getFechaNacimiento() == null) {
    //         errores.append("Debe ingresar la Fecha de nacimiento.\n");
    //     }
    //     if (esVacio(huesped.getTelefono())) {
    //         errores.append("Debe ingresar el Teléfono.\n");
    //     }
    //     // regla del CU
    //     if (huesped.getPosicionFrenteAlIva() != null
    //             && huesped.getPosicionFrenteAlIva().toString().equals("RESPONSABLE_INSCRIPTO")
    //             && esVacio(huesped.getCuit())) {
    //         errores.append("Debe ingresar CUIT porque es Responsable Inscripto.\n");
    //     }
    //     // Si hay errores S
    //     if (errores.length() > 0) {
    //         List<String> listaErrores = Arrays.stream(errores.toString().split("\\r?\\n"))
    //                 .filter(s -> !s.isBlank())
    //                 .collect(Collectors.toList());
    //         model.addAttribute("errorList", listaErrores);
    //         model.addAttribute("huesped", huesped);
    //         model.addAttribute("tiposDocumento", TipoDocumento.values());
    //         model.addAttribute("paises", paisRepository.findAll());
    //         model.addAttribute("provincias", provinciaRepository.findAll());
    //         model.addAttribute("localidades", localidadRepository.findAll());
    //         return "huesped/huesped-form"; // ← volver al formulario
    //     }
    //     if (gestionHuesped.existeDocumento(huesped.getTipoDocumento(), huesped.getDocumento())) {
    //         if (huesped.getDireccion() == null) {
    //             huesped.setDireccion(new Direccion());
    //         }
    //         if (huesped.getDireccion().getLocalidad() == null) {
    //             huesped.getDireccion().setLocalidad(new Localidad());
    //         }
    //         if (huesped.getDireccion().getLocalidad().getProvincia() == null) {
    //             huesped.getDireccion().getLocalidad().setProvincia(new Provincia());
    //         }
    //         if (huesped.getDireccion().getLocalidad().getProvincia().getPais() == null) {
    //             huesped.getDireccion().getLocalidad().getProvincia().setPais(new Pais());
    //         }
    //         model.addAttribute("titulo", "¡CUIDADO!");
    //         model.addAttribute("mensaje", "El tipo y número de documento ya existen en el sistema.");
    //         model.addAttribute("accionAceptar", "/huespedes/forzar-guardar");
    //         model.addAttribute("accionCorregir", "/huespedes/corregir"); // corregir → POST que devuelve form
    //         model.addAttribute("objeto", huesped);
    //         model.addAttribute("focusField", "tipoDocumento");
    //         return "emergentes/advertencia";
    //     }
    //     gestionHuesped.registrarHuesped(huesped);
    //     model.addAttribute("titulo", "Huésped registrado");
    //     model.addAttribute("mensaje", "El huésped fue cargado correctamente. ¿Desea cargar otro?");
    //     model.addAttribute("accionAceptar", "/huespedes/nuevo");
    //     model.addAttribute("accionCancelar", "/");
    //     return "emergentes/exito";
    // }
    @PostMapping("/guardar")
    public String guardarHuesped(@ModelAttribute("huesped") Huesped huesped,
            BindingResult bindingResult,
            Model model) {

        try {
            // llamo a la lógica que ya funciona
            gestionHuesped.registrarHuesped(huesped);

            model.addAttribute("mensaje", "El huésped " + huesped.getNombre() + " " + huesped.getApellido() + " ha sido registrado correctamente. ¿Desea cargar otro?");

            // limpiar formulario: nuevo Huesped con nested objects
            Huesped hnew = new Huesped();
            Direccion d = new Direccion();
            Localidad loc = new Localidad();
            Provincia prov = new Provincia();
            Pais pais = new Pais();
            prov.setPais(pais);
            loc.setProvincia(prov);
            d.setLocalidad(loc);
            hnew.setDireccion(d);

            model.addAttribute("huesped", hnew);

        } catch (DuplicateKeyException dk) {
            model.addAttribute("error", "¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            model.addAttribute("huespedDuplicado", huesped);
            return "huesped/huesped-duplicado";

        } catch (IllegalArgumentException iae) {
            model.addAttribute("error", "Faltan datos obligatorios: " + iae.getMessage());
            model.addAttribute("huesped", huesped);
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al guardar: " + e.getMessage());
            model.addAttribute("huesped", huesped);
        }

        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());

        return "huesped/huesped-confirmacion";
    }

    @PostMapping("/forzar-guardar")
    public String forzarGuardar(@ModelAttribute("huesped") Huesped huesped, Model model) {

        try {

            Direccion direccionGuardada = gestionHuesped.addDireccionToHuesped(huesped.getDireccion());
            huesped.setDireccion(direccionGuardada);
            gestionHuesped.guardarSinValidar(huesped);
            model.addAttribute("titulo", "Huésped registrado");
            model.addAttribute("mensaje", "El huésped fue cargado correctamente. ¿Desea cargar otro?");
            model.addAttribute("accionAceptar", "/huespedes/nuevo");
            model.addAttribute("accionCancelar", "/");
            return "emergentes/exito";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
            return "emergentes/error";
        }
    }

    @GetMapping("/cancelar")
    public String cancelar(Model model) {

        model.addAttribute("titulo", "Cancelar alta");
        model.addAttribute("mensaje", "¿Desea cancelar el alta del huésped?");
        model.addAttribute("accionAceptar", "/");
        model.addAttribute("accionCancelar", "/huespedes/nuevo");

        return "emergentes/confirmacion";
    }

    private boolean esVacio(String s) {
        return s == null || s.trim().isBlank();
    }

    @PostMapping("/corregir")
    public String corregir(@ModelAttribute("huesped") Huesped huesped, Model model) {

        model.addAttribute("huesped", huesped);

        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());
        model.addAttribute("focusField", "tipoDocumento");
        return "huesped/huesped-form";
    }

    // cu 2
    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("apellido", "");
        model.addAttribute("nombre", "");
        model.addAttribute("tipoDocumento", "");
        model.addAttribute("documento", "");
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        return "huesped/buscar-huesped";
    }

    @PostMapping("/buscar")
    public String procesarBusqueda(
            String apellido,
            String nombre,
            String tipoDocumento,
            String documento,
            Model model) {

        TipoDocumento tipoDocEnum = null;

        if (tipoDocumento != null && !tipoDocumento.isBlank()) {
            try {
                tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
            } catch (Exception e) {

            }
        }

        List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(
                apellido,
                nombre,
                tipoDocEnum,
                documento);

        if (resultados.isEmpty()) {
            return "redirect:/huespedes/nuevo";
        }

        model.addAttribute("huespedes", resultados);
        model.addAttribute("apellido", apellido);
        model.addAttribute("nombre", nombre);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("documento", documento);
        model.addAttribute("tiposDocumento", TipoDocumento.values());

        return "huesped/buscar-huesped";
    }

    @PostMapping("/siguiente")
    public String procesarSiguiente(
            @RequestParam(value = "huespedSeleccionado", required = false) String documentoSeleccionado,
            Model model) {

        if (documentoSeleccionado == null || documentoSeleccionado.isBlank()) {
            return "redirect:/huespedes/nuevo";
        }

        Huesped h = gestionHuesped.buscarUnicoPorDocumento(documentoSeleccionado);

        if (h == null) {

            return "redirect:/huespedes/nuevo";
        }

        model.addAttribute("huesped", h);

        return "huesped/cu10";
    }

}
 */
package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.Localidad;
import tp.tp_disenio_2025_grupo_28.model.Pais;
import tp.tp_disenio_2025_grupo_28.model.Provincia;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.repository.LocalidadRepository;
import tp.tp_disenio_2025_grupo_28.repository.PaisRepository;
import tp.tp_disenio_2025_grupo_28.repository.ProvinciaRepository;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;

@Controller
@RequestMapping("/huespedes")
public class HuespedWebController {

    @Autowired
    private GestionHuesped gestionHuesped;

    // si preferís no tocar servicio, autowireo repos directos para llenar selects
    @Autowired
    private PaisRepository paisRepository;
    @Autowired
    private ProvinciaRepository provinciaRepository;
    @Autowired
    private LocalidadRepository localidadRepository;

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        // inicializamos objetos anidados para evitar problemas de binding
        Huesped h = new Huesped();
        Direccion d = new Direccion();
        Localidad loc = new Localidad();
        Provincia prov = new Provincia();
        Pais pais = new Pais();

        // armamos la jerarquía vacía
        prov.setPais(pais);
        loc.setProvincia(prov);
        d.setLocalidad(loc);
        h.setDireccion(d);

        model.addAttribute("huesped", h);
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());

        //  return "huesped/huesped-form";
        return "huesped/huesped-form";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Fecha en formato HTML date input (yyyy-MM-dd)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

        // Enteros (telefono, numero) — permite null
        // binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }

    @GetMapping()
    public String main(String name, RedirectAttributes redirectAttributes) {
        return "huesped/index";
    }

    @PostMapping("/guardar")
    public String guardarHuesped(@ModelAttribute("huesped") Huesped huesped,
            BindingResult bindingResult,
            Model model) {

        try {
            // llamo a la lógica que ya funciona
            gestionHuesped.registrarHuesped(huesped);

            model.addAttribute("mensaje", "El huésped " + huesped.getNombre() + " " + huesped.getApellido() + " ha sido registrado correctamente. ¿Desea cargar otro?");

            // limpiar formulario: nuevo Huesped con nested objects
            Huesped hnew = new Huesped();
            Direccion d = new Direccion();
            Localidad loc = new Localidad();
            Provincia prov = new Provincia();
            Pais pais = new Pais();
            prov.setPais(pais);
            loc.setProvincia(prov);
            d.setLocalidad(loc);
            hnew.setDireccion(d);

            model.addAttribute("huesped", hnew);

        } catch (DuplicateKeyException dk) {
            model.addAttribute("error", "¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            model.addAttribute("huespedDuplicado", huesped);
            return "huesped/huesped-duplicado";

        } catch (IllegalArgumentException iae) {
            model.addAttribute("error", "Faltan datos obligatorios: " + iae.getMessage());
            model.addAttribute("huesped", huesped);
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al guardar: " + e.getMessage());
            model.addAttribute("huesped", huesped);
        }

        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());

        return "huesped/huesped-confirmacion";
    }
}

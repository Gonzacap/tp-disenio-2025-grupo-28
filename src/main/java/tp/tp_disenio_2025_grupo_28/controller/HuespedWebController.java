package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        // construyo jerarquía vacía para evitar NullPointer en los th:field
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

    // CONVERSIÓN DE FECHAS
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // GUARDAR HUÉSPED (CU09)
    @PostMapping("/guardar")
    public String guardarHuesped(
            @ModelAttribute("huesped") Huesped huesped,
            Model model) {

        // 2.A – VALIDAR CAMPOS OBLIGATORIOS
        StringBuilder errores = new StringBuilder();

        if (esVacio(huesped.getNombre())) {
            errores.append("Debe ingresar el Nombre.\n");
        }

        if (esVacio(huesped.getApellido())) {
            errores.append("Debe ingresar el Apellido.\n");
        }

        if (huesped.getTipoDocumento() == null) {
            errores.append("Debe seleccionar el Tipo de documento.\n");
        }

        if (esVacio(huesped.getDocumento())) {
            errores.append("Debe ingresar el Número de documento.\n");
        }

        if (huesped.getFechaNacimiento() == null) {
            errores.append("Debe ingresar la Fecha de nacimiento.\n");
        }

        if (esVacio(huesped.getTelefono())) {
            errores.append("Debe ingresar el Teléfono.\n");
        }

        // regla del CU
        if ("RESPONSABLE_INSCRIPTO".equals(huesped.getPosicionFrenteAlIva())
                && esVacio(huesped.getCuit())) {
            errores.append("Debe ingresar CUIT porque es Responsable Inscripto.\n");
        }

        // Si hay errores → emergente ERROR
        if (errores.length() > 0) {
            model.addAttribute("mensaje", errores.toString());
            return "emergentes/error";
        }
        // 2.B – CHEQUEAR DUPLICADO (se hace aparte)

        if (gestionHuesped.existeDocumento(huesped.getTipoDocumento(), huesped.getDocumento())) {
            if (huesped.getDireccion() == null) {
                huesped.setDireccion(new Direccion());
            }
            if (huesped.getDireccion().getLocalidad() == null) {
                huesped.getDireccion().setLocalidad(new Localidad());
            }
            if (huesped.getDireccion().getLocalidad().getProvincia() == null) {
                huesped.getDireccion().getLocalidad().setProvincia(new Provincia());
            }
            if (huesped.getDireccion().getLocalidad().getProvincia().getPais() == null) {
                huesped.getDireccion().getLocalidad().getProvincia().setPais(new Pais());
            }

            model.addAttribute("titulo", "¡CUIDADO!");
            model.addAttribute("mensaje", "El tipo y número de documento ya existen en el sistema.");
            model.addAttribute("accionAceptar", "/huespedes/forzar-guardar");
            model.addAttribute("accionCancelar", "/huespedes/nuevo");

            model.addAttribute("objeto", huesped);

            return "emergentes/advertencia";
        }

        // FLUJO PRINCIPAL → Guardar huésped
        gestionHuesped.registrarHuesped(huesped);

        model.addAttribute("titulo", "Huésped registrado");
        model.addAttribute("mensaje", "El huésped fue cargado correctamente. ¿Desea cargar otro?");
        model.addAttribute("accionAceptar", "/huespedes/nuevo");
        model.addAttribute("accionCancelar", "/");

        return "emergentes/exito";
    }

    // 2.B.2.1 – FORZAR GUARDADO
    @PostMapping("/forzar-guardar")
    public String forzarGuardar(@ModelAttribute("huesped") Huesped huesped, Model model) {

        try {
            // Guardar dirección / localidad / provincia / país
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
            // opcional: loggear stacktrace en consola
            e.printStackTrace();
            return "emergentes/error";
        }
    }

    // 2.C – CANCELAR
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

        // volvemos al formulario con los valores ya cargados
        model.addAttribute("huesped", huesped);

        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());

        return "huesped/huesped-form";
    }

}

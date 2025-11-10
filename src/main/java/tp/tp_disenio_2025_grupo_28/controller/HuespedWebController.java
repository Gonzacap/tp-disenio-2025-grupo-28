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

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.stereotype.Controller;

@Controller
//@RequestMapping("/huespedes")
public class HuespedWebController {
    /* 
    @Autowired
    private GestionHuesped gestionHuesped;
    @Autowired
    private PaisRepository paisRepository;
    @Autowired
    private ProvinciaRepository provinciaRepository;
    @Autowired
    private LocalidadRepository localidadRepository;

    //CASO DE USO 9 - DAR ALTA HUESPED
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

        List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(apellido, nombre, tipoDocEnum, documento);

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
     */
}

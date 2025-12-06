package tp.tp_disenio_2025_grupo_28.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.dto.HuespedDTO;
import tp.tp_disenio_2025_grupo_28.mapper.HuespedMapper;
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
public class HuespedWebController2 {

    @Autowired
    private GestionHuesped gestionHuesped;
    @Autowired
    private PaisRepository paisRepository;
    @Autowired
    private ProvinciaRepository provinciaRepository;
    @Autowired
    private LocalidadRepository localidadRepository;

    //  CU02 (NO TOCAR)  
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
            } catch (Exception ignored) {
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

    //caso de uso 9
    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("huesped", new HuespedDTO());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());
        return "huesped/huesped-form";
    }

    //Cuando el usuario presiona "SIGUIENTE" puede disparar
    //2A. El actor no ingresa todos los datos
    //2B. El tipo y numero de documento ya existen
    // o ir a 3
    @PostMapping("/guardar")
    public String procesarAltaSiguiente(@ModelAttribute("huesped") HuespedDTO dto, BindingResult bindingResult, Model model) {
        //armamos la entidad
        Huesped huesped = HuespedMapper.toEntity(dto);
        huesped.setDireccion(construirDireccionDesdeDto(dto));
        try {
            //Flujo principal (2->3), usando el camino normal, validamos duplicados
            Huesped guardado = gestionHuesped.registrarNuevoHuesped(huesped);
            //exito, mostramos mensaje preguntamos si o no
            model.addAttribute("mostrarModalExito", true);
            model.addAttribute("mensajeExito", "El huesped ha sido registrado en el sistema exitosamente.<br>Nombre:" + guardado.getNombre() + "<br>Apellido: " + guardado.getApellido()
                    + "<br> ¿Desea cargar otro?");
            model.addAttribute("huesped", new HuespedDTO()); //Dejamos listo para el sI
            cargarListas(model);
            return "huesped/huesped-form";

        } catch (IllegalArgumentException iae) {
            //2A
            String mensaje = iae.getMessage() == null ? "" : iae.getMessage();
            model.addAttribute("errorList", java.util.Arrays.asList(mensaje.replace("Errores:", "").trim().split("\\s*,\\s*")));
            model.addAttribute("mostrarModalError", true);
            model.addAttribute("huesped", dto); // mantener lo que el usuario ya tipeó
            cargarListas(model);
            return "huesped/huesped-form";
        } catch (DuplicateKeyException dke) {
            //2B duplicado
            model.addAttribute("duplicado", true);
            model.addAttribute("mensajeDuplicado", "¡CUIDADO! El tipo y número de documento ya existen en el sistema.");
            model.addAttribute("huesped", dto); // mantener lo ingresado para CORREGIR
            cargarListas(model);
            return "huesped/huesped-form";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al guardar: " + e.getMessage());
            model.addAttribute("huesped", dto);
            cargarListas(model);
            return "huesped/huesped-form";
        }
    }

    //2B.2.1 Acepptar igualmente
    @PostMapping("/aceptar-igualmente")
    public String aceptarIgualmente(@ModelAttribute("huesped") HuespedDTO dto, Model model) {
        Huesped entity = HuespedMapper.toEntity(dto);
        entity.setDireccion(construirDireccionDesdeDto(dto));

        Huesped guardado = gestionHuesped.registrarHuesped(entity);

        model.addAttribute("exito", true);
        model.addAttribute("mensajeExito",
                "El huésped " + guardado.getNombre() + " " + guardado.getApellido()
                + " ha sido satisfactoriamente cargado al sistema. ¿Desea cargar otro?");
        model.addAttribute("huesped", new HuespedDTO());
        cargarListas(model);
        return "huesped/huesped-form";
    }

    // (2.B.2.2) CORREGIR -> vuelve al form con foco en tipoDocumento (sin perder datos)
    @PostMapping("/corregir")
    public String corregir(@ModelAttribute("huesped") HuespedDTO dto, Model model) {
        model.addAttribute("focusField", "tipoDocumento");
        model.addAttribute("huesped", dto);
        cargarListas(model);
        return "huesped/huesped-form";
    }

    // (2.C) CANCELAR -> confirmación: SI = fin (volver a CU02); NO = quedarse con los datos
    @PostMapping("/cancelar")
    public String cancelar(@ModelAttribute("huesped") HuespedDTO dto,
            @RequestParam("confirm") String confirm,
            Model model) {

        if ("SI".equalsIgnoreCase(confirm)) {

            return "huesped/index";
        }

        // NO: vuelve al punto 1 (pero manteniendo lo ingresado)
        model.addAttribute("huesped", dto);
        cargarListas(model);
        return "huesped/huesped-form";
    }

    // (4-5) luego del éxito: "¿desea cargar otro?" -> SI limpia, NO vuelve a CU02
    @PostMapping("/continuar")
    public String continuarDespuesExito(@RequestParam("respuesta") String respuesta, Model model) {
        if ("si".equalsIgnoreCase(respuesta)) {
            model.addAttribute("huesped", new HuespedDTO());
            cargarListas(model);
            return "huesped/huesped-form";
        }
        return "huesped/index";
    }

// Helpers (NO clases nuevas)
    // =========================
    private void cargarListas(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("paises", paisRepository.findAll());
        model.addAttribute("provincias", provinciaRepository.findAll());
        model.addAttribute("localidades", localidadRepository.findAll());
    }

    private Direccion construirDireccionDesdeDto(HuespedDTO dto) {
        Pais pais = new Pais();
        pais.setNombre(dto.getPais());

        Provincia prov = new Provincia();
        prov.setNombre(dto.getProvincia());
        prov.setPais(pais);

        Localidad loc = new Localidad();
        loc.setNombre(dto.getLocalidad());
        loc.setProvincia(prov);

        Direccion d = new Direccion();
        d.setDireccion(dto.getDireccion());

        d.setDepto(dto.getDepto());
        d.setPiso(dto.getPiso());
        // d.setCodigoPostal(dto.getCodigoPostal());
        d.setLocalidad(loc);

        return d;
    }

    // (opcional) si querés seguir usando el "main" que ya tenías
    @GetMapping()
    public String main(String name, RedirectAttributes redirectAttributes) {
        return "huesped/index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}

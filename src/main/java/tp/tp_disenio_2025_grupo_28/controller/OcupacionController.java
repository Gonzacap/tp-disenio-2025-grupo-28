package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionHuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;

@Controller
@RequestMapping("/ocupacion")
public class OcupacionController {

    @Autowired
    private GestionHabitacion gestionHabitacion;

    @Autowired
    private GestionHuesped gestionHuesped;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    // 1) MOSTRAR BUSCADOR CU02
    /*  @GetMapping("/buscar")
    public String mostrarBuscar(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("tiposDocumento", TipoDocumento.values());

        return "ocuparHabitacion/buscar";
    }*/
    @GetMapping("/buscar")
    public String mostrarBuscar(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            Model model) {

        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("numeroDocumento", numeroDocumento);

        return "ocuparHabitacion/buscar";
    }

    // 2) PROCESAR BÚSQUEDA CU02
    @PostMapping("/buscar")
    public String procesarBusqueda(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        TipoDocumento tipoDocEnum = null;
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            try {
                tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
            } catch (Exception e) {

                model.addAttribute("mensaje", "Ocurrió un error: " + e.getMessage());
                e.printStackTrace();
                return "emergentes/error";
            }
        }

        // ← CU02 EXACTO
        List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(
                apellido, nombre, tipoDocEnum, numeroDocumento
        );

        model.addAttribute("resultados", resultados);
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/buscar";
    }

    /*  @GetMapping(value = "/buscar", params = {"nombre", "apellido", "tipoDocumento", "numeroDocumento"})
    public String buscarGet(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        TipoDocumento tipoDocEnum = null;
        if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
            try {
                tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
            } catch (Exception ignored) {
            }

        }

        List<Huesped> resultados = gestionHuesped.buscarHuespedFinal(
                apellido, nombre, tipoDocEnum, numeroDocumento
        );

        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("numeroDocumento", numeroDocumento);
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/buscar";
    }*/
    // 3) SELECCIONAR RESPONSABLE + ACOMPAÑANTES
    @PostMapping("/seleccionar")
    public String seleccionarOcupantes(
            @RequestParam(required = false) List<String> seleccionados,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            RedirectAttributes redirectAttributes) {

        // Validación CU15 punto 8.A
        if (seleccionados == null || seleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe seleccionar al menos un huésped (CU15 paso 8.A)"
            );

            return "redirect:/ocupacion/buscar?numero_habitacion="
                    + numero_habitacion
                    + "&reservaId=" + reservaId
                    + "&fechaDesde=" + fechaDesde
                    + "&fechaHasta=" + fechaHasta;
        }

        OcupacionHuespedDTO dto = new OcupacionHuespedDTO();

        dto.setIdHuesped(seleccionados.get(0));
        dto.setIdAcompanantes(seleccionados.subList(1, seleccionados.size()));

        // REQUEST DTO
        OcupacionRequestDTO req = new OcupacionRequestDTO();
        req.setNumeroHabitacion(numero_habitacion);

        try {
            req.setFechaDesde(df.parse(fechaDesde));
            req.setFechaHasta(df.parse(fechaHasta));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fecha inválida.");
            return "redirect:/ocupacion/buscar";
        }

        try {
            gestionHabitacion.ocuparHabitacion(reservaId, req, dto);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ocupacion/buscar?numero_habitacion="
                    + numero_habitacion
                    + "&reservaId=" + reservaId
                    + "&fechaDesde=" + fechaDesde
                    + "&fechaHasta=" + fechaHasta;
        }

        return "redirect:/ocupacion/resumen?numero_habitacion="
                + numero_habitacion
                + "&reservaId=" + reservaId
                + "&fechaDesde=" + fechaDesde
                + "&fechaHasta=" + fechaHasta;
    }

    // 4) RESUMEN FINAL
    @GetMapping("/resumen")
    public String resumen(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        List<PersonaFisica> ocupantes
                = gestionHabitacion.obtenerOcupantesAsignados(reservaId, numero_habitacion);

        model.addAttribute("ocupantes", ocupantes);
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/resumen";
    }
}

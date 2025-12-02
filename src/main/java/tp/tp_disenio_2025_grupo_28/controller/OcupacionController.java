package tp.tp_disenio_2025_grupo_28.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionHuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
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

    //  1) PANTALLA DE BÚSQUEDA DE HUÉSPEDES
    @GetMapping("/buscar")
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
    }

    // 2) PROCESA LA BÚSQUEDA DE HUÉSPEDES
    @PostMapping("/buscar")
    public String procesarBusqueda(
            String nombre,
            String apellido,
            String tipoDocumento,
            String numeroDocumento,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        TipoDocumento tipoDocEnum = null;
        if (tipoDocumento != null && !tipoDocumento.isBlank()) {
            tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
        }

        List<PersonaFisica> resultados = gestionHuesped.buscarHuespedFinal(apellido, nombre, tipoDocEnum, numeroDocumento);

        model.addAttribute("resultados", resultados);
        model.addAttribute("tiposDocumento", TipoDocumento.values());

        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/buscar";
    }

    // 3) SELECCIONAR RESPONSABLE Y ACOMPAÑANTES
    @PostMapping("/seleccionar")
    public String seleccionarOcupantes(
            @RequestParam List<Integer> seleccionados,
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            RedirectAttributes redirectAttributes) {

        if (seleccionados == null || seleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un huésped.");
            return "redirect:/ocupacion/buscar?numero_habitacion=" + numero_habitacion
                    + "&reservaId=" + reservaId
                    + "&fechaDesde=" + fechaDesde
                    + "&fechaHasta=" + fechaHasta;
        }

        OcupacionHuespedDTO dto = new OcupacionHuespedDTO();
        dto.setIdHuesped(seleccionados.get(0));
        dto.setIdAcompanantes(seleccionados.subList(1, seleccionados.size()));

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
            return "redirect:/ocupacion/buscar";
        }

        return "redirect:/ocupacion/resumen?numero_habitacion=" + numero_habitacion
                + "&reservaId=" + reservaId
                + "&fechaDesde=" + fechaDesde
                + "&fechaHasta=" + fechaHasta;
    }

    // ➤ 4) RESUMEN FINAL
    @GetMapping("/resumen")
    public String resumen(
            @RequestParam Integer numero_habitacion,
            @RequestParam Integer reservaId,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            Model model) {

        List<PersonaFisica> ocupantes = gestionHabitacion.obtenerOcupantesAsignados(reservaId, numero_habitacion);

        model.addAttribute("ocupantes", ocupantes);
        model.addAttribute("numero_habitacion", numero_habitacion);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "ocuparHabitacion/resumen";
    }
}

package tp.tp_disenio_2025_grupo_28.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionHuespedDTO;
import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoDocumento;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;

@Controller
@RequestMapping("/ocupacion")
public class OcupacionController {

    @Autowired
    private GestionHabitacion gestionHabitacion;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/buscar")
    public String mostrarBuscar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "apellido", required = false) String apellido,
            @RequestParam(name = "tipo_documento", required = false) String tipoDocumento,
            @RequestParam(name = "numero_documento", required = false) String numeroDocumento,
            @RequestParam(name = "reservaId", required = false) Integer reservaId,
            @RequestParam(name = "numero_habitacion", required = false) Integer numeroHabitacion,
            @RequestParam(name = "fechaDesde", required = false) String fechaDesdeStr,
            @RequestParam(name = "fechaHasta", required = false) String fechaHastaStr,
            Model model) {

        // Mantener lo ingresado en el formulario
        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("tipo_documento", tipoDocumento);
        model.addAttribute("numero_documento", numeroDocumento);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("numero_habitacion", numeroHabitacion);
        model.addAttribute("fechaDesde", fechaDesdeStr);
        model.addAttribute("fechaHasta", fechaHastaStr);

        // Ejecutar la búsqueda solo si hay filtros
        boolean hayFiltros = (nombre != null && !nombre.isBlank())
                || (apellido != null && !apellido.isBlank())
                || (tipoDocumento != null && !tipoDocumento.isBlank())
                || (numeroDocumento != null && !numeroDocumento.isBlank());
        TipoDocumento tipoDocEnum = null;

        if (hayFiltros) {
            if (tipoDocumento != null && !tipoDocumento.isBlank()) {
                try {
                    tipoDocEnum = TipoDocumento.valueOf(tipoDocumento);
                } catch (Exception e) {
                    model.addAttribute("error", "Tipo de documento inválido.");
                }
            }
            List<PersonaFisica> resultados
                    = gestionHabitacion.buscarOcupantes(nombre, apellido, tipoDocEnum,
                            numeroDocumento != null && !numeroDocumento.isBlank()
                            ? Integer.parseInt(numeroDocumento) : null);
            model.addAttribute("resultados", resultados);
        }

        return "ocuparHabitacion/buscar";
    }

    @PostMapping("/seleccionar")
    public String seleccionarOcupantes(
            @RequestParam(name = "seleccionados", required = false) List<Integer> seleccionados,
            @RequestParam(name = "reservaId") Integer reservaId,
            @RequestParam(name = "numero_habitacion") Integer numeroHabitacion,
            @RequestParam(name = "fechaDesde") String fechaDesdeStr,
            @RequestParam(name = "fechaHasta") String fechaHastaStr,
            Model model) {

        Date fechaDesde, fechaHasta;
        try {
            fechaDesde = df.parse(fechaDesdeStr);
            fechaHasta = df.parse(fechaHastaStr);
        } catch (ParseException e) {
            model.addAttribute("error", "Formato de fecha inválido (use yyyy-MM-dd)");
            return "ocuparHabitacion/buscar";
        }

        // Validación mínima
        if (seleccionados == null || seleccionados.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un ocupante.");
            return "redirect:/ocupacion/buscar?reservaId=" + reservaId
                    + "&numero_habitacion=" + numeroHabitacion
                    + "&fechaDesde=" + fechaDesdeStr
                    + "&fechaHasta=" + fechaHastaStr;
        }
        OcupacionHuespedDTO huespedesDto = new OcupacionHuespedDTO();
        huespedesDto.setIdHuesped(seleccionados.get(0));

        if (seleccionados.size() > 1) {
            huespedesDto.setIdAcompanantes(seleccionados.subList(1, seleccionados.size()));
        } else {
            huespedesDto.setIdAcompanantes(Collections.emptyList());
        }

        // Período que se ocupará
        OcupacionRequestDTO request = new OcupacionRequestDTO();
        request.setNumeroHabitacion(numeroHabitacion);
        request.setFechaDesde(fechaDesde);
        request.setFechaHasta(fechaHasta);
        // Ejecutar la ocupación

        try {
            gestionHabitacion.ocuparHabitacion(reservaId, request, huespedesDto);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "ocuparHabitacion/buscar";
        }

        // Redirigir al resumen
        return "redirect:/ocupacion/resumen?reservaId=" + reservaId
                + "&numero_habitacion=" + numeroHabitacion
                + "&fechaDesde=" + fechaDesdeStr
                + "&fechaHasta=" + fechaHastaStr;
    }

    // GET → RESUMEN DE OCUPACIÓN
    @GetMapping("/resumen")
    public String resumen(
            @RequestParam(name = "reservaId") Integer reservaId,
            @RequestParam(name = "numero_habitacion") Integer numeroHabitacion,
            @RequestParam(name = "fechaDesde") String fechaDesdeStr,
            @RequestParam(name = "fechaHasta") String fechaHastaStr,
            Model model) {

        Date fechaDesde, fechaHasta;
        try {
            fechaDesde = df.parse(fechaDesdeStr);
            fechaHasta = df.parse(fechaHastaStr);
        } catch (ParseException e) {
            model.addAttribute("error", "Formato de fecha inválido (use yyyy-MM-dd)");
            return "ocuparHabitacion/resumen";
        }

        //Recuperamos datos desde GestionHabitacion
        Object responsable = gestionHabitacion.obtenerResponsableReserva(reservaId);
        List<PersonaFisica> ocupantes
                = gestionHabitacion.obtenerOcupantesAsignados(reservaId, numeroHabitacion);

        Integer lugares = gestionHabitacion.calcularLugaresDisponibles(numeroHabitacion, reservaId);

        // Cargar datos a la vista
        model.addAttribute("responsable", responsable);
        model.addAttribute("ocupantes", ocupantes);
        model.addAttribute("lugares_disponibles", lugares == null ? 0 : lugares);
        model.addAttribute("reservaId", reservaId);
        model.addAttribute("numero_habitacion", numeroHabitacion);
        model.addAttribute("fechaDesde", fechaDesdeStr);
        model.addAttribute("fechaHasta", fechaHastaStr);

        return "ocuparHabitacion/resumen";
    }

}

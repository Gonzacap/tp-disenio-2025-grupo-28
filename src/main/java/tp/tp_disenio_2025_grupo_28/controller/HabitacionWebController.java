package tp.tp_disenio_2025_grupo_28.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.dto.*;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;

@Controller
@RequestMapping("/habitacion")
public class HabitacionWebController {

    private final GestionHabitacion gestionHabitacion;

    @Autowired
    public HabitacionWebController(GestionHabitacion gestionHabitacion) {
        this.gestionHabitacion = gestionHabitacion;
    }

    @GetMapping
    public String mostrarPagina(
            @RequestParam(value = "modo", required = false, defaultValue = "reservar") String modo,
            Model model
    ) {
        model.addAttribute("modo", modo);
        model.addAttribute("habitacionesPorTipo", gestionHabitacion.obtenerHabitacionPorTipoMockup());
        model.addAttribute("dias", List.of());
        model.addAttribute("grilla", List.of());
        model.addAttribute("fechaDesde", null);
        model.addAttribute("fechaHasta", null);

        return "habitacion/GestionHabitacion";
    }

    //CU05 - BUSCAR ESTADOS
    @PostMapping("/validar-fecha")
    public String buscarDisponibilidad(
            @RequestParam(value = "fechaDesde", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
            @RequestParam(value = "fechaHasta", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta,
            @RequestParam(value = "modo", required = false, defaultValue = "reservar") String modo,
            Model model,
            RedirectAttributes redirect
    ) {

        try {
            gestionHabitacion.validarFecha(desde, hasta);
            List<Habitacion> habitaciones = gestionHabitacion.obtenerHabitaciones();
            List<Map<String, Object>> porTipo = gestionHabitacion.obtenerHabitacionPorTipo(habitaciones);
            List<Date> dias = gestionHabitacion.generarDiasEntre(desde, hasta);
            List<Map<String, Object>> grilla = gestionHabitacion.grilla(porTipo, habitaciones, dias, desde, hasta);
            model.addAttribute("modo", modo);
            model.addAttribute("habitacionesPorTipo", porTipo);
            model.addAttribute("grilla", grilla);
            model.addAttribute("dias", dias);
            model.addAttribute("fechaDesde", desde);
            model.addAttribute("fechaHasta", hasta);

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }

    //CU04 - PASO 3 --> IR AL FORMULARIO
    @PostMapping("/reservar")
    public String pasarAFormularioReserva(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam String habitaciones,
            Model model,
            RedirectAttributes redirect, HttpSession session
    ) {
        if (habitaciones == null || habitaciones.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion";
        }

        List<Integer> seleccionadas = Arrays.stream(habitaciones.split(",")).map(Integer::parseInt).toList();

        if (seleccionadas.isEmpty()) {
            redirect.addFlashAttribute("errorMessage",
                    "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion";
        }

        List<Integer> noDisponibles = gestionHabitacion.habitacionesNoDisponibles(seleccionadas, fechaDesde, fechaHasta);

        if (!noDisponibles.isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Habitaciones no disponibles: " + noDisponibles);
            return "redirect:/habitacion";
        }
    List<ReservaHabitacionDTO> habitacionesDTO =
            seleccionadas.stream()
                    .map(nro -> {
                        ReservaHabitacionDTO rh = new ReservaHabitacionDTO(nro, fechaDesde, fechaHasta);
                        
                        return rh;
                    })
                    .toList();

    Habitacion habitacionBase =
            gestionHabitacion.buscarPorNumero(seleccionadas.get(0));

    ReservaRequestDTO dto = new ReservaRequestDTO();
    dto.setHabitaciones(habitacionesDTO);
    dto.setTipoHabitacion(habitacionBase.getTipo());
    dto.setFechaDesde(fechaDesde);
    dto.setFechaHasta(fechaHasta);

    session.setAttribute("reservaDTO", dto);



        return "redirect:/reserva/nueva";
    }

}


/* 
    @PostMapping("/ocupar")
    public String ocuparDesdeGrilla(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam("habitaciones") String habitacionesCSV,
            RedirectAttributes redirectAttributes) {

        // 1) Parsear CSV de habitaciones
        List<Integer> habitacionesSel = new ArrayList<>();
        if (habitacionesCSV != null && !habitacionesCSV.isEmpty()) {
            for (String h : habitacionesCSV.split(",")) {
                try {
                    habitacionesSel.add(Integer.parseInt(h.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // 2) Validar selección
        if (habitacionesSel.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación para ocupar.");
            return "redirect:/habitacion";
        }

        Integer numeroHab = habitacionesSel.get(0);

        // 3) Validar que la habitación exista
        if (!gestionHabitacion.existeHabitacion(numeroHab)) {
            redirectAttributes.addFlashAttribute("errorMessage", "La habitación seleccionada no existe.");
            return "redirect:/habitacion";
        }

        // 4) Buscar reserva existente (si hay)
        Integer reservaId = gestionHabitacion.buscarReservaParaOcupar(numeroHab, fechaDesde, fechaHasta, true);

        // 5) Formatear fechas para URL
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // 6) Redirigir a CU15 con los parámetros necesarios
        String url = "redirect:/ocupacion/buscar?"
                + "numero_habitacion=" + numeroHab
                + "&fechaDesde=" + df.format(fechaDesde)
                + "&fechaHasta=" + df.format(fechaHasta)
                + "&reservaId=" + (reservaId != null ? reservaId : 0); // 0 = ocupa sin reserva

        return url;
    }
} */

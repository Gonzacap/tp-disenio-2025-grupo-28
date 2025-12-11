package tp.tp_disenio_2025_grupo_28.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.dto.ReservaHabitacionDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacionOld;

@Controller
@RequestMapping("/habitacion")
public class HabitacionWebController {

    private final GestionHabitacion gestionHabitacion;
    private final GestionHabitacionOld gestionHabitacionOld;

    @Autowired
    public HabitacionWebController(GestionHabitacion gestionHabitacion, GestionHabitacionOld gestionHabitacionOld) {
        this.gestionHabitacion = gestionHabitacion;
        this.gestionHabitacionOld = gestionHabitacionOld;
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
            @RequestParam(value = "modo") String modo,
            Model model,
            RedirectAttributes redirect
    ) {

        try {
            gestionHabitacion.validarFecha(desde, hasta);
            List<Habitacion> habitaciones = gestionHabitacion.obtenerHabitaciones();
            List<Map<String, Object>> porTipo = gestionHabitacion.obtenerHabitacionPorTipo(habitaciones);
            List<Date> dias = gestionHabitacion.generarDiasEntre(desde, hasta);
            List<Map<String, Object>> grilla = gestionHabitacion.grilla(porTipo, habitaciones, dias, desde, hasta);

            if (modo.equals("ocupar") && porTipo.isEmpty()) {
                model.addAttribute("sinHabitaciones", true);
            }

            model.addAttribute("modo", modo);
            model.addAttribute("habitacionesPorTipo", porTipo);
            model.addAttribute("grilla", grilla);
            model.addAttribute("dias", dias);
            model.addAttribute("fechaDesde", desde);
            model.addAttribute("fechaHasta", hasta);

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion?modo=" + modo;
        }
    }

    //CU04 - PASO 3 --> IR AL FORMULARIO
    @PostMapping("/reservar")
    public String pasarAFormularioReserva(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam String habitaciones,
            @RequestParam(name = "reservasInput", required = true) String reservasJson,
            Model model,
            RedirectAttributes redirect, HttpSession session
    ) {
        if (habitaciones == null || habitaciones.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion?modo=reservar";
        }
        if (reservasJson == null || reservasJson.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Faltan las fechas de selección por habitación.");
            return "redirect:/habitacion?modo=reservar";
        }

        List<Integer> seleccionadas = Arrays.stream(habitaciones.split(",")).map(Integer::parseInt).toList();

        if (seleccionadas.isEmpty()) {
            redirect.addFlashAttribute("errorMessage",
                    "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion?modo=reservar";
        }

        List<Integer> noDisponibles = gestionHabitacion.habitacionesNoDisponibles(seleccionadas, fechaDesde, fechaHasta);

        if (!noDisponibles.isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Habitaciones no disponibles: " + noDisponibles);
            return "redirect:/habitacion?modo=reservar";
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> reservasMap;
        try {
            reservasMap = mapper.readValue(
                    reservasJson,
                    new TypeReference<Map<String, Map<String, String>>>() {
            }
            );
        } catch (IOException e) {
            redirect.addFlashAttribute("errorMessage", "Error procesando las fechas de selección (reservasInput).");
            return "redirect:/habitacion?modo=reservar";
        }
        List<ReservaHabitacionDTO> habitacionesDTO;
        try {
            habitacionesDTO = seleccionadas.stream()
                    .map(num -> {
                        Map<String, String> r = reservasMap.get(num.toString());
                        if (r == null || r.get("desde") == null || r.get("hasta") == null) {
                            throw new IllegalArgumentException("Faltan fechas para la habitación " + num);
                        }

                        Date fDesde = java.sql.Date.valueOf(r.get("desde"));
                        Date fHasta = java.sql.Date.valueOf(r.get("hasta"));

                        return new ReservaHabitacionDTO(num, fDesde, fHasta);
                    })
                    .toList();
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/habitacion?modo=reservar";
        }
        if (habitacionesDTO.size() > 1) {
            Date baseDesde = habitacionesDTO.get(0).getFechaDesde();
            Date baseHasta = habitacionesDTO.get(0).getFechaHasta();

            boolean mismatch = habitacionesDTO.stream()
                    .anyMatch(h -> !h.getFechaDesde().equals(baseDesde) || !h.getFechaHasta().equals(baseHasta));

            if (mismatch) {
                redirect.addFlashAttribute("errorMessage",
                        "Todas las habitaciones seleccionadas deben tener el mismo rango de fechas.");
                return "redirect:/habitacion?modo=reservar";
            }

            // si son iguales, también actualizamos fechaDesde/fechaHasta globales (consistencia)
            fechaDesde = baseDesde;
            fechaHasta = baseHasta;
        }
        for (ReservaHabitacionDTO rh : habitacionesDTO) {
            boolean disponible = gestionHabitacion.estaDisponible(
                    rh.getNumeroHabitacion(),
                    rh.getFechaDesde(),
                    rh.getFechaHasta()
            );
            if (!disponible) {
                redirect.addFlashAttribute("errorMessage",
                        "La habitación " + rh.getNumeroHabitacion() + " no está disponible en el período seleccionado.");
                return "redirect:/habitacion?modo=reservar";
            }
        }

        Habitacion habitacionBase = gestionHabitacion.buscarPorNumero(seleccionadas.get(0));

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setHabitaciones(habitacionesDTO);
        dto.setTipoHabitacion(habitacionBase.getTipo());
        dto.setFechaDesde(fechaDesde);
        dto.setFechaHasta(fechaHasta);

        session.setAttribute("reservaDTO", dto);

        return "redirect:/reserva/nueva";
    }

    @ModelAttribute("fechaDesde")
    public String fechaDesde() {
        return null;
    }

    @ModelAttribute("fechaHasta")
    public String fechaHasta() {
        return null;
    }

    /**
     * Old Method
     */
    @GetMapping("/old")
    public String mostrarPaginaOld(
            Model model,
            @ModelAttribute("fechaDesde") Date fechaDesde,
            @ModelAttribute("fechaHasta") Date fechaHasta
    ) {

        model.addAttribute("habitacionesPorTipo", gestionHabitacionOld.obtenerHabitacionPorTipoMockup());
        model.addAttribute("dias", new ArrayList<>());
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "habitacion/GestionHabitacionOld";
    }

    /**
     * Old Method
     */
    @PostMapping("/validar-fecha-old")
    public String mostrarEstadoHabitaciones(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        try {

            gestionHabitacionOld.validarFecha(fechaDesde, fechaHasta);

            List<Habitacion> habitaciones = gestionHabitacionOld.obtenerHabitaciones();
            List<Map<String, Object>> habitacionesPorTipo = gestionHabitacionOld.obtenerHabitacionPorTipo(habitaciones);

            List<Date> dias = gestionHabitacionOld.generarDiasEntre(fechaDesde, fechaHasta);

            List<Map<String, Object>> grilla = gestionHabitacionOld.grilla(
                    habitacionesPorTipo,
                    habitaciones,
                    dias
            );

            model.addAttribute("grilla", grilla);
            model.addAttribute("dias", dias);
            model.addAttribute("habitacionesPorTipo", habitacionesPorTipo);

            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

            model.addAttribute("habitacionesPorTipo", habitacionesPorTipo != null ? habitacionesPorTipo : List.of());
            model.addAttribute("grilla", grilla != null ? grilla : List.of());

            return "habitacion/GestionHabitacionOld";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }

    // CU15 - OCUPAR HABITACIÓN (PASO 3 → IR A FORMULARIO DE OCUPACIÓN)
    @PostMapping("/ocupar")
    public String pasarAFormularioOcupar(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam("numeroHabitacion") Integer numeroHabitacion,
            RedirectAttributes redirect,
            HttpSession session
    ) {

        // 1) VALIDACIONES BÁSICAS
        if (numeroHabitacion == null) {
            redirect.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion?modo=ocupar";
        }
        // 2) VERIFICAR SI ALGUNA HABITACIÓN TIENE RESERVA EN EL RANGO
        boolean tieneReserva = gestionHabitacion.habitacionTieneReserva(numeroHabitacion, fechaDesde, fechaHasta);

        if (tieneReserva && session.getAttribute("confirmarOcuparIgual") == null) {

            session.setAttribute("numeroHabitacionPendiente", numeroHabitacion);
            session.setAttribute("fechaDesdePendiente", fechaDesde);
            session.setAttribute("fechaHastaPendiente", fechaHasta);

            redirect.addFlashAttribute("modalReservaDetectada",
                    "La habitación " + numeroHabitacion + " tiene días reservados en ese rango.");

            return "redirect:/habitacion?modo=ocupar";
        }

        // 3) SI LLEGÓ ACÁ, PUEDE OCUPAR → limpiar flag del modal
        session.removeAttribute("confirmarOcuparIgual");

        // 4) IR A FORMULARIO /OCUPACION/CARGAR (EX CU15 PASO 3)
        redirect.addAttribute("numeroHabitacion", numeroHabitacion);
        redirect.addAttribute("fechaDesde", fechaDesde);
        redirect.addAttribute("fechaHasta", fechaHasta);

        return "redirect:/ocupacion/cargar";
    }
//setear un flag para que NO vuelva a saltar el modal:

    @PostMapping("/ocupar/confirmar")
    public String confirmarOcuparIgual(HttpSession session, Model model) {
        session.setAttribute("confirmarOcuparIgual", true);
        model.addAttribute("modalPresioneTecla", true);

        return "redirect:/habitacion?modo=ocupar";
    }

}

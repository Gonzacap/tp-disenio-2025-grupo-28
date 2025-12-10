package tp.tp_disenio_2025_grupo_28.controller;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.dto.ReservaHabitacionDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;

@Controller
@RequestMapping("/habitacion")
public class HabitacionWebController {

    private final GestionHabitacion gestionHabitacion;
    private final GestionHabitacionOld gestionHabitacionOld;

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
            @RequestParam(name = "reservasInput", required = true) String reservasJson,
            Model model,
            RedirectAttributes redirect, HttpSession session
    ) {
        if (habitaciones == null || habitaciones.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación disponible.");
            return "redirect:/habitacion";
        }
        if (reservasJson == null || reservasJson.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Faltan las fechas de selección por habitación.");
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
            return "redirect:/habitacion";
        }

        /*  List<ReservaHabitacionDTO> habitacionesDTO
                = seleccionadas.stream()
                        .map(nro -> {
                            ReservaHabitacionDTO rh = new ReservaHabitacionDTO(nro, fechaDesde, fechaHasta);

                            return rh;
                        })
                        .toList();*/
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
            return "redirect:/habitacion";
        }
        if (habitacionesDTO.size() > 1) {
            Date baseDesde = habitacionesDTO.get(0).getFechaDesde();
            Date baseHasta = habitacionesDTO.get(0).getFechaHasta();

            boolean mismatch = habitacionesDTO.stream()
                    .anyMatch(h -> !h.getFechaDesde().equals(baseDesde) || !h.getFechaHasta().equals(baseHasta));

            if (mismatch) {
                redirect.addFlashAttribute("errorMessage",
                        "Todas las habitaciones seleccionadas deben tener el mismo rango de fechas.");
                return "redirect:/habitacion";
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
                return "redirect:/habitacion";
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

    /**
     * Old Method
     */
    @GetMapping()
    public String mostrarPaginaOld(Model model) {

        model.addAttribute("habitacionesPorTipo", gestionHabitacionOld.obtenerHabitacionPorTipoMockup());
        model.addAttribute("dias", new ArrayList<>());

        return "habitacion/GestionHabitacion";
    }

    /**
     * Old Method
     */
    @PostMapping("/validar-fecha-old")
    public String mostrarEstadoHabitaciones(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
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

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
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

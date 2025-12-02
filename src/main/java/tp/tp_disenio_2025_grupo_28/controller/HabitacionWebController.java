package tp.tp_disenio_2025_grupo_28.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;

@Controller
@RequestMapping("/habitacion")
public class HabitacionWebController {

    private final GestionHabitacion gestionHabitacion;

    @Autowired
    public HabitacionWebController(GestionHabitacion gestionHabitacion) {
        this.gestionHabitacion = gestionHabitacion;
    }

    @GetMapping()
    public String mostrarPagina(Model model) {
        if (!model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", null);
        }

        if (!model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", null);
        }

        // 1. Cargamos tipos de habitación (encabezados de la tabla)
        model.addAttribute("habitacionesPorTipo", gestionHabitacion.obtenerHabitacionPorTipoMockup());

        // 2. Inicializamos las listas y variables que la vista necesita
        // Esto evita errores si GestionHabitacion.html intenta iterar sobre 'grilla' o 'dias'.
        model.addAttribute("dias", new ArrayList<>());
        model.addAttribute("grilla", new ArrayList<>());
        model.addAttribute("fechaDesde", null);
        model.addAttribute("fechaHasta", null);

        // 3. Devolvemos la vista con la RUTA CORREGIDA (usando la subcarpeta 'habitacion')
        return "habitacion/GestionHabitacion";
    }

    @PostMapping("/validar-fecha")
    public String solicitarEstadoHabitaciones(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            gestionHabitacion.validarFecha(fechaDesde, fechaHasta);

            List<Habitacion> habitaciones = gestionHabitacion.obtenerHabitaciones();
            List<Map<String, Object>> habitacionesPorTipo = gestionHabitacion.obtenerHabitacionPorTipo(habitaciones);
            List<Date> dias = gestionHabitacion.generarDiasEntre(fechaDesde, fechaHasta);
            List<Map<String, Object>> grilla = gestionHabitacion.grilla(habitacionesPorTipo, habitaciones, dias);

            model.addAttribute("grilla", grilla);
            model.addAttribute("dias", dias);
            model.addAttribute("habitacionesPorTipo", habitacionesPorTipo);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }

    // ESTE METODO RECIBE LOS DATOS DE LA GRILLA (CU05) Y MANDA AL FORMULARIO (CU04)
    // ESTE METODO RECIBE LOS DATOS DE LA GRILLA (CU05) Y MANDA AL FORMULARIO (CU04)
    @PostMapping("/reservar")
    public String reservarDesdeGrilla(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam("habitaciones") String habitacionesCSV,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1) PARSEAR CSV DE HABITACIONES
        List<Integer> habitacionesSel = new ArrayList<>();

        if (habitacionesCSV != null && !habitacionesCSV.isEmpty()) {
            for (String h : habitacionesCSV.split(",")) {
                try {
                    habitacionesSel.add(Integer.parseInt(h.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (habitacionesSel.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe seleccionar al menos una habitación.");
            return "redirect:/habitacion";
        }

        // 2) VALIDAR HABITACIONES EXISTENTES
        List<Integer> inexistentes = habitacionesSel.stream().filter(n -> !gestionHabitacion.existeHabitacion(n)).toList();

        if (!inexistentes.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Las siguientes habitaciones no existen: " + inexistentes
            );
            return "redirect:/habitacion";
        }
        // 3) VALIDAR DISPONIBILIDAD REAL

        List<Integer> noDisponibles
                = gestionHabitacion.habitacionesNoDisponibles(
                        habitacionesSel, fechaDesde, fechaHasta
                );

        if (!noDisponibles.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Las siguientes habitaciones no están disponibles en el rango seleccionado: "
                    + noDisponibles
            );
            return "redirect:/habitacion";
        }

        // 4) OBTENER TIPO DE LA PRIMER HABITACIÓN
        Habitacion hab = gestionHabitacion.buscarPorNumero(habitacionesSel.get(0));
        TipoHabitacion tipo = (hab != null) ? hab.getTipo() : null;

        // 5) CREAR DTO PARA FORMULARIO CU04
        ReservaRequestDTO dto = new ReservaRequestDTO(
                null, null, null, // Nombre / Apellido / Teléfono
                fechaDesde, fechaHasta,
                habitacionesSel,
                tipo
        );

        model.addAttribute("reservaRequestDTO", dto);

        return "reserva/nueva-reserva";
    }

   /*  @PostMapping("/ocupar")
    public String ocuparDesdeGrilla(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam("habitaciones") String habitacionesCSV,
            RedirectAttributes redirectAttributes) {

        List<Integer> habitacionesSel = new ArrayList<>();
        if (habitacionesCSV != null && !habitacionesCSV.isEmpty()) {
            for (String h : habitacionesCSV.split(",")) {
                try {
                    habitacionesSel.add(Integer.parseInt(h.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (habitacionesSel.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debe seleccionar una habitación para ocupar.");
            return "redirect:/habitacion";
        }
        //VALIDAMOS HABITACIONES INEXTISTENTES
        List<Integer> inexistentes = habitacionesSel.stream().filter(n -> !gestionHabitacion.existeHabitacion(n)).toList();

        if (!inexistentes.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Habitación inexistente: " + inexistentes);
            return "redirect:/habitacion";
        }

        Integer numeroHab = habitacionesSel.get(0);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return "redirect:/ocupacion/buscar?numero_habitacion=" + numeroHab + "&fechaDesde=" + df.format(fechaDesde) + "&fechaHasta=" + df.format(fechaHasta);
    }*/
@PostMapping("/ocupar")
public String ocuparDesdeGrilla(
        @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
        @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
        @RequestParam("habitaciones") String habitacionesCSV,
        RedirectAttributes redirectAttributes) {

    List<Integer> habitacionesSel = new ArrayList<>();
    if (habitacionesCSV != null && !habitacionesCSV.isEmpty()) {
        for (String h : habitacionesCSV.split(",")) {
            try {
                habitacionesSel.add(Integer.parseInt(h.trim()));
            } catch (NumberFormatException ignored) {}
        }
    }

    if (habitacionesSel.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Debe seleccionar una habitación para ocupar.");
        return "redirect:/habitacion";
    }

    Integer numeroHab = habitacionesSel.get(0);

    // VALIDACIÓN
    if (!gestionHabitacion.existeHabitacion(numeroHab)) {
        redirectAttributes.addFlashAttribute("errorMessage", "La habitación no existe.");
        return "redirect:/habitacion";
    }

    // 1) BUSCAR RESERVA QUE CORRESPONDA A ESA HABITACIÓN
    Integer reservaId = gestionHabitacion.buscarReservaParaOcupar(
            numeroHab, fechaDesde, fechaHasta
    );

    // FORMATEO DE FECHAS 
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    //2) REDIRIGIR A CU15 (agregando reservaId si existe)
    String url = "redirect:/ocupacion/buscar?"
            + "numero_habitacion=" + numeroHab
            + "&fechaDesde=" + df.format(fechaDesde)
            + "&fechaHasta=" + df.format(fechaHasta);

    if (reservaId != null) {
        url += "&reservaId=" + reservaId;
    } else {
        url += "&reservaId=0"; // ocupa sin reserva
    }

    return url;
}



}
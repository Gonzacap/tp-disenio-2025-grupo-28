package tp.tp_disenio_2025_grupo_28.controller;

import java.time.ZoneId;
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
    @PostMapping("/reservar")
    public String reservarDesdeGrilla(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam("habitaciones") String habitacionesCSV,
            Model model) {

        List<Integer> habitacionesSel = new ArrayList<>();

        if (habitacionesCSV != null && !habitacionesCSV.isEmpty()) {
            for (String h : habitacionesCSV.split(",")) {
                try {
                    habitacionesSel.add(Integer.parseInt(h.trim()));
                } catch (NumberFormatException e) {
                    // ignorar errores de parseo
                }
            }
        }

        if (habitacionesSel.isEmpty()) {
            model.addAttribute("errorMessage", "Debe seleccionar al menos una habitación.");
            return "redirect:/habitacion";
        }

        // Buscamos el tipo de habitación para mostrar en el resumen
        TipoHabitacion tipo = null;
        Habitacion h = gestionHabitacion.buscarPorNumero(habitacionesSel.get(0));
        if (h != null) {
            tipo = h.getTipo();
        }

        // Creamos el DTO con los datos que ya tenemos (Fechas y Habitaciones)
        // El resto (Nombre, Apellido) van null para que el usuario los llene
        ReservaRequestDTO dto = new ReservaRequestDTO(
                null, null, null, // Nombre, Apellido, Telefono vacios
                fechaDesde.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                fechaHasta.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                habitacionesSel,
                tipo
        );

        model.addAttribute("reservaRequestDTO", dto);

        // Retornamos la vista del formulario CU04
        return "reserva/nueva-reserva";
    }
}

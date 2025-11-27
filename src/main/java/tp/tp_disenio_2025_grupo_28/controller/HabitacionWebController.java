package tp.tp_disenio_2025_grupo_28.controller;

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

import tp.tp_disenio_2025_grupo_28.model.Habitacion;
import tp.tp_disenio_2025_grupo_28.service.GestionHabitacion;
// import tp.tp_disenio_2025_grupo_28.mapper.*;

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

        model.addAttribute("habitacionesPorTipo", gestionHabitacion.obtenerHabitacionPorTipoMockup());
        model.addAttribute("dias", new ArrayList<>());

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

            List<Map<String, Object>> grilla = gestionHabitacion.grilla(
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

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.*;
// import org.springframework.dao.DuplicateKeyException;
// import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

// import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.service.*;
import tp.tp_disenio_2025_grupo_28.dto.*;
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

        model.addAttribute("tiposHabitacion", gestionHabitacion.obtenerTiposHabitacion());
        model.addAttribute("habitaciones", new ArrayList<>());
        model.addAttribute("dias", new ArrayList<>());

        return "habitacion/GestionHabitacion";
    }

    @PostMapping("/validar-fecha")
    public String mostrarEstadoHabitaciones(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        try {

            gestionHabitacion.validarFecha(fechaDesde, fechaHasta);

            List<HabitacionDTO> habitaciones = gestionHabitacion.obtenerHabitacionesOrdenadas();

            List<ReservaDTO> reservas = gestionHabitacion.obtenerReservasEntre(fechaDesde, fechaHasta);

            List<Date> dias = gestionHabitacion.generarDiasEntre(fechaDesde, fechaHasta);

            List<Map<String, Object>> diasConEstados = gestionHabitacion.construirGrillaEstados(habitaciones, reservas, dias);

            model.addAttribute("tiposHabitacion", gestionHabitacion.obtenerTiposHabitacion());
            model.addAttribute("habitaciones", habitaciones);
            model.addAttribute("dias", diasConEstados);

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }
}

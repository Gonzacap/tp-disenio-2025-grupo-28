package tp.tp_disenio_2025_grupo_28.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tp.tp_disenio_2025_grupo_28.dto.HabitacionDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaDTO;
import tp.tp_disenio_2025_grupo_28.model.enums.TipoHabitacion;
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

            Map<TipoHabitacion, List<HabitacionDTO>> habitacionesPorTipo
                    = habitaciones.stream().collect(Collectors.groupingBy(HabitacionDTO::getTipo));

            List<ReservaDTO> reservas = gestionHabitacion.obtenerReservasEntre(fechaDesde, fechaHasta);

            List<Date> dias = gestionHabitacion.generarDiasEntre(fechaDesde, fechaHasta);

            List<Map<String, Object>> diasConEstados = gestionHabitacion.construirGrillaEstados(habitaciones, reservas, dias);

            model.addAttribute("tiposHabitacion", gestionHabitacion.obtenerTiposHabitacion());
            // model.addAttribute("habitaciones", habitaciones);
            model.addAttribute("habitacionesPorTipo", habitacionesPorTipo);
            model.addAttribute("dias", diasConEstados);

            return "habitacion/GestionHabitacion";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }
}

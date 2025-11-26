package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.service.*;
import tp.tp_disenio_2025_grupo_28.dto.*;
import tp.tp_disenio_2025_grupo_28.mapper.*;

@Controller
@RequestMapping("/habitacion")
public class HabitacionWebController {

    private final GestionHabitacion gestionHabitacion;

    @Autowired
    public HabitacionWebController(GestionHabitacion gestionHabitacion) {
        this.gestionHabitacion = gestionHabitacion;
    }


    @PostMapping("/validar-fecha")
    public String mostrarEstadoHabitaciones(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            List<HabitacionEstadoDTO> grilla =
                    gestionHabitacion.mostrarEstadoHabitaciones(fechaDesde, fechaHasta);

            model.addAttribute("grilla", grilla);
            model.addAttribute("fechaDesde", fechaDesde);
            model.addAttribute("fechaHasta", fechaHasta);

            return "habitacion/estado-habitaciones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/habitacion";
        }
    }


    @GetMapping()
    public String main() {
        return "habitacion/index";
    }
}

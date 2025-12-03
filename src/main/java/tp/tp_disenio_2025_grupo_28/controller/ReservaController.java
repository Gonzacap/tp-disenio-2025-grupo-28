package tp.tp_disenio_2025_grupo_28.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Usuario;
import tp.tp_disenio_2025_grupo_28.repository.HabitacionRepository;
import tp.tp_disenio_2025_grupo_28.service.ReservaService;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private HabitacionRepository habitacionRepo;

    // Paso 1 y 2 del CU: mostrar disponibilidad
    @GetMapping("/nueva")

    public String mostrarFormulario(
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            Model model) {
        ReservaRequestDTO dto = new ReservaRequestDTO(null, null, null, fechaDesde, fechaHasta, null, null);
        model.addAttribute("reservaRequestDTO", dto);

        return "reserva/nueva-reserva";

    }

    // Paso 8-10 del CU: recibir datos y registrar la reserva
    @PostMapping("/crear")
    public String crearReserva(@ModelAttribute ReservaRequestDTO dto, HttpSession session, Model model) {
        // Usuario usuario = (Usuario) session.getAttribute("usuario");
        List<String> errores = new ArrayList<>();

        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            errores.add("Debe ingresar el Apellido.");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            errores.add("Debe ingresar el Nombre.");
        }

        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            errores.add("Debe ingresar el Teléfono.");
        }
        // Si hay errores → volver al formulario
        /*    if (!errores.isEmpty()) {
            model.addAttribute("errores", errores);
            model.addAttribute("dto", dto); // Mantiene datos cargados
            return "reserva/nueva-reserva"; // Vuelve al punto 8 del CU
        }*/
        if (!errores.isEmpty()) {
            model.addAttribute("errorMessage", errores.get(0)); // muestra solo 1 error
            model.addAttribute("reservaRequestDTO", dto);
            return "reserva/nueva-reserva";
        }

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            reservaService.reservar(dto, usuario);

            model.addAttribute("titulo", "Reserva exitosa");
            model.addAttribute("mensaje", "La reserva fue creada correctamente.");
            model.addAttribute("redirect", "/habitacion");

            return "emergentes/exito";
        } catch (RuntimeException e) {
            model.addAttribute("mensaje", e.getMessage());
            model.addAttribute("redirect", "/habitacion");
            return "emergentes/error";
        }

    }

}

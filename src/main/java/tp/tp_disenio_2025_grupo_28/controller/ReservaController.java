package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaResponseDTO;
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
    public String mostrarFormulario(Model model) {
        model.addAttribute("reservaRequestDTO", new ReservaRequestDTO(null, null, null, null, null, null));
        return "reserva/nueva-reserva";
    }

    // Paso 8-10 del CU: recibir datos y registrar la reserva
    @PostMapping("/crear")
    public String crearReserva(
            @ModelAttribute ReservaRequestDTO dto,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            // PANTALLA EMERGENTE
            model.addAttribute("mensaje", "Debe iniciar sesión antes de reservar.");
            return "error";
        }

        try {
            ReservaResponseDTO respuesta = reservaService.reservar(dto, usuario);

            model.addAttribute("mensaje",
                    "Reserva creada con ID: " + respuesta.getIdReserva());
            model.addAttribute("accionAceptar", "/");
            return "exito";

        } catch (RuntimeException ex) {

            model.addAttribute("mensaje", ex.getMessage());
            model.addAttribute("accionAceptar", "/reserva/nueva");
            return "error"; // Emergente genérica
        }
    }
}

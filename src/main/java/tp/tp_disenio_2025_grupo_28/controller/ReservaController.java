package tp.tp_disenio_2025_grupo_28.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import tp.tp_disenio_2025_grupo_28.dto.ReservaHabitacionDTO;
import tp.tp_disenio_2025_grupo_28.dto.ReservaRequestDTO;
import tp.tp_disenio_2025_grupo_28.model.Habitacion;
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
            Model model, HttpSession session) {
        //ReservaRequestDTO dto = new ReservaRequestDTO(null, null, null, fechaDesde, fechaHasta, null, null);
        ReservaRequestDTO dto = (ReservaRequestDTO) session.getAttribute("reservaDTO");
        if (dto == null) {
            // flujo inválido, vuelve a selección de habitaciones
            return "redirect:/habitacion";
        }
        List<Integer> nums = dto.getHabitaciones().stream()
                .map(ReservaHabitacionDTO::getNumeroHabitacion)
                .toList();

        Map<Integer, String> tipoMap = habitacionRepo
                .findAllByNumeroHabitacionIn(nums)
                .stream()
                .collect(Collectors.toMap(
                        Habitacion::getNumeroHabitacion,
                        h -> h.getTipo().getNombre()
                ));

        model.addAttribute("habitacionTipoMap", tipoMap);

        model.addAttribute("reservaRequestDTO", dto);

        return "reserva/nueva-reserva";

    }

    // pasos 8 a 10
    @PostMapping("/crear")
    public String crear(
            @ModelAttribute("reservaRequestDTO") ReservaRequestDTO dto,
            HttpSession session,
            Model model) {

        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            model.addAttribute("errorMessage", "Debe ingresar el Apellido");
            model.addAttribute("reservaRequestDTO", dto);
            model.addAttribute("focusField", "apellido");
            return "reserva/nueva-reserva";
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            model.addAttribute("errorMessage", "Debe ingresar el Nombre");
            model.addAttribute("reservaRequestDTO", dto);
            model.addAttribute("focusField", "nombre");
            return "reserva/nueva-reserva";
        }

        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            model.addAttribute("errorMessage", "Debe ingresar el Teléfono");
            model.addAttribute("reservaRequestDTO", dto);
            model.addAttribute("focusField", "telefono");
            return "reserva/nueva-reserva";
        }
        ReservaRequestDTO sessionDTO = (ReservaRequestDTO) session.getAttribute("reservaDTO");
        if (sessionDTO == null) {
            return "redirect:/habitacion";
        }

        // Copiar solo datos ingresados por el usuario
        sessionDTO.setApellido(dto.getApellido());
        sessionDTO.setNombre(dto.getNombre());
        sessionDTO.setTelefono(dto.getTelefono());

        if (sessionDTO.getHabitaciones() == null || sessionDTO.getHabitaciones().isEmpty()) {
            model.addAttribute("errorMessage", "No hay habitaciones seleccionadas para reservar");
            model.addAttribute("reservaRequestDTO", sessionDTO);
            return "reserva/nueva-reserva";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        reservaService.reservar(sessionDTO, usuario);
        // limpiar sesión
        session.removeAttribute("reservaDTO");

        return "redirect:/habitacion";
    }
}

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;
import tp.tp_disenio_2025_grupo_28.dto.HuespedDTO;
import tp.tp_disenio_2025_grupo_28.mapper.HuespedMapper;

@Controller
@RequestMapping("/huesped")
public class HuespedTestWebController {

    private final GestionHuesped gestionHuesped;

    @Autowired
    public HuespedTestWebController(GestionHuesped gestionHuesped) {
        this.gestionHuesped = gestionHuesped;
    }

    /**
     * Handles the GET request to show the new Huesped form.
     * Ensures a Huesped object is available for form binding and checks for Flash attributes.
     */
    @GetMapping("/new")
    public String showNewHuespedForm(Model model) {
        // Use model.containsAttribute to avoid overwriting user input preserved by RedirectAttributes
        if (!model.containsAttribute("huesped")) {
            model.addAttribute("huesped", new Huesped());
        }

        // Agregar lista de tipos de documento
        List<String> tiposDocumento = gestionHuesped.listarTipoDocumento();
        model.addAttribute("tiposDocumento", tiposDocumento);

        return "huesped/form/Registro"; // Name of the Thymeleaf template
    }

    /**
     * Handles the POST request when the form is submitted.
     * Delegates saving to the service and handles exceptions.
     */
    @PostMapping("/new")
    public String saveHuesped(@ModelAttribute("huesped") Huesped huesped,
                              RedirectAttributes redirectAttributes) {
        try {
            HuespedDTO nuevoHuesped = gestionHuesped.registrarNuevoHuesped(huesped);

            String fullName = nuevoHuesped.getNombre() + " " + nuevoHuesped.getApellido();
            return "redirect:/huesped/success?name=" + fullName;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("huesped", huesped);
            return "redirect:/huesped/new";
        }
    }

    /**
     * Simple success page controller.
     */
    @GetMapping("/success")
    public String showSuccess(String name, Model model) {
        model.addAttribute("huespedName", name);
        return "huespedSuccess";
    }
}
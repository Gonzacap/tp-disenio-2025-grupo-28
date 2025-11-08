package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mensaje", "Hola desde Thymeleaf!");
        return "index"; // Busca templates/index.html
    }
}

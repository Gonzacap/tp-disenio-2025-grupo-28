package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import tp.tp_disenio_2025_grupo_28.model.Usuario;
import tp.tp_disenio_2025_grupo_28.service.GestionAutenticacion;

@Controller
public class LoginController {

    @Autowired
    private GestionAutenticacion authService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam Integer dni,
            @RequestParam String contrasena,
            HttpSession session,
            Model model) {

        Usuario u = authService.autenticarUsuario(dni, contrasena);

        if (u == null) {
            model.addAttribute("error", "DNI o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("usuario", u);

        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

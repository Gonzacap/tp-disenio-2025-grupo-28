package tp.tp_disenio_2025_grupo_28.controller;

import java.util.Enumeration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String index(HttpSession session) {

        // Validar que el usuario está logueado
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        // Limpiar sesions
        Enumeration<String> atributos = session.getAttributeNames();
        while (atributos.hasMoreElements()) {
            String nombre = atributos.nextElement();
            if (!"usuario".equals(nombre)) {
                session.removeAttribute(nombre);
            }
        }

        return "index";  // nombre de la plantilla index.html
    }

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "redirect:/index";
    }

}

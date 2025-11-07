package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test")
    public String probarConexion() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return "Conexión exitosa a la DB!";
            } else {
                return "Conexión fallida: resultado inesperado";
            }
        } catch (Exception e) {
            return "Error al conectar a la DB: " + e.getMessage();
        }
    }
}

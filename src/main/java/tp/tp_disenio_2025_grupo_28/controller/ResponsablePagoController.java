package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.web.bind.annotation.*;
import tp.tp_disenio_2025_grupo_28.model.ResponsablePago;
import tp.tp_disenio_2025_grupo_28.repository.ResponsablePagoRepository;
import java.util.List;

@RestController
@RequestMapping("/responsablepagos")
public class ResponsablePagoController {

    private final ResponsablePagoRepository repo;

    public ResponsablePagoController(ResponsablePagoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ResponsablePago> listarResponsablePagos() {
        return repo.findAll();
    }

    @PostMapping
    public ResponsablePago crearResponsablePago(@RequestBody ResponsablePago responsablePago) {
        return repo.save(responsablePago);
    }
}

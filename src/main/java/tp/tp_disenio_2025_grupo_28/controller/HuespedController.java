package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.web.bind.annotation.*;
import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import java.util.List;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {

    private final HuespedRepository repo;

    public HuespedController(HuespedRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Huesped> listarHuespedes() {
        return repo.findAll();
    }

    @PostMapping
    public Huesped crearHuesped(@RequestBody Huesped huesped) {
        return repo.save(huesped);
    }
}

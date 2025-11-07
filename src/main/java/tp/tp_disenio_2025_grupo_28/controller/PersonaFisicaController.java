package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.web.bind.annotation.*;
import tp.tp_disenio_2025_grupo_28.model.PersonaFisica;
import tp.tp_disenio_2025_grupo_28.repository.PersonaFisicaRepository;
import java.util.List;

@RestController
@RequestMapping("/personasfisicas")
public class PersonaFisicaController {

    private final PersonaFisicaRepository repo;

    public PersonaFisicaController(PersonaFisicaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<PersonaFisica> listarPersonasFisicas() {
        return repo.findAll();
    }

    @PostMapping
    public PersonaFisica crearPersonasFisicas(@RequestBody PersonaFisica personaFisica) {
        return repo.save(personaFisica);
    }
}

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;

import org.springframework.http.*;
import org.springframework.dao.DuplicateKeyException;


import tp.tp_disenio_2025_grupo_28.model.Huesped;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.service.GestionHuesped;
import java.util.*;

@RestController
@RequestMapping("/huespedes")
public class HuespedController {

    private final HuespedRepository repo;
    @Autowired
    private GestionHuesped gestionHuesped;

    public HuespedController(HuespedRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Huesped> listarHuespedes() {
        return repo.findAll();
    }

    // @PostMapping
    // public Huesped crearHuesped(@RequestBody Huesped huesped) {
    //     return repo.save(huesped);
    // }

    @PostMapping
    public ResponseEntity<?> crearHuesped(@RequestBody Huesped huesped) {
        try {
            Huesped nuevo = gestionHuesped.registrarHuesped(huesped);

            return ResponseEntity.ok(
                "El huésped " + nuevo.getNombre() + " " + nuevo.getApellido() + " ha sido satisfactoriamente cargado."
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;

import org.springframework.http.*;

import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.repository.HuespedRepository;
import tp.tp_disenio_2025_grupo_28.service.*;
import java.util.*;


@RestController
@RequestMapping("/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @PostMapping
    public ResponseEntity<Direccion> crear(@RequestBody Direccion direccion) {
        Direccion nueva = direccionService.guardar(direccion);
        return ResponseEntity.ok(nueva);
    }

    @GetMapping
    public ResponseEntity<List<Direccion>> listar() {
        return ResponseEntity.ok(direccionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Direccion> obtener(@PathVariable Integer id) {
        return direccionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
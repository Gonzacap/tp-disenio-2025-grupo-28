package tp.tp_disenio_2025_grupo_28.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tp.tp_disenio_2025_grupo_28.model.Direccion;
import tp.tp_disenio_2025_grupo_28.service.DireccionService;

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

package tp.tp_disenio_2025_grupo_28.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import tp.tp_disenio_2025_grupo_28.dto.OcupacionRequestDTO;
import tp.tp_disenio_2025_grupo_28.service.EstadoHabitacionPeriodoService;

@RestController
@RequestMapping("/ocupacion")
public class OcupacionController {

    @Autowired
    private EstadoHabitacionPeriodoService periodoService;

    @PostMapping("/seleccionar")
    public ResponseEntity<?> seleccionar(@RequestBody OcupacionRequestDTO dto) {

        boolean disponible
                = periodoService.estaDisponible(dto.getNumeroHabitacion(), dto.getFechaDesde(), dto.getFechaHasta());

        if (!disponible) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La habitación NO está disponible.");
        }

        return ResponseEntity.ok("OK");
    }

}

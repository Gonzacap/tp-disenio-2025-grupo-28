package tp.tp_disenio_2025_grupo_28.service;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Service;
import java.util.*;

import tp.tp_disenio_2025_grupo_28.model.*;
import tp.tp_disenio_2025_grupo_28.repository.*;


@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    public Direccion guardar(Direccion direccion) {
        return direccionRepository.save(direccion);
    }

    public List<Direccion> listar() {
        return direccionRepository.findAll();
    }

    public Optional<Direccion> buscarPorId(Integer id) {
        return direccionRepository.findById(id);
    }

    public void eliminar(Integer id) {
        direccionRepository.deleteById(id);
    }
}
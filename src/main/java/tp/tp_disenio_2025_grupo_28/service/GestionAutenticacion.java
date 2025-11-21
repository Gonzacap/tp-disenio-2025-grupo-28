package tp.tp_disenio_2025_grupo_28.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tp.tp_disenio_2025_grupo_28.model.Usuario;
import tp.tp_disenio_2025_grupo_28.repository.UsuarioRepository;

@Service
public class GestionAutenticacion {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario autenticarUsuario(Integer dni, String contrasena) {
        return usuarioRepository
                .findByDniAndContrasena(dni, contrasena)
                .orElse(null);
    }
}

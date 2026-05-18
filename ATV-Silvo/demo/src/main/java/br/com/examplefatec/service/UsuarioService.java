package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        Usuario usuarioExistente = null;
        if (usuario.getIdUsuario() != null) {
            usuarioExistente = findById(usuario.getIdUsuario());
        }

        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("ROLE_USER");
        }

        if (usuario.getSenhaUsuario() == null || usuario.getSenhaUsuario().isBlank()) {
            if (usuarioExistente != null) {
                usuario.setSenhaUsuario(usuarioExistente.getSenhaUsuario());
            }
        } else if (!usuario.getSenhaUsuario().startsWith("$2")) {
            usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        }

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        usuarioRepository.deleteById(id);
    }
}

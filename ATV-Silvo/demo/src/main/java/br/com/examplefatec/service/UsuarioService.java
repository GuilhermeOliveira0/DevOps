package br.com.examplefatec.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.PasswordResetTokenRepository;
import br.com.examplefatec.repository.UsuarioRepository;

/**
 * Service de usuarios.
 * Centraliza normalizacao, validacao de e-mail unico, criptografia de senha e exclusao segura.
 */
@Service
public class UsuarioService {

    private static final int MAX_EMAIL_LENGTH = 40;

    private final UsuarioRepository usuarioRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Salva usuario novo ou editado.
     * Normaliza campos, garante ROLE_USER por padrao e criptografa senha em texto puro com BCrypt.
     */
    public Usuario save(Usuario usuario) {
        normalizarDados(usuario);
        validarEmailUnico(usuario);

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

    /**
     * Padroniza nome, e-mail e role antes das validacoes e da persistencia.
     */
    private void normalizarDados(Usuario usuario) {
        if (usuario.getNomeUsuario() != null) {
            usuario.setNomeUsuario(usuario.getNomeUsuario().trim());
        }

        if (usuario.getEmailUsuario() != null) {
            usuario.setEmailUsuario(usuario.getEmailUsuario().trim().toLowerCase(Locale.ROOT));
        }

        if (usuario.getRole() != null) {
            usuario.setRole(usuario.getRole().trim());
        }
    }

    /**
     * Impede cadastro ou edicao com e-mail ja usado por outro usuario.
     */
    private void validarEmailUnico(Usuario usuario) {
        if (usuario.getEmailUsuario() == null || usuario.getEmailUsuario().isBlank()) {
            throw new IllegalArgumentException("Informe um email valido.");
        }

        if (usuario.getEmailUsuario().length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("O email deve ter no maximo 40 caracteres.");
        }

        boolean emailJaExiste = usuario.getIdUsuario() == null
                ? usuarioRepository.existsByEmailUsuario(usuario.getEmailUsuario())
                : usuarioRepository.existsByEmailUsuarioAndIdUsuarioNot(
                        usuario.getEmailUsuario(),
                        usuario.getIdUsuario());

        if (emailJaExiste) {
            throw new IllegalArgumentException("Este email ja esta cadastrado.");
        }
    }

    /**
     * Retorna todos os usuarios cadastrados.
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca usuario pelo id ou retorna null quando nao existe.
     */
    public Usuario findById(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Exclui usuario e remove tokens de recuperacao associados antes da exclusao.
     */
    @Transactional
    public void deleteById(int id) {
        Usuario usuario = findById(id);
        if (usuario == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUsuario(usuario);
        usuarioRepository.deleteById(id);
    }

    /**
     * Atualiza senha durante a recuperacao, sempre criptografando o valor recebido.
     */
    public void updatePassword(Usuario usuario, String rawPassword) {
        usuario.setSenhaUsuario(passwordEncoder.encode(rawPassword));
        usuarioRepository.save(usuario);
    }
}

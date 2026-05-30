package br.com.examplefatec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.PasswordResetTokenRepository;
import br.com.examplefatec.repository.UsuarioRepository;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository, passwordResetTokenRepository, passwordEncoder);
    }

    @Test
    void updatePasswordStoresEncodedPassword() {
        Usuario usuario = new Usuario();
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$10$senhaCriptografada");

        usuarioService.updatePassword(usuario, "novaSenha123");

        assertEquals("$2a$10$senhaCriptografada", usuario.getSenhaUsuario());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void saveNewUserDefaultsRoleAndStoresEncodedPassword() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("Usuario Teste");
        usuario.setEmailUsuario(" USER@test.com ");
        usuario.setSenhaUsuario("senhaAberta123");
        when(usuarioRepository.existsByEmailUsuario("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaAberta123")).thenReturn("$2a$10$senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario saved = usuarioService.save(usuario);

        assertEquals("ROLE_USER", saved.getRole());
        assertEquals("user@test.com", saved.getEmailUsuario());
        assertEquals("$2a$10$senhaCriptografada", saved.getSenhaUsuario());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void saveNewUserRejectsDuplicatedEmail() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("Usuario Teste");
        usuario.setEmailUsuario("user@test.com");
        usuario.setSenhaUsuario("senhaAberta123");
        when(usuarioRepository.existsByEmailUsuario("user@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Este email ja esta cadastrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deleteByIdRemovesPasswordResetTokensBeforeDeletingUser() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(352);
        when(usuarioRepository.findById(352)).thenReturn(java.util.Optional.of(usuario));

        usuarioService.deleteById(352);

        verify(passwordResetTokenRepository).deleteByUsuario(usuario);
        verify(usuarioRepository).deleteById(352);
    }
}

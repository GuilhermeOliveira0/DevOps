package br.com.examplefatec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.examplefatec.dto.ResetPasswordForm;
import br.com.examplefatec.entity.PasswordResetToken;
import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.PasswordResetTokenRepository;
import br.com.examplefatec.repository.UsuarioRepository;

class PasswordResetServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-05-29T12:00:00Z"),
            ZoneId.of("UTC"));

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmailService emailService;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordResetService = new PasswordResetService(
                usuarioRepository,
                tokenRepository,
                usuarioService,
                emailService,
                CLOCK,
                "http://localhost:8082",
                30);
    }

    @Test
    void requestPasswordResetForExistingEmailStoresOnlyTokenHashAndSendsResetEmail() {
        Usuario usuario = usuario("user@test.com");
        when(usuarioRepository.findFirstByEmailUsuarioOrderByIdUsuarioDesc("user@test.com"))
                .thenReturn(Optional.of(usuario));
        when(tokenRepository.findByUsuarioAndUsedAtIsNull(usuario)).thenReturn(List.of());

        passwordResetService.requestPasswordReset(" USER@test.com ", "127.0.0.1");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(usuario, savedToken.getUsuario());
        assertEquals(64, savedToken.getTokenHash().length());
        assertEquals(LocalDateTime.now(CLOCK).plusMinutes(30), savedToken.getExpiresAt());
        assertEquals(LocalDateTime.now(CLOCK), savedToken.getCreatedAt());
        assertEquals("127.0.0.1", savedToken.getRequestedIp());

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendPasswordResetEmail(eq(usuario), linkCaptor.capture());

        String rawToken = extractTokenFromLink(linkCaptor.getValue());
        assertNotNull(rawToken);
        assertNotEquals(rawToken, savedToken.getTokenHash());
        assertFalse(savedToken.getTokenHash().contains(rawToken));
    }

    @Test
    void requestPasswordResetForUnknownEmailDoesNotSaveTokenOrSendEmail() {
        when(usuarioRepository.findFirstByEmailUsuarioOrderByIdUsuarioDesc("missing@test.com"))
                .thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("missing@test.com", "127.0.0.1");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void resetPasswordRejectsExpiredToken() {
        PasswordResetToken token = validToken();
        token.setExpiresAt(LocalDateTime.now(CLOCK).minusMinutes(1));
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        ResetPasswordForm form = resetForm("raw-token", "novaSenha123", "novaSenha123");

        assertEquals(PasswordResetResult.INVALID_TOKEN, passwordResetService.resetPassword(form));
        verify(usuarioService, never()).updatePassword(any(), any());
    }

    @Test
    void resetPasswordRejectsAlreadyUsedToken() {
        PasswordResetToken token = validToken();
        token.setUsedAt(LocalDateTime.now(CLOCK).minusMinutes(1));
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        ResetPasswordForm form = resetForm("raw-token", "novaSenha123", "novaSenha123");

        assertEquals(PasswordResetResult.INVALID_TOKEN, passwordResetService.resetPassword(form));
        verify(usuarioService, never()).updatePassword(any(), any());
    }

    @Test
    void resetPasswordRejectsDifferentPasswords() {
        ResetPasswordForm form = resetForm("raw-token", "novaSenha123", "outraSenha123");

        assertEquals(PasswordResetResult.PASSWORD_MISMATCH, passwordResetService.resetPassword(form));
        verify(tokenRepository, never()).findByTokenHash(any());
    }

    @Test
    void resetPasswordRejectsShortPassword() {
        ResetPasswordForm form = resetForm("raw-token", "1234567", "1234567");

        assertEquals(PasswordResetResult.INVALID_PASSWORD, passwordResetService.resetPassword(form));
        verify(tokenRepository, never()).findByTokenHash(any());
    }

    @Test
    void resetPasswordWithValidTokenUpdatesPasswordMarksTokenUsedAndSendsConfirmation() {
        PasswordResetToken token = validToken();
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        ResetPasswordForm form = resetForm("raw-token", "novaSenha123", "novaSenha123");

        assertEquals(PasswordResetResult.SUCCESS, passwordResetService.resetPassword(form));

        verify(usuarioService).updatePassword(token.getUsuario(), "novaSenha123");
        assertEquals(LocalDateTime.now(CLOCK), token.getUsedAt());
        verify(tokenRepository).save(token);
        verify(emailService).sendPasswordChangedEmail(token.getUsuario());
    }

    @Test
    void validateTokenReturnsFalseForMissingToken() {
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertFalse(passwordResetService.isTokenValid("missing-token"));
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        when(tokenRepository.findByTokenHash(any())).thenReturn(Optional.of(validToken()));

        assertTrue(passwordResetService.isTokenValid("raw-token"));
    }

    private PasswordResetToken validToken() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUsuario(usuario("user@test.com"));
        token.setTokenHash("a".repeat(64));
        token.setCreatedAt(LocalDateTime.now(CLOCK).minusMinutes(5));
        token.setExpiresAt(LocalDateTime.now(CLOCK).plusMinutes(25));
        return token;
    }

    private Usuario usuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNomeUsuario("Usuario Teste");
        usuario.setEmailUsuario(email);
        usuario.setSenhaUsuario("$2a$10$senha");
        usuario.setRole("ROLE_USER");
        return usuario;
    }

    private ResetPasswordForm resetForm(String token, String senha, String confirmacaoSenha) {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token);
        form.setSenha(senha);
        form.setConfirmacaoSenha(confirmacaoSenha);
        return form;
    }

    private String extractTokenFromLink(String link) {
        String query = URI.create(link).getQuery();
        for (String parameter : query.split("&")) {
            String[] parts = parameter.split("=", 2);
            if ("token".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }
}

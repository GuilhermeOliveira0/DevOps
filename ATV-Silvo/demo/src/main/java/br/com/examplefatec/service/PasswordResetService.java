package br.com.examplefatec.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.examplefatec.dto.ResetPasswordForm;
import br.com.examplefatec.entity.PasswordResetToken;
import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.PasswordResetTokenRepository;
import br.com.examplefatec.repository.UsuarioRepository;

@Service
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final Clock clock;
    private final String baseUrl;
    private final long expirationMinutes;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            UsuarioService usuarioService,
            EmailService emailService,
            Clock clock,
            @Value("${app.base-url:http://localhost:8082}") String baseUrl,
            @Value("${app.password-reset.expiration-minutes:30}") long expirationMinutes) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
        this.clock = clock;
        this.baseUrl = baseUrl;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public void requestPasswordReset(String email, String requestedIp) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isBlank()) {
            return;
        }

        usuarioRepository.findFirstByEmailUsuarioOrderByIdUsuarioDesc(normalizedEmail)
                .ifPresent(usuario -> createResetToken(usuario, requestedIp));
    }

    public boolean isTokenValid(String rawToken) {
        return findUsableToken(rawToken) != null;
    }

    @Transactional
    public PasswordResetResult resetPassword(ResetPasswordForm form) {
        if (!isPasswordValid(form.getSenha())) {
            return PasswordResetResult.INVALID_PASSWORD;
        }

        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return PasswordResetResult.PASSWORD_MISMATCH;
        }

        PasswordResetToken token = findUsableToken(form.getToken());
        if (token == null) {
            return PasswordResetResult.INVALID_TOKEN;
        }

        usuarioService.updatePassword(token.getUsuario(), form.getSenha());
        token.setUsedAt(now());
        tokenRepository.save(token);
        emailService.sendPasswordChangedEmail(token.getUsuario());
        return PasswordResetResult.SUCCESS;
    }

    private void createResetToken(Usuario usuario, String requestedIp) {
        LocalDateTime usedAt = now();
        tokenRepository.findByUsuarioAndUsedAtIsNull(usuario).forEach(token -> {
            token.setUsedAt(usedAt);
            tokenRepository.save(token);
        });

        String rawToken = generateRawToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUsuario(usuario);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setCreatedAt(now());
        resetToken.setExpiresAt(now().plusMinutes(expirationMinutes));
        resetToken.setRequestedIp(requestedIp);
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(usuario, buildResetLink(rawToken));
    }

    private PasswordResetToken findUsableToken(String rawToken) {
        String normalizedToken = rawToken == null ? "" : rawToken.trim();
        if (normalizedToken.isBlank()) {
            return null;
        }

        return tokenRepository.findByTokenHash(hashToken(normalizedToken))
                .filter(token -> token.getUsedAt() == null)
                .filter(token -> !token.getExpiresAt().isBefore(now()))
                .orElse(null);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponivel para hash de token.", exception);
        }
    }

    private String buildResetLink(String rawToken) {
        return baseUrl.replaceAll("/$", "") + "/reset-password?token=" + rawToken;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isPasswordValid(String password) {
        return password != null
                && password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}

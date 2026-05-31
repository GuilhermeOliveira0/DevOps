package br.com.examplefatec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.examplefatec.dto.ForgotPasswordForm;
import br.com.examplefatec.dto.ResetPasswordForm;
import br.com.examplefatec.service.PasswordResetResult;
import br.com.examplefatec.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controller do fluxo "Esqueci minha senha".
 * Recebe os formularios, aciona o service e retorna apenas views ou redirects.
 */
@Controller
public class PasswordResetController {

    private static final String GENERIC_RESET_MESSAGE =
            "Se o email estiver cadastrado, enviaremos instrucoes para redefinir sua senha.";

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Abre a tela para informar o email de recuperacao.
     * Apenas prepara o objeto do formulario, sem consultar o banco.
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordForm(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        }
        return "forgot-password";
    }

    /**
     * Recebe o email informado e solicita a geracao de token.
     * A mensagem de retorno e sempre generica para nao revelar se o email existe.
     */
    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(
            @Valid @ModelAttribute ForgotPasswordForm forgotPasswordForm,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }

        passwordResetService.requestPasswordReset(forgotPasswordForm.getEmail(), request.getRemoteAddr());
        model.addAttribute("successMessage", GENERIC_RESET_MESSAGE);
        return "forgot-password";
    }

    /**
     * Valida o token da URL e abre a tela de nova senha quando ele ainda e utilizavel.
     */
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(required = false) String token, Model model) {
        if (!passwordResetService.isTokenValid(token)) {
            model.addAttribute("tokenError", "Link de redefinicao invalido, expirado ou ja utilizado.");
            return "reset-password";
        }

        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setToken(token);
        model.addAttribute("resetPasswordForm", resetPasswordForm);
        return "reset-password";
    }

    /**
     * Recebe nova senha e confirmacao, delegando a troca segura ao service.
     * Em sucesso, redireciona para o login com mensagem de confirmacao.
     */
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(
            @Valid @ModelAttribute ResetPasswordForm resetPasswordForm,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "reset-password";
        }

        PasswordResetResult result = passwordResetService.resetPassword(resetPasswordForm);
        if (result == PasswordResetResult.SUCCESS) {
            return "redirect:/login?resetSuccess";
        }

        model.addAttribute("resetError", messageFor(result));
        return "reset-password";
    }

    /**
     * Traduz o resultado de negocio em uma mensagem simples para a tela.
     */
    private String messageFor(PasswordResetResult result) {
        return switch (result) {
            case INVALID_TOKEN -> "Link de redefinicao invalido, expirado ou ja utilizado.";
            case INVALID_PASSWORD -> "A senha deve ter entre 8 e 72 caracteres.";
            case PASSWORD_MISMATCH -> "A senha e a confirmacao precisam ser iguais.";
            case SUCCESS -> "";
        };
    }
}

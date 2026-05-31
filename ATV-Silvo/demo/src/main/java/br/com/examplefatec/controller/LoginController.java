package br.com.examplefatec.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller das telas de login e home.
 * Centraliza a regra simples de redirecionar usuarios ja autenticados para /home.
 */
@Controller
public class LoginController {

    /**
     * Exibe a tela de login para anonimos.
     * Quando o usuario ja esta autenticado, redireciona para a home interna.
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (isLoggedUser(authentication)) {
            return "redirect:/home";
        }

        return "login";
    }

    /**
     * Retorna a pagina home depois do login.
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /**
     * Diferencia usuario logado de acesso anonimo do Spring Security.
     */
    private boolean isLoggedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}

package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.fatecads.fatecads.entity.Usuario;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication autenticacao) {
        boolean usuarioNormal = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> Usuario.PAPEL_USUARIO_NORMAL
                        .equals(autoridade.getAuthority()));

        if (usuarioNormal) {
            return "redirect:/loja";
        }

        return "home";
    }
}

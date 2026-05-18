package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/listar";
    }

    @GetMapping("/criar")
    public String criar(Model model, Authentication authentication) {
        model.addAttribute("usuario", new Usuario());
        configurarFormulario(model, authentication, true);
        return "usuario/formularioUsuario";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/usuarios/listar";
        }
        model.addAttribute("usuario", usuario);
        configurarFormulario(model, authentication, false);
        return "usuario/formularioUsuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, Authentication authentication) {
        boolean novoCadastro = usuario.getIdUsuario() == null;
        boolean admin = isAdmin(authentication);

        if (!novoCadastro && !admin) {
            return "redirect:/home";
        }

        if (!admin) {
            usuario.setRole("ROLE_USER");
        } else if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("ROLE_USER");
        }

        usuarioService.save(usuario);

        if (novoCadastro && !admin) {
            return "redirect:/login?created";
        }
        return "redirect:/usuarios/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/listar";
    }

    private void configurarFormulario(Model model, Authentication authentication, boolean novoCadastro) {
        boolean admin = isAdmin(authentication);
        model.addAttribute("canManageRole", admin);
        model.addAttribute("publicRegistration", !admin && novoCadastro);
        model.addAttribute("backHref", admin ? "/usuarios/listar" : "/");
        model.addAttribute("backLabel", admin ? "Voltar para lista" : "Voltar para inicio");
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}

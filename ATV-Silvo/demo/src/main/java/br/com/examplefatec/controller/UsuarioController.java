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

/**
 * Controller responsavel pelo cadastro, listagem e administracao de usuarios.
 * Mantem regras de perfil no controller apenas para decidir fluxo de tela e permissao visual.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista usuarios cadastrados para usuario autenticado.
     * Tambem informa para a view se o usuario logado pode editar/excluir perfis.
     */
    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("canManageUsers", isAdmin(authentication));
        return "usuario/listar";
    }

    /**
     * Abre o formulario de novo usuario.
     * A mesma tela atende cadastro publico e cadastro feito por administrador.
     */
    @GetMapping("/criar")
    public String criar(Model model, Authentication authentication) {
        model.addAttribute("usuario", new Usuario());
        configurarFormulario(model, authentication, true);
        return "usuario/formularioUsuario";
    }

    /**
     * Abre o formulario de edicao de usuario existente.
     * Apenas administradores devem chegar aqui pela regra do SecurityConfig.
     */
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

    /**
     * Salva cadastro novo ou edicao de usuario.
     * Novos cadastros publicos sempre recebem ROLE_USER antes de gravar.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, Authentication authentication, Model model) {
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

        try {
            usuarioService.save(usuario);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", exception.getMessage());
            configurarFormulario(model, authentication, novoCadastro);
            return "usuario/formularioUsuario";
        }

        if (novoCadastro && !admin) {
            return "redirect:/login?created";
        }
        return "redirect:/usuarios/listar";
    }

    /**
     * Exclui usuario pelo id.
     * A rota e protegida para ADMIN no SecurityConfig e altera dados no banco.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/listar";
    }

    /**
     * Prepara flags e links usados pela tela de formulario de usuario.
     */
    private void configurarFormulario(Model model, Authentication authentication, boolean novoCadastro) {
        boolean admin = isAdmin(authentication);
        model.addAttribute("canManageRole", admin);
        model.addAttribute("publicRegistration", !admin && novoCadastro);
        model.addAttribute("backHref", admin ? "/usuarios/listar" : "/");
        model.addAttribute("backLabel", admin ? "Voltar para lista" : "Voltar para inicio");
    }

    /**
     * Verifica se a autenticacao atual possui autoridade ROLE_ADMIN.
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}

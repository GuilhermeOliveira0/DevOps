package br.com.examplefatec.Security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import br.com.examplefatec.demo.DemoApplication;

@SpringBootTest(classes = DemoApplication.class)
class SecurityConfigTest {

    private final MockMvc mockMvc;

    @Autowired
    SecurityConfigTest(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void usuarioCriarIsPublic() throws Exception {
        mockMvc.perform(get("/usuarios/criar"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPageIsAvailableForAnonymousUser() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPageRedirectsLoggedUserToHome() throws Exception {
        mockMvc.perform(get("/login")
                .with(user("user@test.com").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void usuarioListarAllowsLoggedUser() throws Exception {
        mockMvc.perform(get("/usuarios/listar")
                .with(user("user@test.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioEditarStillRequiresAdmin() throws Exception {
        mockMvc.perform(get("/usuarios/editar/1")
                .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioExcluirStillRequiresAdmin() throws Exception {
        mockMvc.perform(get("/usuarios/excluir/1")
                .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }
}

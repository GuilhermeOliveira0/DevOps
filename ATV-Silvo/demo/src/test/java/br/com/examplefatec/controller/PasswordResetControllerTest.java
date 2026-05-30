package br.com.examplefatec.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

import br.com.examplefatec.service.PasswordResetResult;
import br.com.examplefatec.service.PasswordResetService;

class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PasswordResetController(passwordResetService))
                .setViewResolvers((viewName, locale) -> {
                    if (viewName.startsWith("redirect:")) {
                        return new RedirectView(viewName.substring("redirect:".length()), true);
                    }
                    return new AbstractView() {
                        @Override
                        protected void renderMergedOutputModel(
                                java.util.Map<String, Object> model,
                                jakarta.servlet.http.HttpServletRequest request,
                                jakarta.servlet.http.HttpServletResponse response) {
                        }
                    };
                })
                .build();
    }

    @Test
    void forgotPasswordFormIsPublicView() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("forgotPasswordForm"));
    }

    @Test
    void forgotPasswordSubmitUsesGenericSuccessMessage() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .param("email", "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attribute("successMessage", containsString("Se o email estiver cadastrado")));

        verify(passwordResetService).requestPasswordReset(eq("user@test.com"), any());
    }

    @Test
    void resetPasswordWithInvalidTokenShowsControlledError() throws Exception {
        when(passwordResetService.isTokenValid("bad-token")).thenReturn(false);

        mockMvc.perform(get("/reset-password").param("token", "bad-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("tokenError"));
    }

    @Test
    void resetPasswordWithValidTokenShowsForm() throws Exception {
        when(passwordResetService.isTokenValid("valid-token")).thenReturn(true);

        mockMvc.perform(get("/reset-password").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("resetPasswordForm"));
    }

    @Test
    void successfulResetRedirectsToLoginWithSuccessMessage() throws Exception {
        when(passwordResetService.resetPassword(any())).thenReturn(PasswordResetResult.SUCCESS);

        mockMvc.perform(post("/reset-password")
                .param("token", "valid-token")
                .param("senha", "novaSenha123")
                .param("confirmacaoSenha", "novaSenha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?resetSuccess"));
    }
}

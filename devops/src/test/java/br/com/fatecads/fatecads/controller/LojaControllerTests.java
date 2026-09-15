package br.com.fatecads.fatecads.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.model.Carrinho;

@SpringBootTest
@AutoConfigureMockMvc
class LojaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void usuarioNormalDeveAbrirALoja() throws Exception {
        mockMvc.perform(get("/loja").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isOk())
                .andExpect(view().name("loja/listarProdutos"));
    }

    @Test
    void usuarioNormalNaoDeveAbrirCadastroAdministrativo() throws Exception {
        mockMvc.perform(get("/produtos/listar").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorDeveAbrirCadastroAdministrativo() throws Exception {
        mockMvc.perform(get("/produtos/listar").with(user("adm").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("produto/listarProdutos"));
    }

    @Test
    void usuarioNormalDeveSerEnviadoParaALojaDepoisDoLogin() throws Exception {
        mockMvc.perform(get("/home").with(user("cliente").roles("CLIENTE")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loja"));
    }

    @Test
    void deveMostrarOCarrinhoComProduto() throws Exception {
        Produto produto = new Produto();
        produto.setIdProduto(1);
        produto.setDescricaoProduto("Caderno");
        produto.setValorProduto(15.00);

        Carrinho carrinho = new Carrinho();
        carrinho.adicionarProduto(produto, 2);

        mockMvc.perform(get("/loja/carrinho")
                        .with(user("cliente").roles("CLIENTE"))
                        .sessionAttr("carrinho", carrinho))
                .andExpect(status().isOk())
                .andExpect(view().name("loja/carrinho"));
    }

    @Test
    void deveRecusarQuantidadeInvalida() throws Exception {
        mockMvc.perform(post("/loja/carrinho/adicionar")
                        .with(user("cliente").roles("CLIENTE"))
                        .with(csrf())
                        .param("idProduto", "999999")
                        .param("quantidade", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loja"));
    }
}

package br.com.fatecads.fatecads.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.ProdutoService;

/*
 * Tema 6 - Teste de validacao do cadastro de produtos (camada web).
 *
 * Simula o envio do formulario /produtos/salvar com o MockMvc, do jeito
 * que um navegador (ou alguem burlando o HTML) faria. O ProdutoService e
 * um mock: assim conseguimos provar que, com dado invalido, NADA chega a
 * ser gravado no banco.
 */
@WebMvcTest(ProdutoController.class)
@DisplayName("Tema 6 - Validação do formulário de cadastro de produtos")
class ProdutoCadastroValidacaoTests {

    // Tela que o controller devolve quando algum campo tem erro
    private static final String TELA_DO_FORMULARIO = "produto/formularioProduto";

    private static final String DESCRICAO_VALIDA = "Caderno";
    private static final String VALOR_VALIDO = "25.90";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    // POST do formulario como administrador logado, ainda sem nenhum campo
    private MockHttpServletRequestBuilder envioComoAdministrador() {
        return post("/produtos/salvar")
                .with(user("adm").roles("USER"))
                .with(csrf());
    }

    // Unidade e marca sempre validas: os testes so variam descricao e valor
    private MockHttpServletRequestBuilder formularioPreenchido(String descricao, String valor) {
        return envioComoAdministrador()
                .param("descricaoProduto", descricao)
                .param("valorProduto", valor)
                .param("unidadeProduto", "UN")
                .param("marcaProduto", "Tilibra");
    }

    private MockHttpServletRequestBuilder formularioValido() {
        return formularioPreenchido(DESCRICAO_VALIDA, VALOR_VALIDO);
    }

    private MockHttpServletRequestBuilder formularioComDescricao(String descricao) {
        return formularioPreenchido(descricao, VALOR_VALIDO);
    }

    private MockHttpServletRequestBuilder formularioComValor(String valor) {
        return formularioPreenchido(DESCRICAO_VALIDA, valor);
    }

    private void verificaQueNadaFoiSalvo() throws Exception {
        verify(produtoService, never()).save(any(), any(), anyBoolean());
        verify(produtoService, never()).save(any());
    }

    // TESTE 1: envia produto certo -> tem que salvar e voltar para a listagem
    @Test
    @DisplayName("Produto válido é salvo e volta para a listagem")
    void deveSalvarProdutoValido() throws Exception {
        mockMvc.perform(formularioValido())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos/listar"))
                .andExpect(model().hasNoErrors());

        verify(produtoService).save(any(Produto.class), any(), anyBoolean());
    }

    // TESTE 2: envia preco 0.01 (o menor valido) -> tem que salvar
    @Test
    @DisplayName("Valor mínimo (0,01) é aceito")
    void deveAceitarMenorValorPositivo() throws Exception {
        mockMvc.perform(formularioComValor("0.01"))
                .andExpect(redirectedUrl("/produtos/listar"));

        verify(produtoService).save(any(Produto.class), any(), anyBoolean());
    }

    // TESTE 3: envia nome vazio -> volta pro formulario com "Informe a descrição" e nao salva
    @Test
    @DisplayName("Nome vazio é rejeitado e o formulário mostra a mensagem")
    void deveRejeitarNomeVazio() throws Exception {
        mockMvc.perform(formularioComDescricao(""))
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode("produto", "descricaoProduto", "NotBlank"))
                .andExpect(content().string(containsString("Informe a descrição do produto.")));

        verificaQueNadaFoiSalvo();
    }

    // TESTE 4: envia preco -10 -> volta com "maior que zero" e nao salva
    @Test
    @DisplayName("Preço negativo é rejeitado")
    void deveRejeitarPrecoNegativo() throws Exception {
        mockMvc.perform(formularioComValor("-10"))
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode("produto", "valorProduto", "Positive"))
                .andExpect(content().string(containsString("O valor precisa ser maior que zero.")));

        verificaQueNadaFoiSalvo();
    }

    // TESTE 5: envia preco 0 -> tem que recusar e nao salvar
    @Test
    @DisplayName("Preço zero é rejeitado")
    void deveRejeitarPrecoZero() throws Exception {
        mockMvc.perform(formularioComValor("0"))
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().attributeHasFieldErrors("produto", "valorProduto"));

        verificaQueNadaFoiSalvo();
    }

    // TESTE 6: envia "abc" no preco -> volta com "número válido" e nao salva
    @Test
    @DisplayName("Preço com texto (\"abc\") é rejeitado")
    void deveRejeitarPrecoQueNaoENumero() throws Exception {
        mockMvc.perform(formularioComValor("abc"))
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode("produto", "valorProduto", "typeMismatch"))
                .andExpect(content().string(containsString("O valor precisa ser um número válido.")));

        verificaQueNadaFoiSalvo();
    }

    // TESTE 7: envia o formulario em branco -> 4 erros (um por campo) e nao salva
    @Test
    @DisplayName("Formulário enviado sem nenhum campo aponta os 4 obrigatórios")
    void deveRejeitarCamposObrigatoriosNaoPreenchidos() throws Exception {
        MockHttpServletRequestBuilder formularioEmBranco = envioComoAdministrador();

        mockMvc.perform(formularioEmBranco)
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().errorCount(4))
                .andExpect(model().attributeHasFieldErrors("produto",
                        "descricaoProduto", "valorProduto", "unidadeProduto", "marcaProduto"));

        verificaQueNadaFoiSalvo();
    }

    // TESTE 8: envia nome com 41 letras -> passa do limite de 40, recusa e nao salva
    @Test
    @DisplayName("Nome acima de 40 caracteres é rejeitado")
    void deveRejeitarNomeMuitoGrande() throws Exception {
        mockMvc.perform(formularioComDescricao("X".repeat(41)))
                .andExpect(status().isOk())
                .andExpect(view().name(TELA_DO_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode("produto", "descricaoProduto", "Size"));

        verificaQueNadaFoiSalvo();
    }
}

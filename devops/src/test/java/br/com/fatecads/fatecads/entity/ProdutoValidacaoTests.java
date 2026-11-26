package br.com.fatecads.fatecads.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/*
 * Tema 6 - Teste de validacao do cadastro de produtos (camada de regra).
 *
 * Testa as anotacoes da entidade Produto direto com o Validator do
 * Jakarta Bean Validation. Nao sobe o Spring nem precisa de banco:
 * roda em milissegundos.
 */
@DisplayName("Tema 6 - Regras de validação da entidade Produto")
class ProdutoValidacaoTests {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void criarValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void fecharValidador() {
        fabrica.close();
    }

    // Produto correto: o ponto de partida de cada teste
    private Produto produtoValido() {
        Produto produto = new Produto();
        produto.setDescricaoProduto("Caderno 10 matérias");
        produto.setValorProduto(25.90);
        produto.setUnidadeProduto("UN");
        produto.setMarcaProduto("Tilibra");
        return produto;
    }

    // Devolve o nome dos campos que tiveram erro
    private Set<String> camposComErro(Produto produto) {
        return validador.validate(produto).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    // TESTE 1: produto todo certo -> nenhuma regra pode reclamar
    @Test
    @DisplayName("Aceita um produto com todos os campos corretos")
    void deveAceitarProdutoValido() {
        Set<ConstraintViolation<Produto>> erros = validador.validate(produtoValido());
        assertTrue(erros.isEmpty(), "Produto válido não deveria ter erros: " + erros);
    }

    // TESTE 2: nome vazio -> testa 4 casos: null, "", "   " (espacos) e tab
    @ParameterizedTest(name = "Rejeita descrição [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t" })
    @DisplayName("Rejeita nome do produto vazio ou só com espaços")
    void deveRejeitarNomeVazio(String descricao) {
        Produto produto = produtoValido();
        produto.setDescricaoProduto(descricao);

        assertEquals(Set.of("descricaoProduto"), camposComErro(produto));
    }

    // TESTE 3: nome com 41 letras -> passa do limite de 40, tem que recusar
    @Test
    @DisplayName("Rejeita nome com mais de 40 caracteres")
    void deveRejeitarNomeMuitoGrande() {
        Produto produto = produtoValido();
        produto.setDescricaoProduto("X".repeat(41));

        assertEquals(Set.of("descricaoProduto"), camposComErro(produto));
    }

    // TESTE 4: preco negativo ou zero -> testa -0.01, -1, -150 e 0, todos devem ser recusados
    @ParameterizedTest(name = "Rejeita preço {0}")
    @ValueSource(doubles = { -0.01, -1.0, -150.0, 0.0 })
    @DisplayName("Rejeita preço negativo ou zero")
    void deveRejeitarPrecoNegativoOuZero(double preco) {
        Produto produto = produtoValido();
        produto.setValorProduto(preco);

        assertEquals(Set.of("valorProduto"), camposComErro(produto));
    }

    // TESTE 5: preco positivo -> 0.01 (o menor possivel), 1 e 9999.99 devem ser aceitos
    @ParameterizedTest(name = "Aceita preço {0}")
    @ValueSource(doubles = { 0.01, 1.0, 9999.99 })
    @DisplayName("Aceita preços positivos, inclusive o menor possível")
    void deveAceitarPrecoPositivo(double preco) {
        Produto produto = produtoValido();
        produto.setValorProduto(preco);

        assertTrue(camposComErro(produto).isEmpty());
    }

    // TESTE 6: preco em branco (null) -> tem que recusar
    @Test
    @DisplayName("Rejeita preço não informado")
    void deveRejeitarPrecoNulo() {
        Produto produto = produtoValido();
        produto.setValorProduto(null);

        assertEquals(Set.of("valorProduto"), camposComErro(produto));
    }

    // TESTE 7: unidade e marca vazias -> as duas tem que dar erro
    @Test
    @DisplayName("Rejeita unidade e marca em branco")
    void deveRejeitarUnidadeEMarcaVazias() {
        Produto produto = produtoValido();
        produto.setUnidadeProduto("");
        produto.setMarcaProduto(" ");

        assertEquals(Set.of("unidadeProduto", "marcaProduto"), camposComErro(produto));
    }

    // TESTE 8: produto sem nada preenchido -> erro nos 4 campos obrigatorios
    @Test
    @DisplayName("Produto totalmente vazio gera erro nos 4 campos obrigatórios")
    void deveApontarTodosOsCamposObrigatorios() {
        Produto vazio = new Produto();

        assertEquals(
                Set.of("descricaoProduto", "valorProduto", "unidadeProduto", "marcaProduto"),
                camposComErro(vazio));
    }

    // TESTE 9: confere o TEXTO do erro que o usuario ve na tela
    @Test
    @DisplayName("A mensagem de erro é amigável para o usuário")
    void deveTerMensagemAmigavel() {
        Produto produto = produtoValido();
        produto.setValorProduto(-5.0);

        Set<ConstraintViolation<Produto>> erros = validador.validate(produto);
        assertEquals(1, erros.size(), "Deveria ter exatamente 1 erro: " + erros);

        String mensagem = erros.iterator().next().getMessage();
        assertEquals("O valor precisa ser maior que zero.", mensagem);
    }
}

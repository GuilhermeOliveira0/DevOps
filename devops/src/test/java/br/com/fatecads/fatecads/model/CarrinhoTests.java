package br.com.fatecads.fatecads.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.fatecads.fatecads.entity.Produto;

class CarrinhoTests {

    @Test
    void deveAdicionarSomarEExcluirProdutos() {
        Produto caderno = criarProduto(1, "Caderno", 12.50);
        Produto caneta = criarProduto(2, "Caneta", 3.00);
        Carrinho carrinho = new Carrinho();

        carrinho.adicionarProduto(caderno, 1);
        carrinho.adicionarProduto(caderno, 2);
        carrinho.adicionarProduto(caneta, 1);

        assertEquals(2, carrinho.getItens().size());
        assertEquals(4, carrinho.getQuantidadeTotal());
        assertEquals(40.50, carrinho.getValorTotal());

        carrinho.removerProduto(1);

        assertEquals(1, carrinho.getQuantidadeTotal());
        assertEquals(3.00, carrinho.getValorTotal());
    }

    @Test
    void deveAlterarAQuantidade() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarProduto(criarProduto(1, "Caderno", 10.00), 1);

        carrinho.alterarQuantidade(1, 3);

        assertEquals(3, carrinho.getQuantidadeTotal());
        assertEquals(30.00, carrinho.getValorTotal());
    }

    private Produto criarProduto(Integer id, String descricao, Double valor) {
        Produto produto = new Produto();
        produto.setIdProduto(id);
        produto.setDescricaoProduto(descricao);
        produto.setValorProduto(valor);
        return produto;
    }
}

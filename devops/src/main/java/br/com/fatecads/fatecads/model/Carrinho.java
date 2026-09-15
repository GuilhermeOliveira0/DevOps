package br.com.fatecads.fatecads.model;

import java.util.ArrayList;
import java.util.List;

import br.com.fatecads.fatecads.entity.Produto;

/*
 * O carrinho pertence a sessao do usuario.
 * Por isso ele e uma classe Java simples e nao possui anotacao @Entity.
 */
public class Carrinho {

    private List<ItemCarrinho> itens = new ArrayList<>();

    public void adicionarProduto(Produto produto, Integer quantidade) {
        ItemCarrinho itemExistente = buscarItem(produto.getIdProduto());

        if (itemExistente == null) {
            itens.add(new ItemCarrinho(produto, quantidade));
        } else {
            itemExistente.adicionarQuantidade(quantidade);
        }
    }

    public void alterarQuantidade(Integer idProduto, Integer quantidade) {
        ItemCarrinho item = buscarItem(idProduto);
        if (item != null) {
            item.setQuantidade(quantidade);
        }
    }

    public void removerProduto(Integer idProduto) {
        itens.removeIf(item -> item.getIdProduto().equals(idProduto));
    }

    public Integer getQuantidadeTotal() {
        int quantidadeTotal = 0;
        for (ItemCarrinho item : itens) {
            quantidadeTotal += item.getQuantidade();
        }
        return quantidadeTotal;
    }

    public Double getValorTotal() {
        double valorTotal = 0.0;
        for (ItemCarrinho item : itens) {
            valorTotal += item.calcularSubtotal();
        }
        return Math.round(valorTotal * 100.0) / 100.0;
    }

    private ItemCarrinho buscarItem(Integer idProduto) {
        for (ItemCarrinho item : itens) {
            if (item.getIdProduto().equals(idProduto)) {
                return item;
            }
        }
        return null;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }
}

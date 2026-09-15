package br.com.fatecads.fatecads.model;

import br.com.fatecads.fatecads.entity.Produto;

/*
 * Representa uma linha do carrinho.
 * Nao e uma entidade porque o carrinho fica somente na sessao do usuario.
 */
public class ItemCarrinho {

    private Integer idProduto;
    private String descricaoProduto;
    private Double valorUnitario;
    private Integer quantidade;
    private boolean temFoto;

    public ItemCarrinho(Produto produto, Integer quantidade) {
        this.idProduto = produto.getIdProduto();
        this.descricaoProduto = produto.getDescricaoProduto();
        this.valorUnitario = produto.getValorProduto();
        this.quantidade = quantidade;
        this.temFoto = produto.isTemFoto();
    }

    public void adicionarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }

    public Double calcularSubtotal() {
        double valor = valorUnitario == null ? 0.0 : valorUnitario;
        return Math.round(valor * quantidade * 100.0) / 100.0;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public boolean isTemFoto() {
        return temFoto;
    }
}

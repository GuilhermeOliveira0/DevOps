package br.com.fatecads.fatecads.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    private Integer quantidade;
    
    private Double preco;

    private Double subtotal;

    // @JsonIgnore corta a recursao Pedido -> itens -> Pedido na serializacao JSON
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idPedido_fk")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idProduto_fk")
    private Produto produto;

    // Método para calcular o subtotal
    public Double calcularSubtotal() {
        if (quantidade == null || preco == null) {
            return 0.0;
        }
        // Arredonda em centavos: double puro devolve coisas como 209.29999999999998
        return Math.round(quantidade * preco * 100.0) / 100.0;
    }

    public void atualizarSubtotal(){
        this.subtotal = calcularSubtotal();
    }
}

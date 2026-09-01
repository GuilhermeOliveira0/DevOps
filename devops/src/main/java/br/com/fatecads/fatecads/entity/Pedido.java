package br.com.fatecads.fatecads.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idPedido;

    private LocalDate dataPedido;

    private Double totalPedido;

    @ManyToOne
    @JoinColumn(name = "idAluno_fk")
    private Aluno aluno;

    // orphanRemoval deixa o item ser apagado só de sair desta lista
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoPedido> itens = new ArrayList<>();

    // Médoto para calcular o total
    public Double calcularTotal(){
        Double total = 0.0;

        if (itens != null) {
            for (ItemDoPedido item : itens) {
                total += item.calcularSubtotal();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public void atualizarTotal() {
        this.totalPedido = calcularTotal();
    }

}

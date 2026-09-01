package br.com.fatecads.fatecads.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProduto;

    @Column(nullable = false, length = 40)
    private String descricaoProduto;

    private Double valorProduto;

    @Column(nullable = false, length = 10)
    private String unidadeProduto;

    @Column(nullable = false, length = 30)
    private String marcaProduto;

    // A imagem fica gravada no proprio Postgres, em uma coluna bytea
    @JsonIgnore
    @Column(columnDefinition = "bytea")
    private byte[] fotoProduto;

    @Column(length = 100)
    private String tipoFotoProduto;

    @Column(length = 150)
    private String nomeFotoProduto;

    @JsonIgnore
    @OneToMany(mappedBy = "produto")
    private List<ItemDoPedido> itens;

    // Usado pelas telas para decidir se mostra a miniatura ou o aviso de "sem imagem"
    public boolean isTemFoto() {
        return fotoProduto != null && fotoProduto.length > 0;
    }
}

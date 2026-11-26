package br.com.fatecads.fatecads.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    /*
     * Regras de validacao do cadastro (Tema 6).
     * O Spring confere estas anotacoes quando o controller recebe @Valid,
     * entao a regra vale mesmo que alguem burle o "required" do HTML.
     */
    @NotBlank(message = "Informe a descrição do produto.")
    @Size(max = 40, message = "A descrição pode ter no máximo 40 caracteres.")
    @Column(nullable = false, length = 40)
    private String descricaoProduto;

    @NotNull(message = "Informe o valor do produto.")
    @Positive(message = "O valor precisa ser maior que zero.")
    private Double valorProduto;

    @NotBlank(message = "Informe a unidade do produto.")
    @Size(max = 10, message = "A unidade pode ter no máximo 10 caracteres.")
    @Column(nullable = false, length = 10)
    private String unidadeProduto;

    @NotBlank(message = "Informe a marca do produto.")
    @Size(max = 30, message = "A marca pode ter no máximo 30 caracteres.")
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

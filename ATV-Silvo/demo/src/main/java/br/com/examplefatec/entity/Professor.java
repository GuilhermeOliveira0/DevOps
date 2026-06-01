package br.com.examplefatec.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA que representa um professor.
 * Um professor pode estar vinculado a varias disciplinas.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProfessor;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o nome do professor.")
    @Size(max = 40, message = "O nome do professor deve ter no maximo 40 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome do professor deve conter apenas letras e espacos.")
    private String nomeProfessor;

    @Column(nullable = false, length = 11)
    @NotBlank(message = "Informe o telefone do professor.")
    @Pattern(regexp = "^[0-9() .-]{10,15}$", message = "O telefone deve conter 10 ou 11 numeros.")
    private String telefoneProfessor;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe a graduacao do professor.")
    @Size(max = 50, message = "A graduacao deve ter no maximo 50 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "A graduacao deve conter apenas letras e espacos.")
    private String graduacaoProfessor;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Informe o RM do professor.")
    @Size(max = 20, message = "O RM deve ter no maximo 20 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "O RM deve conter apenas letras e numeros.")
    private String rmProfessor;

    @OneToMany(mappedBy = "professor")
    private List<Disciplina> disciplinas;
}

package br.com.examplefatec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA que representa uma disciplina.
 * Guarda os relacionamentos com curso e professor selecionados no cadastro.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idDisciplina;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Informe a sigla da disciplina.")
    @Size(max = 20, message = "A sigla deve ter no maximo 20 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "A sigla da disciplina deve conter apenas letras e numeros.")
    private String siglaDisciplina;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o nome da disciplina.")
    @Size(max = 40, message = "O nome da disciplina deve ter no maximo 40 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome da disciplina deve conter apenas letras e espacos.")
    private String nomeDisciplina;

    @Column(nullable = false)
    @Positive(message = "A carga horaria da disciplina deve ser maior que zero.")
    private int cargaHorariaDisciplina;

    @ManyToOne
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "idProfessor_fk")
    private Professor professor;
}

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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA que representa um curso.
 * Um curso pode estar vinculado a varias disciplinas.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idCurso;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o nome do curso.")
    @Size(max = 40, message = "O nome do curso deve ter no maximo 40 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome do curso deve conter apenas letras e espacos.")
    private String nomeCurso;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o periodo do curso.")
    @Size(max = 40, message = "O periodo deve ter no maximo 40 caracteres.")
    private String periodoCurso;

    @Column(nullable = false)
    @Positive(message = "A carga horaria do curso deve ser maior que zero.")
    private int cargaHorariaCurso;

    @OneToMany(mappedBy = "curso")
    private List<Disciplina> disciplinas;
}

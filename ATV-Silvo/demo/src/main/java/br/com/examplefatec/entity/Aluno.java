package br.com.examplefatec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA que representa um aluno no banco de dados.
 * Tambem guarda o relacionamento opcional com o curso escolhido.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idAluno;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o nome do aluno.")
    @Size(max = 40, message = "O nome do aluno deve ter no maximo 40 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome do aluno deve conter apenas letras e espacos.")
    private String nomeAluno;

    @Column(length = 100)
    @NotBlank(message = "Informe um email valido.")
    @Email(message = "Informe um email valido.")
    @Size(max = 100, message = "O email do aluno deve ter no maximo 100 caracteres.")
    private String emailAluno;

    @Column(nullable = false, length = 11)
    @NotBlank(message = "Informe o telefone do aluno.")
    @Pattern(regexp = "^[0-9() .-]{10,15}$", message = "O telefone deve conter 10 ou 11 numeros.")
    private String telefoneAluno;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe o endereco do aluno.")
    @Size(max = 50, message = "O endereco deve ter no maximo 50 caracteres.")
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    @NotBlank(message = "Informe o CPF do aluno.")
    @Pattern(regexp = "^[0-9.-]{11,14}$", message = "O CPF deve conter exatamente 11 numeros.")
    private String cpfAluno;

    @Column(nullable = false)
    @NotBlank(message = "Informe o RA do aluno.")
    @Size(max = 20, message = "O RA deve ter no maximo 20 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "O RA deve conter apenas letras e numeros.")
    private String raAluno;

    @ManyToOne
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;
}

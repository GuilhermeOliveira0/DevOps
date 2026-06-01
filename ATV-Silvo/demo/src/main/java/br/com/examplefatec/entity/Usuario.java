package br.com.examplefatec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA que representa usuario de acesso ao sistema.
 * A senha deve ser persistida somente em formato criptografado e a role deve seguir o padrao ROLE_*.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idUsuario;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe o nome do usuario.")
    @Size(max = 40, message = "O nome do usuario deve ter no maximo 40 caracteres.")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "O nome do usuario deve conter apenas letras e espacos.")
    private String nomeUsuario;

    @Column(nullable = false, length = 40) 
    @NotBlank(message = "Informe um email valido.")
    @Email(message = "Informe um email valido.")
    @Size(max = 40, message = "O email deve ter no maximo 40 caracteres.")
    private String emailUsuario;

    @Column(nullable = false, length = 150)  
    @Size(max = 150, message = "A senha deve ter no maximo 150 caracteres.")
    private String senhaUsuario;

    @Column(name = "role_usuario", nullable = false, length = 30)
    private String role = "ROLE_USER";
}

package br.com.examplefatec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String nomeUsuario;

    @Column(nullable = false, length = 40) 
    private String emailUsuario;

    @Column(nullable = false, length = 150)  
    private String senhaUsuario;

    @Column(name = "role_usuario", nullable = false, length = 30)
    private String role = "ROLE_USER";
}

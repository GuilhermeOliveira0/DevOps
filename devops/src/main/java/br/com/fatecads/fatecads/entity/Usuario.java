package br.com.fatecads.fatecads.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    public static final String PAPEL_ADMINISTRADOR = "ROLE_USER";
    public static final String PAPEL_USUARIO_NORMAL = "ROLE_CLIENTE";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idUsuario;

    @Column(nullable = false, length = 80)
    private String nomeUsuario;

    // unique: rede de seguranca no banco. A checagem na service da a mensagem
    // amigavel; esta aqui impede o duplicado mesmo por fora da aplicacao.
    @Column(nullable = false, length = 80, unique = true)
    private String emailUsuario;

    @Column(nullable = false, length = 50, unique = true)
    private String loginUsuario;

    @Column(nullable = false, length = 150)
    private String senhaUsuario;

    @Column(length = 20)
    private String telefoneUsuario;

    @Column(length = 6)
    private String codigoRecuperacao;

    private LocalDateTime expiracaoCodigo;

    // Novas contas sao compradores. O administrador pode trocar esse papel.
    private String role = PAPEL_USUARIO_NORMAL;
}

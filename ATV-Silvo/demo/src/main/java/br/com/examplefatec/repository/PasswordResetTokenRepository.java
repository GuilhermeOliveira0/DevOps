package br.com.examplefatec.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.PasswordResetToken;
import br.com.examplefatec.entity.Usuario;

/**
 * Repository dos tokens de recuperacao de senha.
 * Permite localizar token pelo hash e limpar tokens vinculados a um usuario.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * Busca token pelo hash SHA-256 salvo no banco.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /**
     * Lista tokens ainda nao usados de um usuario para invalidacao ao gerar novo link.
     */
    List<PasswordResetToken> findByUsuarioAndUsedAtIsNull(Usuario usuario);

    /**
     * Remove tokens de um usuario antes de excluir o cadastro.
     */
    void deleteByUsuario(Usuario usuario);
}

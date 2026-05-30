package br.com.examplefatec.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.PasswordResetToken;
import br.com.examplefatec.entity.Usuario;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    List<PasswordResetToken> findByUsuarioAndUsedAtIsNull(Usuario usuario);

    void deleteByUsuario(Usuario usuario);
}

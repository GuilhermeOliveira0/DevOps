package br.com.fatecads.fatecads.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLoginUsuario(String loginUsuario);

    Optional<Usuario> findByEmailUsuario(String emailUsuario);

    /*
     * Versoes em lista: usadas para checar duplicidade antes de gravar.
     * As versoes com Optional acima estouram NonUniqueResultException
     * quando ja existe mais de um registro com o mesmo valor.
     */
    List<Usuario> findAllByLoginUsuario(String loginUsuario);

    List<Usuario> findAllByEmailUsuario(String emailUsuario);
}

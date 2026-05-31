package br.com.examplefatec.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Usuario;

/**
 * Repository de usuarios.
 * Contem consultas por e-mail usadas no login, cadastro e recuperacao de senha.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca usuario pelo e-mail exato.
     */
    Optional<Usuario> findByEmailUsuario(String loginUsuario);

    /**
     * Busca o usuario mais recente com o e-mail informado.
     * Mantem compatibilidade caso existam dados antigos duplicados no banco.
     */
    Optional<Usuario> findFirstByEmailUsuarioOrderByIdUsuarioDesc(String emailUsuario);

    /**
     * Verifica se ja existe cadastro com o e-mail informado.
     */
    boolean existsByEmailUsuario(String emailUsuario);

    /**
     * Verifica duplicidade de e-mail ignorando o proprio usuario em edicoes.
     */
    boolean existsByEmailUsuarioAndIdUsuarioNot(String emailUsuario, Integer idUsuario);
}

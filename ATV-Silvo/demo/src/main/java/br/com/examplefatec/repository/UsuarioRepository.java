package br.com.examplefatec.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {    

        Optional<Usuario> findByEmailUsuario(String loginUsuario);

        Optional<Usuario> findFirstByEmailUsuarioOrderByIdUsuarioDesc(String emailUsuario);

        boolean existsByEmailUsuario(String emailUsuario);

        boolean existsByEmailUsuarioAndIdUsuarioNot(String emailUsuario, Integer idUsuario);

      
}

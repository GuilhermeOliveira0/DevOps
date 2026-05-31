package br.com.examplefatec.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Usuario;
import br.com.examplefatec.repository.UsuarioRepository;

/**
 * Service usado pelo Spring Security para carregar usuario durante o login.
 * O e-mail digitado no formulario e tratado como username.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca usuario pelo e-mail e converte para UserDetails.
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findFirstByEmailUsuarioOrderByIdUsuarioDesc(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + login));
        return new UserDetailsImpl(usuario);
    }
}

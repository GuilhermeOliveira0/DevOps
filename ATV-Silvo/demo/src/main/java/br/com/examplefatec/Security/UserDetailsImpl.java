package br.com.examplefatec.Security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.examplefatec.entity.Usuario;

/**
 * Adaptador entre a entity Usuario e o contrato UserDetails do Spring Security.
 * Informa username, senha criptografada e authorities do usuario autenticado.
 */
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Entrega a role salva no banco como authority do Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usuario.getRole()));
    }

    /**
     * Retorna a senha ja criptografada para comparacao feita pelo Spring Security.
     */
    @Override
    public String getPassword() {
        return usuario.getSenhaUsuario();
    }

    /**
     * Usa e-mail como username de login.
     */
    @Override
    public String getUsername() {
        return usuario.getEmailUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

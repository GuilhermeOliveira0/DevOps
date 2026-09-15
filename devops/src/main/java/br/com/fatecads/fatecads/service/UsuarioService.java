package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;


@Service
public class UsuarioService {
    
    // Injeção de Dependência do repositório de alunos
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para salvar um aluno
    public Usuario save(Usuario usuario) {
        validarDuplicidade(usuario);
        validarPapel(usuario);

        boolean edicao = usuario.getIdUsuario() != null;
        boolean senhaInformada = usuario.getSenhaUsuario() != null
                && !usuario.getSenhaUsuario().isBlank();

        if (edicao && !senhaInformada) {
            // Editando sem digitar senha nova: mantem o hash que ja esta gravado.
            // Sem isso a senha seria re-criptografada em cima do proprio hash e
            // o usuario nao conseguiria mais entrar.
            Usuario gravado = findById(usuario.getIdUsuario());
            if (gravado != null) {
                usuario.setSenhaUsuario(gravado.getSenhaUsuario());
            }
        } else {
            usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        }

        return usuarioRepository.save(usuario);
    }

    // Impede que um valor desconhecido seja gravado no campo de papel.
    private void validarPapel(Usuario usuario) {
        String papel = usuario.getRole();
        boolean papelValido = Usuario.PAPEL_ADMINISTRADOR.equals(papel)
                || Usuario.PAPEL_USUARIO_NORMAL.equals(papel);

        if (!papelValido) {
            usuario.setRole(Usuario.PAPEL_USUARIO_NORMAL);
        }
    }

    /*
     * Login e e-mail precisam ser unicos. Sem esta checagem o cadastro aceita
     * dois usuarios com o mesmo login, e a partir do segundo o login para de
     * funcionar: a busca por login devolve mais de uma linha e estoura
     * NonUniqueResultException, que a tela mostra como "usuario ou senha
     * invalidos" - sem pista nenhuma da causa real.
     */
    private void validarDuplicidade(Usuario usuario) {
        for (Usuario outro : usuarioRepository.findAllByLoginUsuario(usuario.getLoginUsuario())) {
            if (!outro.getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new IllegalArgumentException(
                        "Já existe um usuário com o login \"" + usuario.getLoginUsuario() + "\".");
            }
        }

        for (Usuario outro : usuarioRepository.findAllByEmailUsuario(usuario.getEmailUsuario())) {
            if (!outro.getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new IllegalArgumentException(
                        "Já existe um usuário com o e-mail \"" + usuario.getEmailUsuario() + "\".");
            }
        }
    }

    // Método para listar todos os alunos
    public List<Usuario> findAll(){
        return usuarioRepository.findAll(Sort.by("idUsuario"));
    }

    public Usuario findById(Integer id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id){
        usuarioRepository.deleteById(id);
    }
} 

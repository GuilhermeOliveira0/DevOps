package br.com.examplefatec.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.repository.AlunoRepository;

/**
 * Service de alunos.
 * Concentra as operacoes de gravar, listar, buscar e excluir antes de acessar o repository.
 */
@Service
public class AlunoService {

    private static final int MAX_NOME_LENGTH = 40;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_ENDERECO_LENGTH = 50;
    private static final int MAX_RA_LENGTH = 20;

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    /**
     * Grava um aluno novo ou editado no banco.
     */
    public Aluno save(Aluno aluno) {
        normalizarDados(aluno);
        validarDados(aluno);
        return alunoRepository.save(aluno);
    }

    private void normalizarDados(Aluno aluno) {
        aluno.setNomeAluno(ValidationSupport.trim(aluno.getNomeAluno()));
        aluno.setEmailAluno(ValidationSupport.lowerEmail(aluno.getEmailAluno()));
        aluno.setTelefoneAluno(ValidationSupport.onlyDigits(aluno.getTelefoneAluno()));
        aluno.setEnderecoAluno(ValidationSupport.trim(aluno.getEnderecoAluno()));
        aluno.setCpfAluno(ValidationSupport.onlyDigits(aluno.getCpfAluno()));

        if (aluno.getRaAluno() != null) {
            aluno.setRaAluno(ValidationSupport.trim(aluno.getRaAluno()).toUpperCase(Locale.ROOT));
        }
    }

    private void validarDados(Aluno aluno) {
        ValidationSupport.requireText(aluno.getNomeAluno(), "Informe o nome do aluno.");
        ValidationSupport.maxLength(aluno.getNomeAluno(), MAX_NOME_LENGTH, "O nome do aluno deve ter no maximo 40 caracteres.");
        ValidationSupport.lettersAndSpaces(aluno.getNomeAluno(), "O nome do aluno deve conter apenas letras e espacos.");

        ValidationSupport.requireText(aluno.getEmailAluno(), "Informe um email valido.");
        ValidationSupport.maxLength(aluno.getEmailAluno(), MAX_EMAIL_LENGTH, "O email do aluno deve ter no maximo 100 caracteres.");
        ValidationSupport.email(aluno.getEmailAluno(), "Informe um email valido.");

        ValidationSupport.exactDigits(aluno.getCpfAluno(), 11, "O CPF deve conter exatamente 11 numeros.");
        ValidationSupport.phoneDigits(aluno.getTelefoneAluno(), "O telefone deve conter 10 ou 11 numeros.");

        ValidationSupport.requireText(aluno.getEnderecoAluno(), "Informe o endereco do aluno.");
        ValidationSupport.maxLength(aluno.getEnderecoAluno(), MAX_ENDERECO_LENGTH, "O endereco deve ter no maximo 50 caracteres.");

        ValidationSupport.requireText(aluno.getRaAluno(), "Informe o RA do aluno.");
        ValidationSupport.maxLength(aluno.getRaAluno(), MAX_RA_LENGTH, "O RA deve ter no maximo 20 caracteres.");
        ValidationSupport.alphanumeric(aluno.getRaAluno(), "O RA deve conter apenas letras e numeros.");

        if (aluno.getCurso() == null || aluno.getCurso().getIdCurso() == null || aluno.getCurso().getIdCurso() <= 0) {
            throw new IllegalArgumentException("Selecione um curso.");
        }
    }

    /**
     * Retorna todos os alunos cadastrados.
     */
    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }

    /**
     * Exclui um aluno pelo id.
     */
    public void deleteById(int id) {
        alunoRepository.deleteById(id);
    }

    /**
     * Busca um aluno pelo id ou retorna null quando nao existe.
     */
    public Aluno findById(int id) {
        return alunoRepository.findById(id).orElse(null);
    }
}

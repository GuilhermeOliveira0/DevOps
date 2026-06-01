package br.com.examplefatec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.repository.ProfessorRepository;

/**
 * Service de professores.
 * Mantem as operacoes basicas de cadastro separadas do controller.
 */
@Service
public class ProfessorService {

    private static final int MAX_NOME_LENGTH = 40;
    private static final int MAX_GRADUACAO_LENGTH = 50;
    private static final int MAX_RM_LENGTH = 20;

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    /**
     * Grava um professor novo ou editado no banco.
     */
    public Professor save(Professor professor) {
        normalizarDados(professor);
        validarDados(professor);
        return professorRepository.save(professor);
    }

    private void normalizarDados(Professor professor) {
        professor.setNomeProfessor(ValidationSupport.trim(professor.getNomeProfessor()));
        professor.setTelefoneProfessor(ValidationSupport.onlyDigits(professor.getTelefoneProfessor()));
        professor.setGraduacaoProfessor(ValidationSupport.trim(professor.getGraduacaoProfessor()));
        professor.setRmProfessor(ValidationSupport.upperAlphanumeric(professor.getRmProfessor()));
    }

    private void validarDados(Professor professor) {
        ValidationSupport.requireText(professor.getNomeProfessor(), "Informe o nome do professor.");
        ValidationSupport.maxLength(professor.getNomeProfessor(), MAX_NOME_LENGTH, "O nome do professor deve ter no maximo 40 caracteres.");
        ValidationSupport.lettersAndSpaces(professor.getNomeProfessor(), "O nome do professor deve conter apenas letras e espacos.");

        ValidationSupport.phoneDigits(professor.getTelefoneProfessor(), "O telefone deve conter 10 ou 11 numeros.");

        ValidationSupport.requireText(professor.getGraduacaoProfessor(), "Informe a graduacao do professor.");
        ValidationSupport.maxLength(professor.getGraduacaoProfessor(), MAX_GRADUACAO_LENGTH, "A graduacao deve ter no maximo 50 caracteres.");
        ValidationSupport.lettersAndSpaces(professor.getGraduacaoProfessor(), "A graduacao deve conter apenas letras e espacos.");

        ValidationSupport.requireText(professor.getRmProfessor(), "Informe o RM do professor.");
        ValidationSupport.maxLength(professor.getRmProfessor(), MAX_RM_LENGTH, "O RM deve ter no maximo 20 caracteres.");
        ValidationSupport.alphanumeric(professor.getRmProfessor(), "O RM deve conter apenas letras e numeros.");
    }

    /**
     * Retorna todos os professores cadastrados.
     */
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    /**
     * Busca um professor pelo id ou retorna null quando nao existe.
     */
    public Professor findById(int id) {
        return professorRepository.findById(id).orElse(null);
    }

    /**
     * Exclui um professor pelo id.
     */
    public void deleteById(int id) {
        professorRepository.deleteById(id);
    }
}

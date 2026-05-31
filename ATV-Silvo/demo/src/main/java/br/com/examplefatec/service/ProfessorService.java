package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.repository.ProfessorRepository;

/**
 * Service de professores.
 * Mantem as operacoes basicas de cadastro separadas do controller.
 */
@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    /**
     * Grava um professor novo ou editado no banco.
     */
    public Professor save(Professor professor) {
        return professorRepository.save(professor);
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

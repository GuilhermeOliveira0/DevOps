package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Disciplina;
import br.com.examplefatec.repository.DisciplinaRepository;

/**
 * Service de disciplinas.
 * Fornece as operacoes de persistencia usadas pelo DisciplinaController.
 */
@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    /**
     * Grava uma disciplina nova ou editada no banco.
     */
    public Disciplina save(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    /**
     * Retorna todas as disciplinas cadastradas.
     */
    public List<Disciplina> findAll() {
        return disciplinaRepository.findAll();
    }

    /**
     * Busca uma disciplina pelo id ou retorna null quando nao existe.
     */
    public Disciplina findById(int id) {
        return disciplinaRepository.findById(id).orElse(null);
    }

    /**
     * Exclui uma disciplina pelo id.
     */
    public void deleteById(int id) {
        disciplinaRepository.deleteById(id);
    }
}

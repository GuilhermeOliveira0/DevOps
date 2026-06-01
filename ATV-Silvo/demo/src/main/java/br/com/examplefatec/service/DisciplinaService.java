package br.com.examplefatec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Disciplina;
import br.com.examplefatec.repository.DisciplinaRepository;

/**
 * Service de disciplinas.
 * Fornece as operacoes de persistencia usadas pelo DisciplinaController.
 */
@Service
public class DisciplinaService {

    private static final int MAX_NOME_LENGTH = 40;
    private static final int MAX_SIGLA_LENGTH = 20;

    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    /**
     * Grava uma disciplina nova ou editada no banco.
     */
    public Disciplina save(Disciplina disciplina) {
        normalizarDados(disciplina);
        validarDados(disciplina);
        return disciplinaRepository.save(disciplina);
    }

    private void normalizarDados(Disciplina disciplina) {
        disciplina.setNomeDisciplina(ValidationSupport.trim(disciplina.getNomeDisciplina()));
        disciplina.setSiglaDisciplina(ValidationSupport.upperAlphanumeric(disciplina.getSiglaDisciplina()));
    }

    private void validarDados(Disciplina disciplina) {
        ValidationSupport.requireText(disciplina.getNomeDisciplina(), "Informe o nome da disciplina.");
        ValidationSupport.maxLength(disciplina.getNomeDisciplina(), MAX_NOME_LENGTH, "O nome da disciplina deve ter no maximo 40 caracteres.");
        ValidationSupport.lettersAndSpaces(disciplina.getNomeDisciplina(), "O nome da disciplina deve conter apenas letras e espacos.");

        ValidationSupport.requireText(disciplina.getSiglaDisciplina(), "Informe a sigla da disciplina.");
        ValidationSupport.maxLength(disciplina.getSiglaDisciplina(), MAX_SIGLA_LENGTH, "A sigla deve ter no maximo 20 caracteres.");
        ValidationSupport.alphanumeric(disciplina.getSiglaDisciplina(), "A sigla da disciplina deve conter apenas letras e numeros.");

        ValidationSupport.positive(disciplina.getCargaHorariaDisciplina(), "A carga horaria da disciplina deve ser maior que zero.");

        if (disciplina.getCurso() == null || disciplina.getCurso().getIdCurso() == null || disciplina.getCurso().getIdCurso() <= 0) {
            throw new IllegalArgumentException("Selecione um curso.");
        }

        if (disciplina.getProfessor() == null || disciplina.getProfessor().getIdProfessor() == null || disciplina.getProfessor().getIdProfessor() <= 0) {
            throw new IllegalArgumentException("Selecione um professor.");
        }
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

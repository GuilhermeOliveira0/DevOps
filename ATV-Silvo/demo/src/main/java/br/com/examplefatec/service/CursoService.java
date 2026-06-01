package br.com.examplefatec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.repository.CursoRepository;

/**
 * Service de cursos.
 * Isola o controller do acesso direto ao banco feito pelo repository.
 */
@Service
public class CursoService {

    private static final int MAX_NOME_LENGTH = 40;
    private static final int MAX_PERIODO_LENGTH = 40;

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    /**
     * Grava um curso novo ou editado no banco.
     */
    public Curso save(Curso curso) {
        normalizarDados(curso);
        validarDados(curso);
        return cursoRepository.save(curso);
    }

    private void normalizarDados(Curso curso) {
        curso.setNomeCurso(ValidationSupport.trim(curso.getNomeCurso()));
        curso.setPeriodoCurso(ValidationSupport.trim(curso.getPeriodoCurso()));
    }

    private void validarDados(Curso curso) {
        ValidationSupport.requireText(curso.getNomeCurso(), "Informe o nome do curso.");
        ValidationSupport.maxLength(curso.getNomeCurso(), MAX_NOME_LENGTH, "O nome do curso deve ter no maximo 40 caracteres.");
        ValidationSupport.lettersAndSpaces(curso.getNomeCurso(), "O nome do curso deve conter apenas letras e espacos.");

        ValidationSupport.requireText(curso.getPeriodoCurso(), "Informe o periodo do curso.");
        ValidationSupport.maxLength(curso.getPeriodoCurso(), MAX_PERIODO_LENGTH, "O periodo deve ter no maximo 40 caracteres.");

        ValidationSupport.positive(curso.getCargaHorariaCurso(), "A carga horaria do curso deve ser maior que zero.");
    }

    /**
     * Retorna todos os cursos cadastrados.
     */
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    /**
     * Busca um curso pelo id ou retorna null quando nao existe.
     */
    public Curso findById(int id) {
        return cursoRepository.findById(id).orElse(null);
    }

    /**
     * Exclui um curso pelo id.
     */
    public void deleteById(int id) {
        cursoRepository.deleteById(id);
    }
}

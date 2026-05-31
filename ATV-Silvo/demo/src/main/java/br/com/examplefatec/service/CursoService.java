package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.repository.CursoRepository;

/**
 * Service de cursos.
 * Isola o controller do acesso direto ao banco feito pelo repository.
 */
@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Grava um curso novo ou editado no banco.
     */
    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
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

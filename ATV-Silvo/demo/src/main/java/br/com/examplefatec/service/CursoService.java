package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.repository.CursoRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
    }

    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    public Curso findById(int id) {
        return cursoRepository.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        cursoRepository.deleteById(id);
    }
}

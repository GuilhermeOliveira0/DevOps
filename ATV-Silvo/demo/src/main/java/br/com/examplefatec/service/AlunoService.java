package br.com.examplefatec.service;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.repository.AlunoRepository;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    public Aluno save(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }
}

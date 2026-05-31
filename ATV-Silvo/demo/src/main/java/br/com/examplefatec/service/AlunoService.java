package br.com.examplefatec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.repository.AlunoRepository;

/**
 * Service de alunos.
 * Concentra as operacoes de gravar, listar, buscar e excluir antes de acessar o repository.
 */
@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    /**
     * Grava um aluno novo ou editado no banco.
     */
    public Aluno save(Aluno aluno) {
        return alunoRepository.save(aluno);
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

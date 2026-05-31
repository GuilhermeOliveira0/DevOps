package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Aluno;

/**
 * Repository de alunos.
 * Herda do JpaRepository as operacoes basicas de CRUD usadas pelo service.
 */
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
}

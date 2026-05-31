package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Curso;

/**
 * Repository de cursos.
 * Herda do JpaRepository as consultas e operacoes basicas de persistencia.
 */
public interface CursoRepository extends JpaRepository<Curso, Integer> {
}

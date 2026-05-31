package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Professor;

/**
 * Repository de professores.
 * Usa JpaRepository para CRUD e consultas simples por id.
 */
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}

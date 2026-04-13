package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}

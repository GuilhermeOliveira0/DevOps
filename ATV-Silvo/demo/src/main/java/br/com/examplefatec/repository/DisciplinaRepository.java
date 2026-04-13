package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
}

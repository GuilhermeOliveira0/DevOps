package br.com.examplefatec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.examplefatec.entity.Disciplina;

/**
 * Repository de disciplinas.
 * Acesso ao banco para a entity Disciplina.
 */
public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
}

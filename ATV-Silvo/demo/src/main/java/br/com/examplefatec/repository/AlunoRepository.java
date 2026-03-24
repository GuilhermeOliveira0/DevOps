package br.com.examplefatec.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.examplefatec.entity.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
}

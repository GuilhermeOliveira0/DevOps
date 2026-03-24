package br.com.examplefatec.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.examplefatec.entity.Curso;


public interface CursoRepository extends JpaRepository<Curso, Integer> {
    
}

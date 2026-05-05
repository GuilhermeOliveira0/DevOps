package br.com.examplefatec.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor


public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO  )
    
    private Integer idCurso;

    @Column(nullable = false, length = 40)
    private String nomeCurso;

    @Column(nullable = false, length = 40)
    private String periodoCurso;
   
    @Column(nullable = false)
    private int cargaHorariaCurso;

    @OneToMany(mappedBy = "curso")
    private List<Disciplina> disciplinas;
}

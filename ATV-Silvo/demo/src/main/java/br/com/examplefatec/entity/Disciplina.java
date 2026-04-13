package br.com.examplefatec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idDisciplina;

    @Column(nullable = false, length = 20)
    private String siglaDisciplina;

    @Column(nullable = false, length = 40)
    private String nomeDisciplina;

    @Column(nullable = false)
    private int cargaHorariaDisciplina;
}

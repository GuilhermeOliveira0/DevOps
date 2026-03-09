package br.com.examplefatec.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO  )
    
    private int idAlunoo;

    @Column(nullable = false, length = 40)
    private String nomeAluno;

    @Column( length = 100)
    private String emailAluno;

    @Column(nullable = false, length = 11)
    private String telefoneAluno;

    @Column(nullable = false, length = 50)
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    private String cpfAluno;
    
    @Column(nullable = false)
    private String raAluno;


    
}

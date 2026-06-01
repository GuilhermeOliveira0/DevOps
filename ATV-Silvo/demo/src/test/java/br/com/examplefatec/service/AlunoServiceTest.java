package br.com.examplefatec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.repository.AlunoRepository;

class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    private AlunoService alunoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoService = new AlunoService(alunoRepository);
    }

    @Test
    void saveRejectsNameWithNumbers() {
        Aluno aluno = validAluno();
        aluno.setNomeAluno("Aluno 123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.save(aluno));

        assertEquals("O nome do aluno deve conter apenas letras e espacos.", exception.getMessage());
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void saveRejectsCpfWithInvalidLength() {
        Aluno aluno = validAluno();
        aluno.setCpfAluno("123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.save(aluno));

        assertEquals("O CPF deve conter exatamente 11 numeros.", exception.getMessage());
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void saveRejectsPhoneWithInvalidLength() {
        Aluno aluno = validAluno();
        aluno.setTelefoneAluno("12345");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> alunoService.save(aluno));

        assertEquals("O telefone deve conter 10 ou 11 numeros.", exception.getMessage());
        verify(alunoRepository, never()).save(any());
    }

    @Test
    void saveNormalizesAndPersistsValidAluno() {
        Aluno aluno = validAluno();
        aluno.setNomeAluno("  Maria Silva  ");
        aluno.setEmailAluno(" MARIA@TESTE.COM ");
        aluno.setCpfAluno("123.456.789-01");
        aluno.setTelefoneAluno("(11) 99999-8888");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluno saved = alunoService.save(aluno);

        assertEquals("Maria Silva", saved.getNomeAluno());
        assertEquals("maria@teste.com", saved.getEmailAluno());
        assertEquals("12345678901", saved.getCpfAluno());
        assertEquals("11999998888", saved.getTelefoneAluno());
        verify(alunoRepository).save(aluno);
    }

    private Aluno validAluno() {
        Aluno aluno = new Aluno();
        aluno.setNomeAluno("Maria Silva");
        aluno.setEmailAluno("maria@teste.com");
        aluno.setTelefoneAluno("11999998888");
        aluno.setEnderecoAluno("Rua Teste 123");
        aluno.setCpfAluno("12345678901");
        aluno.setRaAluno("RA12345");
        Curso curso = new Curso();
        curso.setIdCurso(1);
        aluno.setCurso(curso);
        return aluno;
    }
}

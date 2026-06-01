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

import br.com.examplefatec.entity.Disciplina;
import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.repository.DisciplinaRepository;

class DisciplinaServiceTest {

    @Mock
    private DisciplinaRepository disciplinaRepository;

    private DisciplinaService disciplinaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        disciplinaService = new DisciplinaService(disciplinaRepository);
    }

    @Test
    void saveRejectsNameWithNumbers() {
        Disciplina disciplina = validDisciplina();
        disciplina.setNomeDisciplina("Matematica 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> disciplinaService.save(disciplina));

        assertEquals("O nome da disciplina deve conter apenas letras e espacos.", exception.getMessage());
        verify(disciplinaRepository, never()).save(any());
    }

    @Test
    void saveRejectsInvalidSigla() {
        Disciplina disciplina = validDisciplina();
        disciplina.setSiglaDisciplina("BD@1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> disciplinaService.save(disciplina));

        assertEquals("A sigla da disciplina deve conter apenas letras e numeros.", exception.getMessage());
        verify(disciplinaRepository, never()).save(any());
    }

    @Test
    void saveRejectsZeroWorkload() {
        Disciplina disciplina = validDisciplina();
        disciplina.setCargaHorariaDisciplina(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> disciplinaService.save(disciplina));

        assertEquals("A carga horaria da disciplina deve ser maior que zero.", exception.getMessage());
        verify(disciplinaRepository, never()).save(any());
    }

    @Test
    void saveNormalizesAndPersistsValidDisciplina() {
        Disciplina disciplina = validDisciplina();
        disciplina.setNomeDisciplina("  Banco De Dados  ");
        disciplina.setSiglaDisciplina(" bd1 ");
        when(disciplinaRepository.save(any(Disciplina.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Disciplina saved = disciplinaService.save(disciplina);

        assertEquals("Banco De Dados", saved.getNomeDisciplina());
        assertEquals("BD1", saved.getSiglaDisciplina());
        verify(disciplinaRepository).save(disciplina);
    }

    private Disciplina validDisciplina() {
        Disciplina disciplina = new Disciplina();
        disciplina.setNomeDisciplina("Banco De Dados");
        disciplina.setSiglaDisciplina("BD1");
        disciplina.setCargaHorariaDisciplina(80);
        Curso curso = new Curso();
        curso.setIdCurso(1);
        Professor professor = new Professor();
        professor.setIdProfessor(1);
        disciplina.setCurso(curso);
        disciplina.setProfessor(professor);
        return disciplina;
    }
}

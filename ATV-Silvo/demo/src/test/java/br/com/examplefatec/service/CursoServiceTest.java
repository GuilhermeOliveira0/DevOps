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

import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.repository.CursoRepository;

class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    private CursoService cursoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cursoService = new CursoService(cursoRepository);
    }

    @Test
    void saveRejectsNameWithNumbers() {
        Curso curso = validCurso();
        curso.setNomeCurso("ADS 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cursoService.save(curso));

        assertEquals("O nome do curso deve conter apenas letras e espacos.", exception.getMessage());
        verify(cursoRepository, never()).save(any());
    }

    @Test
    void saveRejectsNegativeWorkload() {
        Curso curso = validCurso();
        curso.setCargaHorariaCurso(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cursoService.save(curso));

        assertEquals("A carga horaria do curso deve ser maior que zero.", exception.getMessage());
        verify(cursoRepository, never()).save(any());
    }

    @Test
    void saveNormalizesAndPersistsValidCurso() {
        Curso curso = validCurso();
        curso.setNomeCurso("  Analise De Sistemas  ");
        curso.setPeriodoCurso("  Noturno  ");
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Curso saved = cursoService.save(curso);

        assertEquals("Analise De Sistemas", saved.getNomeCurso());
        assertEquals("Noturno", saved.getPeriodoCurso());
        verify(cursoRepository).save(curso);
    }

    private Curso validCurso() {
        Curso curso = new Curso();
        curso.setNomeCurso("Analise De Sistemas");
        curso.setPeriodoCurso("Noturno");
        curso.setCargaHorariaCurso(2800);
        return curso;
    }
}

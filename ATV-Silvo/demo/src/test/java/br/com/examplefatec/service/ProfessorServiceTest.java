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

import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.repository.ProfessorRepository;

class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    private ProfessorService professorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        professorService = new ProfessorService(professorRepository);
    }

    @Test
    void saveRejectsNameWithNumbers() {
        Professor professor = validProfessor();
        professor.setNomeProfessor("Professor 1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> professorService.save(professor));

        assertEquals("O nome do professor deve conter apenas letras e espacos.", exception.getMessage());
        verify(professorRepository, never()).save(any());
    }

    @Test
    void saveRejectsInvalidPhone() {
        Professor professor = validProfessor();
        professor.setTelefoneProfessor("abc");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> professorService.save(professor));

        assertEquals("O telefone deve conter 10 ou 11 numeros.", exception.getMessage());
        verify(professorRepository, never()).save(any());
    }

    @Test
    void saveNormalizesAndPersistsValidProfessor() {
        Professor professor = validProfessor();
        professor.setNomeProfessor("  Joao Silva  ");
        professor.setTelefoneProfessor("(11) 98888-7777");
        professor.setRmProfessor(" rm123 ");
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Professor saved = professorService.save(professor);

        assertEquals("Joao Silva", saved.getNomeProfessor());
        assertEquals("11988887777", saved.getTelefoneProfessor());
        assertEquals("RM123", saved.getRmProfessor());
        verify(professorRepository).save(professor);
    }

    private Professor validProfessor() {
        Professor professor = new Professor();
        professor.setNomeProfessor("Joao Silva");
        professor.setTelefoneProfessor("11988887777");
        professor.setGraduacaoProfessor("Mestrado");
        professor.setRmProfessor("RM123");
        return professor;
    }
}

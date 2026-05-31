package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.entity.Disciplina;
import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.service.CursoService;
import br.com.examplefatec.service.DisciplinaService;
import br.com.examplefatec.service.ProfessorService;

/**
 * Controller responsavel pelas telas e acoes de disciplinas.
 * Tambem carrega cursos e professores para montar os relacionamentos do formulario.
 */
@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ProfessorService professorService;

    /**
     * Salva uma disciplina criada ou editada.
     * Antes de gravar, troca os ids recebidos no formulario pelas entidades Curso e Professor.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Disciplina disciplina) {
        // O formulario envia referencias parciais; o JPA precisa das entidades gerenciadas.
        if (disciplina.getCurso() != null && disciplina.getCurso().getIdCurso() != null) {
            Curso cursoSelecionado = cursoService.findById(disciplina.getCurso().getIdCurso());
            disciplina.setCurso(cursoSelecionado);
        }

        if (disciplina.getProfessor() != null && disciplina.getProfessor().getIdProfessor() != null) {
            Professor professorSelecionado = professorService.findById(disciplina.getProfessor().getIdProfessor());
            disciplina.setProfessor(professorSelecionado);
        }

        disciplinaService.save(disciplina);
        return "redirect:/disciplinas/listar";
    }

    /**
     * Consulta todas as disciplinas e retorna a pagina de listagem.
     */
    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "disciplina/listar";
    }

    /**
     * Abre o formulario de nova disciplina com listas de cursos e professores.
     */
    @GetMapping("/criar")
    public String criar(Model model) {
        Disciplina disciplina = new Disciplina();
        disciplina.setCurso(new Curso());
        disciplina.setProfessor(new Professor());
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("cursos", cursoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        return "disciplina/formularioDisciplina";
    }

    /**
     * Abre o formulario preenchido para editar uma disciplina existente.
     * Garante objetos vazios para curso/professor quando ainda nao ha relacionamento.
     */
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        Disciplina disciplina = disciplinaService.findById(id);
        if (disciplina.getCurso() == null) {
            disciplina.setCurso(new Curso());
        }
        if (disciplina.getProfessor() == null) {
            disciplina.setProfessor(new Professor());
        }
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("cursos", cursoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        return "disciplina/formularioDisciplina";
    }

    /**
     * Exclui uma disciplina pelo id informado na rota.
     * Este metodo altera dados no banco.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        disciplinaService.deleteById(id);
        return "redirect:/disciplinas/listar";
    }
}

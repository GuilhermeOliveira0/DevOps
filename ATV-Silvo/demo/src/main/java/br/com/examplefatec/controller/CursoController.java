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
import br.com.examplefatec.service.CursoService;

/**
 * Controller responsavel pelas telas e acoes de cursos.
 * Mantem o controller simples e delega regras de persistencia ao CursoService.
 */
@Controller
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    /**
     * Salva um curso vindo do formulario e redireciona para a listagem.
     * Este metodo altera dados no banco.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Curso curso) {
        cursoService.save(curso);
        return "redirect:/cursos/listar";
    }

    /**
     * Consulta todos os cursos cadastrados e retorna a pagina de listagem.
     */
    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.findAll());
        return "curso/listar";
    }

    /**
     * Abre o formulario para cadastrar um novo curso.
     */
    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("curso", new Curso());
        return "curso/formularioCurso";
    }

    /**
     * Abre o formulario preenchido para editar um curso existente.
     */
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        Curso curso = cursoService.findById(id);
        model.addAttribute("curso", curso);
        return "curso/formularioCurso";
    }

    /**
     * Exclui um curso pelo id informado na rota.
     * Este metodo altera dados no banco.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        cursoService.deleteById(id);
        return "redirect:/cursos/listar";
    }
}

package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Professor;
import br.com.examplefatec.service.ProfessorService;

@Controller
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Professor professor) {
        professorService.save(professor);
        return "redirect:/professores/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("professores", professorService.findAll());
        return "professor/listar";
    }

    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor/formularioProfessor";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        Professor professor = professorService.findById(id);
        model.addAttribute("professor", professor);
        return "professor/formularioProfessor";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        professorService.deleteById(id);
        return "redirect:/professores/listar";
    }
}

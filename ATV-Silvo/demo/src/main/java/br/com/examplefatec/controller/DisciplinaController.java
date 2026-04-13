package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Disciplina;
import br.com.examplefatec.service.DisciplinaService;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Disciplina disciplina) {
        disciplinaService.save(disciplina);
        return "redirect:/disciplinas/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("disciplinas", disciplinaService.findAll());
        return "disciplina/listar";
    }

    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        return "disciplina/formularioDisciplina";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        Disciplina disciplina = disciplinaService.findById(id);
        model.addAttribute("disciplina", disciplina);
        return "disciplina/formularioDisciplina";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        disciplinaService.deleteById(id);
        return "redirect:/disciplinas/listar";
    }
}

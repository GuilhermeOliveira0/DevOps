package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.service.AlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {
    @Autowired
    private AlunoService alunoService;

 
 
    // metodo para salvar aluno.
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }

    //metodo para listar todos os alunos.
    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        return "alunos/listar";
    }


}

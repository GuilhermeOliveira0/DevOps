package br.com.examplefatec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.examplefatec.entity.Aluno;
import br.com.examplefatec.entity.Curso;
import br.com.examplefatec.service.AlunoService;
import br.com.examplefatec.service.CursoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;
    @Autowired
    private CursoService cursoService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        aluno.setCpfAluno(onlyDigits(aluno.getCpfAluno()));
        aluno.setTelefoneAluno(onlyDigits(aluno.getTelefoneAluno()));

        if (aluno.getCurso() != null && aluno.getCurso().getIdCurso() != null && aluno.getCurso().getIdCurso() > 0) {
            Curso cursoSelecionado = cursoService.findById(aluno.getCurso().getIdCurso());
            aluno.setCurso(cursoSelecionado);
        } else {
            aluno.setCurso(null);
        }
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listar";
    }
//criando o método para exibir o formulário de criação de aluno
    @GetMapping("/criar")
    public String criar(Model model) {
        Aluno aluno = new Aluno();
        aluno.setCurso(new Curso());
        model.addAttribute("aluno", aluno);
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        alunoService.deleteById(id);
        return "redirect:/alunos/listar";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable int id, Model model) {
        Aluno aluno = alunoService.findById(id);
        if (aluno == null) {
            return "redirect:/alunos/listar";
        }
        if (aluno.getCurso() == null) {
            aluno.setCurso(new Curso());
        }
        model.addAttribute("aluno", aluno);
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";
    }

    private String onlyDigits(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }
}

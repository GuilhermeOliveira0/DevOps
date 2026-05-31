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

/**
 * Controller responsavel pelas telas e acoes de alunos.
 * Recebe dados dos formularios Thymeleaf, consulta cursos quando necessario
 * e delega gravacao, listagem e exclusao para os services.
 */
@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private CursoService cursoService;

    /**
     * Salva um aluno enviado pelo formulario de criacao ou edicao.
     * Normaliza CPF/telefone, resolve o curso selecionado e altera dados no banco.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        aluno.setCpfAluno(onlyDigits(aluno.getCpfAluno()));
        aluno.setTelefoneAluno(onlyDigits(aluno.getTelefoneAluno()));

        // O formulario envia apenas o id do curso; aqui buscamos a entidade real antes de salvar.
        if (aluno.getCurso() != null && aluno.getCurso().getIdCurso() != null && aluno.getCurso().getIdCurso() > 0) {
            Curso cursoSelecionado = cursoService.findById(aluno.getCurso().getIdCurso());
            aluno.setCurso(cursoSelecionado);
        } else {
            aluno.setCurso(null);
        }

        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }

    /**
     * Lista todos os alunos cadastrados.
     * Apenas consulta o banco e retorna a pagina Thymeleaf de listagem.
     */
    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listar";
    }

    /**
     * Abre o formulario para cadastrar um novo aluno.
     * Tambem carrega a lista de cursos para o campo de selecao.
     */
    @GetMapping("/criar")
    public String criar(Model model) {
        Aluno aluno = new Aluno();
        aluno.setCurso(new Curso());
        model.addAttribute("aluno", aluno);
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";
    }

    /**
     * Exclui um aluno pelo id informado na rota e redireciona para a listagem.
     * Este metodo altera dados no banco.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable int id) {
        alunoService.deleteById(id);
        return "redirect:/alunos/listar";
    }

    /**
     * Abre o formulario preenchido para editar um aluno existente.
     * Consulta aluno e cursos, sem alterar dados no banco.
     */
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

    /**
     * Mantem apenas numeros em campos como CPF e telefone antes da persistencia.
     */
    private String onlyDigits(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }
}

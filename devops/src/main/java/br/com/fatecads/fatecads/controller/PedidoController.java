package br.com.fatecads.fatecads.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProdutoService produtoService;

    // API JSON que ja existia
    @PostMapping
    @ResponseBody
    public Pedido salvarPedido(@RequestBody Pedido pedido) {
        return pedidoService.salvarPedido(pedido);
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        return "pedido/listarPedido";
    }

    // Método para abrir a tela de cadastro de pedidos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        List<Aluno> alunos = alunoService.findAll();
        model.addAttribute("alunos", alunos);
        return "pedido/formularioPedido";
    }

    // Cria o pedido vazio e manda direto para a tela onde os itens sao lancados
    @PostMapping("/abrir")
    public String abrir(@RequestParam Integer idAluno, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.abrirPedido(idAluno);
            return "redirect:/pedidos/editar/" + pedido.getIdPedido();
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos/criar";
        }
    }

    /*
     * Tela de edicao do pedido: lista os itens ja lancados com a quantidade
     * em campo aberto, e traz o formulario para incluir mais um item.
     */
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            redirect.addFlashAttribute("erro", "Pedido " + id + " não encontrado.");
            return "redirect:/pedidos/listar";
        }

        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("pedido", pedido);
        model.addAttribute("produtos", produtos);
        return "pedido/editarPedido";
    }

    // Grava as quantidades alteradas na tela e recalcula subtotais e total
    @PostMapping("/{id}/quantidades")
    public String atualizarQuantidades(@PathVariable Integer id,
                                       @RequestParam(value = "idItem", required = false) List<Integer> idItens,
                                       @RequestParam(value = "quantidade", required = false) List<Integer> quantidades,
                                       RedirectAttributes redirect) {
        try {
            pedidoService.atualizarQuantidades(id, idItens, quantidades);
            redirect.addFlashAttribute("mensagem", "Quantidades atualizadas.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/editar/" + id;
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(@PathVariable Integer id,
                                @RequestParam Integer idProduto,
                                @RequestParam Integer quantidade,
                                RedirectAttributes redirect) {
        try {
            pedidoService.adicionarItem(id, idProduto, quantidade);
            redirect.addFlashAttribute("mensagem", "Item adicionado.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/editar/" + id;
    }

    @GetMapping("/{id}/itens/{idItem}/excluir")
    public String removerItem(@PathVariable Integer id,
                              @PathVariable Integer idItem,
                              RedirectAttributes redirect) {
        try {
            pedidoService.removerItem(id, idItem);
            redirect.addFlashAttribute("mensagem", "Item removido.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/editar/" + id;
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        pedidoService.deleteById(id);
        return "redirect:/pedidos/listar";
    }
    
}

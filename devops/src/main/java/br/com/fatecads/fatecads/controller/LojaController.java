package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.model.Carrinho;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/loja")
public class LojaController {

    private static final String NOME_CARRINHO_NA_SESSAO = "carrinho";

    @Autowired
    private ProdutoService produtoService;

    // Mostra todos os produtos cadastrados na pagina de compras.
    @GetMapping
    public String listarProdutos(Model model, HttpSession sessao) {
        model.addAttribute("produtos", produtoService.findAll());
        model.addAttribute("carrinho", buscarCarrinho(sessao));
        return "loja/listarProdutos";
    }

    // Mostra os produtos que o usuario ja colocou no carrinho.
    @GetMapping("/carrinho")
    public String mostrarCarrinho(Model model, HttpSession sessao) {
        model.addAttribute("carrinho", buscarCarrinho(sessao));
        return "loja/carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarAoCarrinho(@RequestParam Integer idProduto,
                                      @RequestParam(defaultValue = "1") Integer quantidade,
                                      HttpSession sessao,
                                      RedirectAttributes redirect) {
        Produto produto = produtoService.findById(idProduto);

        if (produto == null) {
            redirect.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/loja";
        }

        if (quantidade == null || quantidade < 1) {
            redirect.addFlashAttribute("erro", "A quantidade precisa ser no mínimo 1.");
            return "redirect:/loja";
        }

        Carrinho carrinho = buscarCarrinho(sessao);
        carrinho.adicionarProduto(produto, quantidade);
        redirect.addFlashAttribute("mensagem", "Produto adicionado ao carrinho.");
        return "redirect:/loja";
    }

    @PostMapping("/carrinho/quantidade")
    public String alterarQuantidade(@RequestParam Integer idProduto,
                                    @RequestParam Integer quantidade,
                                    HttpSession sessao,
                                    RedirectAttributes redirect) {
        if (quantidade == null || quantidade < 1) {
            redirect.addFlashAttribute("erro", "A quantidade precisa ser no mínimo 1.");
            return "redirect:/loja/carrinho";
        }

        buscarCarrinho(sessao).alterarQuantidade(idProduto, quantidade);
        redirect.addFlashAttribute("mensagem", "Quantidade atualizada.");
        return "redirect:/loja/carrinho";
    }

    @GetMapping("/carrinho/remover/{idProduto}")
    public String removerDoCarrinho(@PathVariable Integer idProduto,
                                    HttpSession sessao,
                                    RedirectAttributes redirect) {
        buscarCarrinho(sessao).removerProduto(idProduto);
        redirect.addFlashAttribute("mensagem", "Produto removido do carrinho.");
        return "redirect:/loja/carrinho";
    }

    /*
     * Na primeira chamada cria o carrinho. Nas proximas chamadas devolve o
     * mesmo objeto, que ficou guardado na sessao daquele usuario.
     */
    private Carrinho buscarCarrinho(HttpSession sessao) {
        Carrinho carrinho = (Carrinho) sessao.getAttribute(NOME_CARRINHO_NA_SESSAO);

        if (carrinho == null) {
            carrinho = new Carrinho();
            sessao.setAttribute(NOME_CARRINHO_NA_SESSAO, carrinho);
        }

        return carrinho;
    }
}

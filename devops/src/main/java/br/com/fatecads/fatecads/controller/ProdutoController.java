package br.com.fatecads.fatecads.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Método para listar todos os produtos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "produto/listarProdutos";
    }

    // Método para abrir o formulário de criação de produtos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }
    
    // Método para salvar o produto no banco de dados, junto com a imagem
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto,
                         @RequestParam(value = "arquivoFoto", required = false) MultipartFile arquivoFoto,
                         @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
                         RedirectAttributes redirect) {
        try {
            produtoService.save(produto, arquivoFoto, removerFoto);
        } catch (IOException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return produto.getIdProduto() == null
                    ? "redirect:/produtos/criar"
                    : "redirect:/produtos/editar/" + produto.getIdProduto();
        }
        return "redirect:/produtos/listar";
    }
    
    // Método para abrir o formulário de edição de produtos
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        Produto produto = produtoService.findById(id);
        if (produto == null) {
            redirect.addFlashAttribute("erro", "Produto " + id + " não encontrado.");
            return "redirect:/produtos/listar";
        }
        model.addAttribute("produto", produto);
        return "produto/formularioProduto";
    }

    /*
     * Devolve a imagem gravada na coluna bytea. E este endereco que o
     * <img src="..."> das telas de produto consome.
     */
    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> foto(@PathVariable Integer id) {
        Produto produto = produtoService.findById(id);

        if (produto == null || !produto.isTemFoto()) {
            return ResponseEntity.notFound().build();
        }

        MediaType tipo = MediaType.IMAGE_JPEG;
        if (produto.getTipoFotoProduto() != null) {
            try {
                tipo = MediaType.parseMediaType(produto.getTipoFotoProduto());
            } catch (Exception ignorado) {
                // tipo gravado invalido: cai no JPEG do padrao
            }
        }

        return ResponseEntity.ok().contentType(tipo).body(produto.getFotoProduto());
    }
    
    /*
     * Grava o valor alterado direto na listagem (duplo clique na celula).
     * Responde JSON porque quem chama e o fetch da pagina, nao um formulario.
     */
    @PostMapping("/{id}/valor")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> atualizarValor(
            @PathVariable Integer id,
            @RequestParam("valorProduto") Double valorProduto) {

        Map<String, Object> resposta = new LinkedHashMap<>();

        try {
            Produto produto = produtoService.atualizarValor(id, valorProduto);
            resposta.put("ok", true);
            resposta.put("valor", produto.getValorProduto());
            resposta.put("textoFormatado", formatarMoeda(produto.getValorProduto()));
            return ResponseEntity.ok(resposta);

        } catch (IllegalArgumentException e) {
            resposta.put("ok", false);
            resposta.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(resposta);
        }
    }

    // Mesmo formato que o Thymeleaf usa na tabela, para o texto nao "pular"
    // de aparencia depois de gravar pela edicao no quadro.
    private String formatarMoeda(Double valor) {
        double v = (valor == null) ? 0.0 : valor;
        return "R$ " + String.format(new Locale("pt", "BR"), "%,.2f", v);
    }

    // Método para excluir um produto pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        produtoService.deleteById(id);
        return "redirect:/produtos/listar";
    }
    
    
}

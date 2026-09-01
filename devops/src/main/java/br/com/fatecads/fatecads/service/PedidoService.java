package br.com.fatecads.fatecads.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    // Método para criar um pedido
    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        pedido.setDataPedido(LocalDate.now());
        if (pedido.getItens() == null) {
            pedido.setItens(new ArrayList<>());
        }
        for (ItemDoPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            item.setProduto(produto);
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            item.setPedido(pedido);
        }
        pedido.atualizarTotal();
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return pedidoRepository.findAll(Sort.by("idPedido"));
    }

    @Transactional(readOnly = true)
    public Pedido findById(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    // Abre um pedido vazio para o aluno; os itens entram depois, na tela de edicao
    @Transactional
    public Pedido abrirPedido(Integer idAluno) {
        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setAluno(aluno);
        pedido.setDataPedido(LocalDate.now());
        pedido.setItens(new ArrayList<>());
        pedido.setTotalPedido(0.0);

        return pedidoRepository.save(pedido);
    }

    /*
     * Se o produto ja estiver no pedido, soma na quantidade em vez de criar
     * uma segunda linha para o mesmo item.
     */
    @Transactional
    public void adicionarItem(Integer idPedido, Integer idProduto, Integer quantidade) {
        if (quantidade == null || quantidade < 1) {
            throw new IllegalArgumentException("A quantidade precisa ser no mínimo 1");
        }

        Pedido pedido = buscarObrigatorio(idPedido);
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        ItemDoPedido existente = null;
        for (ItemDoPedido item : pedido.getItens()) {
            if (item.getProduto() != null
                    && item.getProduto().getIdProduto().equals(idProduto)) {
                existente = item;
                break;
            }
        }

        if (existente != null) {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
            existente.atualizarSubtotal();
        } else {
            ItemDoPedido item = new ItemDoPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            pedido.getItens().add(item);
        }

        pedido.atualizarTotal();
        pedidoRepository.save(pedido);
    }

    /*
     * Recebe os ids dos itens e as quantidades novas na mesma ordem em que a
     * tela mandou, regrava cada subtotal e recalcula o total do pedido.
     * O preco unitario nao muda: fica o que valia quando o item foi lancado.
     */
    @Transactional
    public void atualizarQuantidades(Integer idPedido, List<Integer> idItens, List<Integer> quantidades) {
        Pedido pedido = buscarObrigatorio(idPedido);

        if (idItens == null || quantidades == null || idItens.size() != quantidades.size()) {
            throw new IllegalArgumentException("Itens e quantidades não conferem");
        }

        for (int i = 0; i < idItens.size(); i++) {
            Integer idItem = idItens.get(i);
            Integer novaQuantidade = quantidades.get(i);

            if (novaQuantidade == null || novaQuantidade < 1) {
                throw new IllegalArgumentException("A quantidade precisa ser no mínimo 1");
            }

            for (ItemDoPedido item : pedido.getItens()) {
                if (item.getIdItem().equals(idItem)) {
                    item.setQuantidade(novaQuantidade);
                    item.atualizarSubtotal();
                    break;
                }
            }
        }

        pedido.atualizarTotal();
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void removerItem(Integer idPedido, Integer idItem) {
        Pedido pedido = buscarObrigatorio(idPedido);
        pedido.getItens().removeIf(item -> item.getIdItem().equals(idItem));
        pedido.atualizarTotal();
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void deleteById(Integer id) {
        pedidoRepository.deleteById(id);
    }

    private Pedido buscarObrigatorio(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
    }
}

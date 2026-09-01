package br.com.fatecads.fatecads.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class ProdutoService {

    // Tipos de imagem aceitos no cadastro
    private static final List<String> TIPOS_ACEITOS =
            List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @Autowired
    private ProdutoRepository produtoRepository;

    // Método para salvar um produto
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    /*
     * Salva o produto tratando a imagem:
     * - arquivo novo enviado  -> grava os bytes no Postgres
     * - marcou "remover"      -> limpa a imagem
     * - nenhum dos dois       -> mantem a imagem que ja estava gravada
     *   (sem isso, editar o produto apagaria a foto)
     */
    public Produto save(Produto produto, MultipartFile arquivoFoto, boolean removerFoto)
            throws IOException {

        if (arquivoFoto != null && !arquivoFoto.isEmpty()) {
            validarImagem(arquivoFoto);
            produto.setFotoProduto(arquivoFoto.getBytes());
            produto.setTipoFotoProduto(arquivoFoto.getContentType());
            produto.setNomeFotoProduto(arquivoFoto.getOriginalFilename());

        } else if (removerFoto) {
            produto.setFotoProduto(null);
            produto.setTipoFotoProduto(null);
            produto.setNomeFotoProduto(null);

        } else if (produto.getIdProduto() != null) {
            Produto gravado = findById(produto.getIdProduto());
            if (gravado != null) {
                produto.setFotoProduto(gravado.getFotoProduto());
                produto.setTipoFotoProduto(gravado.getTipoFotoProduto());
                produto.setNomeFotoProduto(gravado.getNomeFotoProduto());
            }
        }

        return produtoRepository.save(produto);
    }

    private void validarImagem(MultipartFile arquivo) throws IOException {
        String tipo = arquivo.getContentType();
        if (tipo == null || !TIPOS_ACEITOS.contains(tipo.toLowerCase())) {
            throw new IOException("Formato nao suportado. Envie JPG, PNG, GIF ou WEBP.");
        }
    }

    /*
     * Usado pela edicao no proprio quadro da listagem. Mexe so no valor,
     * sem passar pelo formulario inteiro - assim a foto e os demais campos
     * ficam intocados.
     */
    public Produto atualizarValor(Integer id, Double novoValor) {
        if (novoValor == null || novoValor < 0) {
            throw new IllegalArgumentException("O valor precisa ser zero ou maior.");
        }

        Produto produto = findById(id);
        if (produto == null) {
            throw new IllegalArgumentException("Produto " + id + " não encontrado.");
        }

        produto.setValorProduto(Math.round(novoValor * 100.0) / 100.0);
        return produtoRepository.save(produto);
    }

    // Método para listar todos os produtos
    public List<Produto> findAll() {
        return produtoRepository.findAll(Sort.by("idProduto"));
    }

    // Método para buscar todos os produtos
    public Produto findById(Integer id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        produtoRepository.deleteById(id);
    }
}

package br.com.examplefatec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller da pagina publica inicial do sistema.
 * Mostra a tela de apresentacao/login sem exigir autenticacao.
 */
@Controller
public class FatecAdsController {

    /**
     * Atende a raiz do site e o caminho /fatecads, retornando a view index.
     */
    @GetMapping({"/", "/fatecads"})
    public String index() {
        return "index";
    }
    
}

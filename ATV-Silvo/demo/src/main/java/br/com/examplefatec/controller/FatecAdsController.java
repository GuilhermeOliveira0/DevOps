package br.com.examplefatec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FatecAdsController {

    @GetMapping({"/", "/fatecads"})
    public String index() {
        return "aluno/index";
    }
    
}

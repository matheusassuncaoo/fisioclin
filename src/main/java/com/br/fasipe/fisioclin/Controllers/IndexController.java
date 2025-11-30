package com.br.fasipe.fisioclin.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para servir a página inicial (index.html)
 * Redireciona "/" para "/frontend/index.html" onde os assets estão corretamente referenciados
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        // Redireciona para /frontend/index.html onde os assets (CSS, JS) estão corretamente referenciados
        return "redirect:/frontend/index.html";
    }
}


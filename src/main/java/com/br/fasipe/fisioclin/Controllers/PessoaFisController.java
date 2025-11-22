package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import com.br.fasipe.fisioclin.Services.PessoaFisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pessoasfis")
@CrossOrigin(origins = "*")
public class PessoaFisController {

    @Autowired
    private PessoaFisService pessoaFisService;

    @GetMapping
    public ResponseEntity<List<PessoaFis>> listarTodos() {
        return ResponseEntity.ok(pessoaFisService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaFis> buscarPorId(@PathVariable Integer id) {
        return pessoaFisService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PessoaFis> criar(@RequestBody PessoaFis pessoaFis) {
        PessoaFis novaPessoaFis = pessoaFisService.salvar(pessoaFis);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoaFis);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaFis> atualizar(@PathVariable Integer id, @RequestBody PessoaFis pessoaFis) {
        try {
            PessoaFis pessoaFisAtualizada = pessoaFisService.atualizar(id, pessoaFis);
            return ResponseEntity.ok(pessoaFisAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        pessoaFisService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

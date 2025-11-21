package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.AtendiFisio;
import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.Services.AtendiFisioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@CrossOrigin(origins = "*")
public class AtendiFisioController {
    
    @Autowired
    private AtendiFisioService atendiFisioService;
    
    @GetMapping
    public ResponseEntity<List<AtendiFisio>> listarTodos() {
        List<AtendiFisio> atendimentos = atendiFisioService.listarTodos();
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AtendiFisio> buscarPorId(@PathVariable Integer id) {
        return atendiFisioService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<AtendiFisio>> buscarPorPaciente(@PathVariable Integer idPaciente) {
        List<AtendiFisio> atendimentos = atendiFisioService.buscarPorPaciente(idPaciente);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/profissional/{idProfissio}")
    public ResponseEntity<List<AtendiFisio>> buscarPorProfissional(@PathVariable Integer idProfissio) {
        List<AtendiFisio> atendimentos = atendiFisioService.buscarPorProfissional(idProfissio);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<AtendiFisio>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<AtendiFisio> atendimentos = atendiFisioService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/evolucao/{idPaciente}")
    public ResponseEntity<List<AtendiFisio>> buscarEvolucao(
            @PathVariable Integer idPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<AtendiFisio> evolucao = atendiFisioService.buscarEvolucaoPaciente(idPaciente, dataInicio, dataFim);
            return ResponseEntity.ok(evolucao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/paciente/{idPaciente}/ultimo")
    public ResponseEntity<AtendiFisio> buscarUltimoAtendimento(@PathVariable Integer idPaciente) {
        AtendiFisio ultimo = atendiFisioService.buscarUltimoAtendimento(idPaciente);
        if (ultimo != null) {
            return ResponseEntity.ok(ultimo);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/paciente/{idPaciente}/count")
    public ResponseEntity<Long> contarAtendimentos(@PathVariable Integer idPaciente) {
        Long count = atendiFisioService.contarAtendimentos(idPaciente);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<AtendiFisio> criar(@RequestBody AtendiFisio atendimento) {
        try {
            AtendiFisio novoAtendimento = atendiFisioService.salvar(atendimento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/soap")
    public ResponseEntity<AtendiFisio> criarComSOAP(@RequestBody AtendimentoSOAPDTO dto) {
        try {
            AtendiFisio novoAtendimento = atendiFisioService.criarComSOAP(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AtendiFisio> atualizar(@PathVariable Integer id, @RequestBody AtendiFisio atendimento) {
        try {
            AtendiFisio atendimentoAtualizado = atendiFisioService.atualizar(id, atendimento);
            return ResponseEntity.ok(atendimentoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            atendiFisioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

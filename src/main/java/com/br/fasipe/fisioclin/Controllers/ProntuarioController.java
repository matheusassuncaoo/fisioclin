package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.Prontuario;
import com.br.fasipe.fisioclin.Services.ProntuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prontuarios")
@CrossOrigin(origins = "*")
public class ProntuarioController {
    
    @Autowired
    private ProntuarioService prontuarioService;
    
    @GetMapping
    public ResponseEntity<List<Prontuario>> listarTodos() {
        List<Prontuario> prontuarios = prontuarioService.listarTodos();
        return ResponseEntity.ok(prontuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Prontuario> buscarPorId(@PathVariable Integer id) {
        return prontuarioService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/paciente/{idPaciente}/evolucao")
    public ResponseEntity<List<Prontuario>> buscarEvolucaoPaciente(@PathVariable Integer idPaciente) {
        List<Prontuario> evolucao = prontuarioService.buscarEvolucaoPaciente(idPaciente);
        return ResponseEntity.ok(evolucao);
    }
    
    @GetMapping("/paciente/{idPaciente}/periodo")
    public ResponseEntity<List<Prontuario>> buscarEvolucaoPorPeriodo(
            @PathVariable Integer idPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<Prontuario> prontuarios = prontuarioService.buscarEvolucaoPorPeriodo(idPaciente, dataInicio, dataFim);
            return ResponseEntity.ok(prontuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/profissional/{idProfissio}")
    public ResponseEntity<List<Prontuario>> buscarPorProfissional(@PathVariable Integer idProfissio) {
        List<Prontuario> prontuarios = prontuarioService.buscarPorProfissional(idProfissio);
        return ResponseEntity.ok(prontuarios);
    }
    
    @GetMapping("/especialidade/{idEspec}")
    public ResponseEntity<List<Prontuario>> buscarPorEspecialidade(@PathVariable Integer idEspec) {
        List<Prontuario> prontuarios = prontuarioService.buscarPorEspecialidade(idEspec);
        return ResponseEntity.ok(prontuarios);
    }
    
    @GetMapping("/paciente/{idPaciente}/ultimo")
    public ResponseEntity<Prontuario> buscarUltimoProntuario(@PathVariable Integer idPaciente) {
        Prontuario ultimo = prontuarioService.buscarUltimoProntuario(idPaciente);
        if (ultimo != null) {
            return ResponseEntity.ok(ultimo);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/paciente/{idPaciente}/visiveis")
    public ResponseEntity<List<Prontuario>> buscarProntuariosVisiveis(@PathVariable Integer idPaciente) {
        List<Prontuario> prontuarios = prontuarioService.buscarProntuariosVisiveis(idPaciente);
        return ResponseEntity.ok(prontuarios);
    }
    
    @GetMapping("/paciente/{idPaciente}/count")
    public ResponseEntity<Long> contarProntuarios(@PathVariable Integer idPaciente) {
        Long count = prontuarioService.contarProntuarios(idPaciente);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<Prontuario> criar(@RequestBody Prontuario prontuario) {
        try {
            Prontuario novoProntuario = prontuarioService.salvar(prontuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProntuario);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Prontuario> atualizar(@PathVariable Integer id, @RequestBody Prontuario prontuario) {
        try {
            Prontuario atualizado = prontuarioService.atualizar(id, prontuario);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            prontuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

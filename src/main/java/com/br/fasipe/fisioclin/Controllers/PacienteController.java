package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.Paciente;
import com.br.fasipe.fisioclin.Services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        List<Paciente> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Paciente>> listarAtivos() {
        List<Paciente> pacientes = pacienteService.listarAtivos();
        return ResponseEntity.ok(pacientes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Integer id) {
        return pacienteService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/rg/{rg}")
    public ResponseEntity<Paciente> buscarPorRg(@PathVariable String rg) {
        return pacienteService.buscarPorRg(rg)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/pessoa/{idPessoaFis}")
    public ResponseEntity<Paciente> buscarPorIdPessoaFis(@PathVariable Integer idPessoaFis) {
        return pacienteService.buscarPorIdPessoaFis(idPessoaFis)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count/ativos")
    public ResponseEntity<Long> contarAtivos() {
        Long count = pacienteService.contarAtivos();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/ativo")
    public ResponseEntity<Boolean> verificarSeAtivo(@PathVariable Integer id) {
        Boolean ativo = pacienteService.isPacienteAtivo(id);
        return ResponseEntity.ok(ativo);
    }
    
    @PostMapping
    public ResponseEntity<Paciente> criar(@RequestBody Paciente paciente) {
        try {
            Paciente novoPaciente = pacienteService.salvar(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPaciente);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(@PathVariable Integer id, @RequestBody Paciente paciente) {
        try {
            Paciente pacienteAtualizado = pacienteService.atualizar(id, paciente);
            return ResponseEntity.ok(pacienteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Integer id) {
        try {
            pacienteService.inativar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Integer id) {
        try {
            pacienteService.ativar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

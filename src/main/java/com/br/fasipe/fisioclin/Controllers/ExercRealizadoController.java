package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.ExercRealizado;
import com.br.fasipe.fisioclin.Services.ExercRealizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exercicios-realizados")
@CrossOrigin(origins = "*")
public class ExercRealizadoController {
    
    @Autowired
    private ExercRealizadoService exercRealizadoService;
    
    @GetMapping
    public ResponseEntity<List<ExercRealizado>> listarTodos() {
        List<ExercRealizado> exercicios = exercRealizadoService.listarTodos();
        return ResponseEntity.ok(exercicios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExercRealizado> buscarPorId(@PathVariable Integer id) {
        return exercRealizadoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/prescricao/{idExercPresc}")
    public ResponseEntity<List<ExercRealizado>> buscarPorPrescricao(@PathVariable Integer idExercPresc) {
        List<ExercRealizado> exercicios = exercRealizadoService.buscarPorPrescricao(idExercPresc);
        return ResponseEntity.ok(exercicios);
    }
    
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ExercRealizado>> buscarPorPaciente(@PathVariable Integer idPaciente) {
        List<ExercRealizado> exercicios = exercRealizadoService.buscarPorPaciente(idPaciente);
        return ResponseEntity.ok(exercicios);
    }
    
    @GetMapping("/evolucao/{idExercPresc}")
    public ResponseEntity<List<ExercRealizado>> buscarEvolucao(
            @PathVariable Integer idExercPresc,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<ExercRealizado> evolucao = exercRealizadoService.buscarEvolucaoExercicios(idExercPresc, dataInicio, dataFim);
        return ResponseEntity.ok(evolucao);
    }
    
    @GetMapping("/paciente/{idPaciente}/ultimos")
    public ResponseEntity<List<ExercRealizado>> buscarUltimos(
            @PathVariable Integer idPaciente,
            @RequestParam(defaultValue = "10") Integer limite) {
        List<ExercRealizado> exercicios = exercRealizadoService.buscarUltimosExercicios(idPaciente, limite);
        return ResponseEntity.ok(exercicios);
    }
    
    @GetMapping("/prescricao/{idExercPresc}/count")
    public ResponseEntity<Long> contarRealizados(@PathVariable Integer idExercPresc) {
        Long count = exercRealizadoService.contarExerciciosRealizados(idExercPresc);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<ExercRealizado> registrar(@RequestBody ExercRealizado exercRealizado) {
        try {
            ExercRealizado novoExercicio = exercRealizadoService.registrar(exercRealizado);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoExercicio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExercRealizado> atualizar(@PathVariable Integer id, @RequestBody ExercRealizado exercRealizado) {
        try {
            ExercRealizado atualizado = exercRealizadoService.atualizar(id, exercRealizado);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            exercRealizadoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

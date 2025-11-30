package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.Anamnese;
import com.br.fasipe.fisioclin.Repositories.AnamneseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de Anamneses
 */
@RestController
@RequestMapping("/api/anamneses")
@Tag(name = "Anamneses", description = "API para gerenciamento de anamneses")
public class AnamneseController {
    
    @Autowired
    private AnamneseRepository anamneseRepository;
    
    @Operation(summary = "Listar todas as anamneses")
    @GetMapping
    public ResponseEntity<List<Anamnese>> listarTodas() {
        return ResponseEntity.ok(anamneseRepository.findAll());
    }
    
    @Operation(summary = "Buscar anamnese por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Anamnese> buscarPorId(@PathVariable Integer id) {
        return anamneseRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Buscar anamneses por paciente")
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<Anamnese>> buscarPorPaciente(@PathVariable Integer idPaciente) {
        List<Anamnese> anamneses = anamneseRepository.findByIdPacienteOrderByDataAnamDesc(idPaciente);
        return ResponseEntity.ok(anamneses);
    }
    
    @Operation(summary = "Buscar anamneses por profissional")
    @GetMapping("/profissional/{idProfissio}")
    public ResponseEntity<List<Anamnese>> buscarPorProfissional(@PathVariable Integer idProfissio) {
        List<Anamnese> anamneses = anamneseRepository.findByIdProfissioOrderByDataAnamDesc(idProfissio);
        return ResponseEntity.ok(anamneses);
    }
    
    @Operation(summary = "Buscar última anamnese do paciente")
    @GetMapping("/paciente/{idPaciente}/ultima")
    public ResponseEntity<Anamnese> buscarUltimaAnamnese(@PathVariable Integer idPaciente) {
        Anamnese ultima = anamneseRepository.findUltimaAnamnese(idPaciente);
        if (ultima != null) {
            return ResponseEntity.ok(ultima);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Buscar anamneses aprovadas do paciente")
    @GetMapping("/paciente/{idPaciente}/aprovadas")
    public ResponseEntity<List<Anamnese>> buscarAprovadasPorPaciente(@PathVariable Integer idPaciente) {
        List<Anamnese> anamneses = anamneseRepository.findAnamnesesAprovadasPaciente(idPaciente);
        return ResponseEntity.ok(anamneses);
    }
    
    @Operation(summary = "Buscar anamneses pendentes")
    @GetMapping("/pendentes")
    public ResponseEntity<List<Anamnese>> buscarPendentes() {
        List<Anamnese> anamneses = anamneseRepository.findAnamnesesPendentes();
        return ResponseEntity.ok(anamneses);
    }
    
    @Operation(summary = "Buscar anamneses pendentes do paciente")
    @GetMapping("/paciente/{idPaciente}/pendentes")
    public ResponseEntity<List<Anamnese>> buscarPendentesPorPaciente(@PathVariable Integer idPaciente) {
        List<Anamnese> anamneses = anamneseRepository.findAnamnesesPendentesByPaciente(idPaciente);
        return ResponseEntity.ok(anamneses);
    }
    
    @Operation(summary = "Contar anamneses pendentes do paciente")
    @GetMapping("/paciente/{idPaciente}/pendentes/count")
    public ResponseEntity<Long> contarPendentesPorPaciente(@PathVariable Integer idPaciente) {
        Long count = anamneseRepository.countAnamnesesPendentesByPaciente(idPaciente);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
}


package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.DTOs.ProfissionalDTO;
import com.br.fasipe.fisioclin.Services.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de profissionais
 */
@RestController
@RequestMapping("/api/profissionais")
@Tag(name = "Profissionais", description = "API para gerenciamento de profissionais")
public class ProfissionalController {
    
    @Autowired
    private ProfissionalService profissionalService;
    
    // Expor o service para teste (temporário)
    public ProfissionalService getService() {
        return profissionalService;
    }
    
    @Operation(summary = "Listar profissionais ativos")
    @GetMapping
    public ResponseEntity<List<ProfissionalDTO>> listarAtivos() {
        return ResponseEntity.ok(profissionalService.listarProfissionaisAtivos());
    }
    
    @Operation(summary = "Listar profissionais de fisioterapia", 
               description = "Lista profissionais com registro CREFITO para fisioterapia")
    @GetMapping("/fisioterapia")
    public ResponseEntity<List<ProfissionalDTO>> listarFisioterapeutas() {
        try {
            System.out.println("🔍 Controller: Iniciando busca de profissionais de fisioterapia...");
            List<ProfissionalDTO> profissionais = profissionalService.listarProfissionaisFisioterapia();
            System.out.println("✅ Controller: Retornando " + profissionais.size() + " profissionais");
            return ResponseEntity.ok(profissionais);
        } catch (Exception e) {
            System.err.println("❌ Erro no controller ao listar profissionais: " + e.getMessage());
            e.printStackTrace();
            // Retorna lista vazia em caso de erro
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }
    
    @Operation(summary = "Listar TODOS os profissionais (teste)", 
               description = "Endpoint de teste para verificar se a API está funcionando")
    @GetMapping("/todos")
    public ResponseEntity<?> listarTodos() {
        try {
            // Usar reflection para acessar o repository (temporário para debug)
            java.lang.reflect.Field field = ProfissionalService.class.getDeclaredField("profissionalRepository");
            field.setAccessible(true);
            com.br.fasipe.fisioclin.Repositories.ProfissionalRepository repo = 
                (com.br.fasipe.fisioclin.Repositories.ProfissionalRepository) field.get(profissionalService);
            
            List<com.br.fasipe.fisioclin.Models.Profissional> todos = repo.findAll();
            System.out.println("📋 Total de profissionais no banco: " + todos.size());
            
            java.util.Map<String, Object> resultado = new java.util.HashMap<>();
            resultado.put("total", todos.size());
            resultado.put("profissionais", todos.stream().map(p -> {
                java.util.Map<String, Object> prof = new java.util.HashMap<>();
                prof.put("idProfissio", p.getIdProfissio());
                prof.put("idConseProfi", p.getIdConseProfi());
                prof.put("statusProfi", p.getStatusProfi());
                return prof;
            }).collect(java.util.stream.Collectors.toList()));
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Buscar profissional por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalDTO> buscarPorId(@PathVariable Integer id) {
        return profissionalService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Buscar nome do profissional")
    @GetMapping("/{id}/nome")
    public ResponseEntity<String> buscarNome(@PathVariable Integer id) {
        String nome = profissionalService.buscarNomeProfissional(id);
        if (nome != null) {
            return ResponseEntity.ok(nome);
        }
        return ResponseEntity.notFound().build();
    }
}


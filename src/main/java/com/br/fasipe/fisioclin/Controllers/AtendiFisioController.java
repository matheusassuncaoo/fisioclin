package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.Models.AtendiFisio;
import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.DTOs.AtendimentoSimplesDTO;
import com.br.fasipe.fisioclin.Exceptions.ErrorResponse;
import com.br.fasipe.fisioclin.Services.AtendiFisioService;
import com.br.fasipe.fisioclin.config.InputSanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/atendimentos")
@Tag(name = "Atendimentos", description = "API para gerenciamento de atendimentos de fisioterapia")
public class AtendiFisioController {
    
    private static final Logger logger = LoggerFactory.getLogger(AtendiFisioController.class);
    
    @Autowired
    private AtendiFisioService atendiFisioService;
    
    @Autowired
    private InputSanitizer inputSanitizer;
    
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
    
    @Operation(summary = "Criar novo atendimento")
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody AtendiFisio atendimento) {
        try {
            // Sanitizar descrição
            if (atendimento.getDescrAtendi() != null) {
                atendimento.setDescrAtendi(inputSanitizer.sanitizeMultiline(atendimento.getDescrAtendi()));
            }
            
            logger.info("Criando atendimento - Paciente: {}", atendimento.getIdPaciente());
            AtendiFisio novoAtendimento = atendiFisioService.salvar(atendimento);
            logger.info("Atendimento criado com sucesso - ID: {}", novoAtendimento.getIdAtendiFisio());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro de validação ao criar atendimento: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", e.getMessage(), "/api/atendimentos")
            );
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido ao criar atendimento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict", e.getMessage(), "/api/atendimentos")
            );
        }
    }
    
    @Operation(summary = "Criar atendimento simples")
    @PostMapping("/simples")
    public ResponseEntity<?> criarSimples(@Valid @RequestBody AtendimentoSimplesDTO dto) {
        try {
            // Sanitizar descrição
            if (dto.getDescricao() != null) {
                dto.setDescricao(inputSanitizer.sanitizeMultiline(dto.getDescricao()));
            }
            if (dto.getCodProced() != null) {
                dto.setCodProced(inputSanitizer.sanitizeCode(dto.getCodProced()));
            }
            
            logger.info("Criando atendimento simples - Paciente: {}, Profissional: {}", 
                dto.getIdPaciente(), dto.getIdProfissio());
            
            AtendiFisio novoAtendimento = atendiFisioService.criarSimples(dto);
            
            logger.info("Atendimento criado - ID: {}", novoAtendimento.getIdAtendiFisio());
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", e.getMessage(), "/api/atendimentos/simples")
            );
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict", e.getMessage(), "/api/atendimentos/simples")
            );
        }
    }
    
    @Operation(summary = "Criar atendimento com método SOAP")
    @PostMapping("/soap")
    public ResponseEntity<?> criarComSOAP(@Valid @RequestBody AtendimentoSOAPDTO dto) {
        try {
            // Sanitizar campos de texto
            sanitizarSOAPDTO(dto);
            
            logger.info("Criando atendimento SOAP - Paciente: {}, Profissional: {}", 
                dto.getIdPaciente(), dto.getIdProfissio());
            
            AtendiFisio novoAtendimento = atendiFisioService.criarComSOAP(dto);
            
            logger.info("Atendimento SOAP criado - ID: {}", novoAtendimento.getIdAtendiFisio());
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro de validação ao criar atendimento SOAP: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", e.getMessage(), "/api/atendimentos/soap")
            );
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido ao criar atendimento SOAP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict", e.getMessage(), "/api/atendimentos/soap")
            );
        }
    }
    
    private void sanitizarSOAPDTO(AtendimentoSOAPDTO dto) {
        if (dto.getSubjetivo() != null) {
            dto.setSubjetivo(inputSanitizer.sanitizeMultiline(dto.getSubjetivo()));
        }
        if (dto.getObjetivo() != null) {
            dto.setObjetivo(inputSanitizer.sanitizeMultiline(dto.getObjetivo()));
        }
        if (dto.getAvaliacao() != null) {
            dto.setAvaliacao(inputSanitizer.sanitizeMultiline(dto.getAvaliacao()));
        }
        if (dto.getPlano() != null) {
            dto.setPlano(inputSanitizer.sanitizeMultiline(dto.getPlano()));
        }
        if (dto.getCodProced() != null) {
            dto.setCodProced(inputSanitizer.sanitizeCode(dto.getCodProced()));
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

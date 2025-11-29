package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteDetalhesDTO;
import com.br.fasipe.fisioclin.Exceptions.ErrorResponse;
import com.br.fasipe.fisioclin.Models.Prontuario;
import com.br.fasipe.fisioclin.Services.PacienteDetalhesService;
import com.br.fasipe.fisioclin.config.InputSanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller para visualização consolidada de dados do paciente
 * Otimizado para rápida visualização no atendimento
 * 
 * SEGURANÇA:
 * - Validação de entrada com Bean Validation (@Valid)
 * - Sanitização de campos de texto (XSS protection)
 * - Logs de auditoria para operações sensíveis
 */
@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Detalhes do Paciente", description = "API para visualização consolidada e evolução de pacientes")
public class PacienteDetalhesController {
    
    private static final Logger logger = LoggerFactory.getLogger(PacienteDetalhesController.class);
    
    @Autowired
    private PacienteDetalhesService pacienteDetalhesService;
    
    @Autowired
    private InputSanitizer inputSanitizer;
    
    @Operation(
        summary = "Buscar detalhes completos do paciente",
        description = "Retorna informações consolidadas: estatísticas, última anamnese, últimos atendimentos, exercícios ativos e evolução recente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalhes retornados com sucesso",
            content = @Content(schema = @Schema(implementation = PacienteDetalhesDTO.class))),
        @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<PacienteDetalhesDTO> buscarDetalhesCompletos(
        @Parameter(description = "ID do paciente", required = true) 
        @PathVariable Integer id) {
        
        // Validação básica de ID
        if (id == null || id <= 0) {
            logger.warn("Tentativa de buscar detalhes com ID inválido: {}", id);
            return ResponseEntity.badRequest().build();
        }
        
        try {
            logger.debug("Buscando detalhes do paciente ID: {}", id);
            PacienteDetalhesDTO detalhes = pacienteDetalhesService.buscarDetalhesCompletos(id);
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            logger.info("Paciente não encontrado - ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao buscar detalhes do paciente ID: {} - {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Criar evolução SOAP",
        description = "Cria uma nova evolução no prontuário usando o método SOAP (Subjetivo, Objetivo, Avaliação, Plano)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Evolução criada com sucesso",
            content = @Content(schema = @Schema(implementation = Prontuario.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou paciente inativo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/evolucao-soap")
    public ResponseEntity<?> criarEvolucaoSOAP(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados da evolução SOAP",
            required = true,
            content = @Content(schema = @Schema(implementation = AtendimentoSOAPDTO.class))
        )
        @Valid @RequestBody AtendimentoSOAPDTO soapDTO) {
        
        try {
            // Sanitização dos campos de texto (proteção XSS)
            sanitizarSOAPDTO(soapDTO);
            
            // Log de auditoria
            logger.info("Criando evolução SOAP - Paciente: {}, Profissional: {}", 
                soapDTO.getIdPaciente(), soapDTO.getIdProfissio());
            
            Prontuario prontuario = pacienteDetalhesService.criarEvolucaoSOAP(soapDTO);
            
            logger.info("Evolução SOAP criada com sucesso - Prontuário ID: {}", prontuario.getIdProntu());
            return ResponseEntity.status(HttpStatus.CREATED).body(prontuario);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Dados inválidos na evolução SOAP: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", e.getMessage(), "/api/pacientes/evolucao-soap")
            );
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para evolução SOAP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(LocalDateTime.now(), 409, "Conflict", e.getMessage(), "/api/pacientes/evolucao-soap")
            );
        } catch (Exception e) {
            logger.error("Erro ao criar evolução SOAP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                new ErrorResponse(LocalDateTime.now(), 500, "Internal Server Error", 
                    "Ocorreu um erro ao processar a evolução", "/api/pacientes/evolucao-soap")
            );
        }
    }
    
    /**
     * Sanitiza campos de texto do DTO para proteção contra XSS
     */
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
        if (dto.getLinkProced() != null) {
            dto.setLinkProced(inputSanitizer.sanitize(dto.getLinkProced()));
        }
    }
}

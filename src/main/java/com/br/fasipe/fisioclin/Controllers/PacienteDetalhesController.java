package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteDetalhesDTO;
import com.br.fasipe.fisioclin.Models.Prontuario;
import com.br.fasipe.fisioclin.Services.PacienteDetalhesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para visualização consolidada de dados do paciente
 * Otimizado para rápida visualização no atendimento
 */
@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
@Tag(name = "Detalhes do Paciente", description = "API para visualização consolidada e evolução de pacientes")
public class PacienteDetalhesController {
    
    @Autowired
    private PacienteDetalhesService pacienteDetalhesService;
    
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
        @Parameter(description = "ID do paciente", required = true) @PathVariable Integer id) {
        try {
            PacienteDetalhesDTO detalhes = pacienteDetalhesService.buscarDetalhesCompletos(id);
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Criar evolução SOAP",
        description = "Cria uma nova evolução no prontuário usando o método SOAP (Subjetivo, Objetivo, Avaliação, Plano)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evolução criada com sucesso",
            content = @Content(schema = @Schema(implementation = Prontuario.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou paciente inativo"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/evolucao-soap")
    public ResponseEntity<Prontuario> criarEvolucaoSOAP(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados da evolução SOAP",
            required = true,
            content = @Content(schema = @Schema(implementation = AtendimentoSOAPDTO.class))
        )
        @RequestBody AtendimentoSOAPDTO soapDTO) {
        try {
            Prontuario prontuario = pacienteDetalhesService.criarEvolucaoSOAP(soapDTO);
            return ResponseEntity.ok(prontuario);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

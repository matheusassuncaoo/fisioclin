package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.DTOs.AtendimentoSOAPDTO;
import com.br.fasipe.fisioclin.DTOs.PacienteDetalhesDTO;
import com.br.fasipe.fisioclin.Models.Prontuario;
import com.br.fasipe.fisioclin.Services.PacienteDetalhesService;
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
public class PacienteDetalhesController {
    
    @Autowired
    private PacienteDetalhesService pacienteDetalhesService;
    
    /**
     * GET /api/pacientes/{id}/detalhes
     * Retorna TODOS os dados consolidados do paciente de forma otimizada
     * 
     * Inclui:
     * - Dados básicos
     * - Estatísticas (total atendimentos, exercícios, dias em tratamento, etc)
     * - Última anamnese
     * - Últimos 10 atendimentos
     * - Exercícios prescritos ativos
     * - Evolução dos últimos 30 dias (para gráficos)
     */
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<PacienteDetalhesDTO> buscarDetalhesCompletos(@PathVariable Integer id) {
        try {
            PacienteDetalhesDTO detalhes = pacienteDetalhesService.buscarDetalhesCompletos(id);
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * POST /api/pacientes/evolucao-soap
     * Cria uma nova evolução no prontuário usando método SOAP
     * 
     * Body esperado:
     * {
     *   "idPaciente": 1,
     *   "idProfissio": 1,
     *   "idProced": 1,
     *   "idEspec": 1,
     *   "dataAtendimento": "2025-11-20",
     *   "subjetivo": "Paciente relata...",
     *   "objetivo": "Realizado...",
     *   "avaliacao": "Apresentou melhora...",
     *   "plano": "Manter conduta..."
     * }
     */
    @PostMapping("/evolucao-soap")
    public ResponseEntity<Prontuario> criarEvolucaoSOAP(@RequestBody AtendimentoSOAPDTO soapDTO) {
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

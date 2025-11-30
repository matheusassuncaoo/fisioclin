package com.br.fasipe.fisioclin.Controllers;

import com.br.fasipe.fisioclin.DTOs.DashboardDTO;
import com.br.fasipe.fisioclin.Services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para dados do Dashboard
 * Fornece estatísticas consolidadas do sistema
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "API para estatísticas do dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Operation(summary = "Obter dados do dashboard", description = "Retorna estatísticas consolidadas: pacientes ativos, atendimentos hoje, anamneses pendentes")
    @GetMapping
    public ResponseEntity<DashboardDTO> obterDashboard() {
        DashboardDTO dashboard = dashboardService.obterDadosDashboard();
        return ResponseEntity.ok(dashboard);
    }
    
    @Operation(summary = "Obter dados detalhados do dashboard", description = "Retorna estatísticas com listas detalhadas")
    @GetMapping("/detalhado")
    public ResponseEntity<DashboardDTO> obterDashboardDetalhado() {
        DashboardDTO dashboard = dashboardService.obterDadosDashboardDetalhado();
        return ResponseEntity.ok(dashboard);
    }
    
    @Operation(summary = "Contar atendimentos de hoje")
    @GetMapping("/atendimentos-hoje")
    public ResponseEntity<Long> contarAtendimentosHoje() {
        return ResponseEntity.ok(dashboardService.contarAtendimentosHoje());
    }
    
    @Operation(summary = "Contar anamneses pendentes")
    @GetMapping("/anamneses-pendentes")
    public ResponseEntity<Long> contarAnamnesesPendentes() {
        return ResponseEntity.ok(dashboardService.contarAnamnesesPendentes());
    }
    
    @Operation(summary = "Contar anamneses pendentes de um paciente")
    @GetMapping("/anamneses-pendentes/{idPaciente}")
    public ResponseEntity<Long> contarAnamnesesPendentesPorPaciente(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(dashboardService.contarAnamnesesPendentesPorPaciente(idPaciente));
    }
}


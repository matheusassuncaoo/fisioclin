package com.br.fasipe.fisioclin.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para dados do Dashboard
 * Contém estatísticas consolidadas do sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    
    // Contadores principais
    private Long totalPacientesAtivos;
    private Long atendimentosHoje;
    private Long anamnesesPendentes;
    
    // Detalhes adicionais (opcional)
    private LocalDate dataReferencia;
    
    // Resumo de atendimentos de hoje
    private List<AtendimentoHojeDTO> atendimentosDeHoje;
    
    // Anamneses aguardando aprovação
    private List<AnamnesePendenteDTO> anamnesesAguardando;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtendimentoHojeDTO {
        private Integer idAtendiFisio;
        private Integer idPaciente;
        private String nomePaciente;
        private String procedimento;
        private String nomeProfissional;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnamnesePendenteDTO {
        private Integer idAnamnese;
        private Integer idPaciente;
        private String nomePaciente;
        private LocalDate dataAnamnese;
        private String status;
    }
}


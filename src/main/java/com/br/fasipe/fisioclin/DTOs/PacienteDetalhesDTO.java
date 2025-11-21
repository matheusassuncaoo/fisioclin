package com.br.fasipe.fisioclin.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO consolidado com todas as informações do paciente
 * para visualização rápida no atendimento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDetalhesDTO {
    
    // Dados básicos do paciente
    private Integer idPaciente;
    private String rgPaciente;
    private String estdoRgPac;
    private Boolean statusPac;
    
    // Dados da pessoa física
    private String nomePessoa;
    private String cpfPessoa;
    private LocalDate dataNascPes;
    private String sexoPessoa;
    
    // Estatísticas gerais
    private EstatisticasDTO estatisticas;
    
    // Última anamnese
    private AnamneseResumoDTO ultimaAnamnese;
    
    // Últimos atendimentos (top 5)
    private List<AtendimentoResumoDTO> ultimosAtendimentos;
    
    // Exercícios prescritos ativos
    private List<ExercicioPrescritoDTO> exerciciosAtivos;
    
    // Histórico de evolução (últimos 30 dias)
    private List<EvolucaoDiariaDTO> evolucaoRecente;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasDTO {
        private Long totalAtendimentos;
        private Long totalProntuarios;
        private Long totalExerciciosRealizados;
        private Integer diasEmTratamento;
        private LocalDate primeiroAtendimento;
        private LocalDate ultimoAtendimento;
        private Double frequenciaSemanal; // Média de atendimentos por semana
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnamneseResumoDTO {
        private Integer idAnamnese;
        private LocalDate dataAnamnese;
        private String statusAnamnese;
        private String queixaPrincipal;
        private String historiaDoenca;
        private String observacao;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtendimentoResumoDTO {
        private Integer idAtendiFisio;
        private LocalDate dataAtendimento;
        private String descricao;
        private String nomeProfissional; // Se tiver relacionamento
        private String procedimento; // Se tiver relacionamento
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExercicioPrescritoDTO {
        private Integer idExercPresc;
        private String nomeExercicio;
        private Integer series;
        private Integer repeticoes;
        private String orientacao;
        private Integer totalRealizados;
        private LocalDate ultimaRealizacao;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoDiariaDTO {
        private LocalDate data;
        private Integer quantidadeAtendimentos;
        private Integer exerciciosRealizados;
        private String resumoDia;
    }
}

package com.br.fasipe.fisioclin.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para criar atendimento de forma simples (sem SOAP)
 */
@Data
public class AtendimentoSimplesDTO {
    
    @NotNull(message = "ID do paciente é obrigatório")
    private Integer idPaciente;
    
    @NotNull(message = "ID do profissional é obrigatório")
    private Integer idProfissio;
    
    @NotNull(message = "Código do procedimento é obrigatório")
    @Size(min = 1, max = 8, message = "Código do procedimento deve ter entre 1 e 8 caracteres")
    private String codProced;
    
    @NotNull(message = "Data do atendimento é obrigatória")
    @PastOrPresent(message = "Data do atendimento não pode ser futura")
    private LocalDate dataAtendimento;
    
    @Size(max = 250, message = "Descrição não pode exceder 250 caracteres")
    private String descricao;
}


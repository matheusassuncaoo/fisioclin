package com.br.fasipe.fisioclin.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO para o método SOAP (Subjetivo, Objetivo, Avaliação, Plano)
 * Usado para criar evoluções no prontuário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoSOAPDTO {
    
    private Integer idPaciente;
    private Integer idProfissio;
    private String codProced;  // Código do procedimento (ex: "12345678")
    private Integer idEspec;
    private LocalDate dataAtendimento;
    
    // SOAP
    private String subjetivo;  // Queixa do paciente
    private String objetivo;   // Conduta realizada
    private String avaliacao;  // Resposta ao tratamento
    private String plano;      // Próximas ações
    
    // Campos opcionais
    private String linkProced;
    private Boolean autoPacVisu; // Autorização para paciente visualizar
}

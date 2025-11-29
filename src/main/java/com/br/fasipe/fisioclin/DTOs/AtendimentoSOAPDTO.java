package com.br.fasipe.fisioclin.DTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO para o método SOAP (Subjetivo, Objetivo, Avaliação, Plano)
 * Usado para criar evoluções no prontuário
 * 
 * Validações aplicadas:
 * - Campos obrigatórios: idPaciente, idProfissio, codProced, dataAtendimento
 * - Tamanhos máximos para evitar overflow no banco
 * - Data não pode ser futura
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoSOAPDTO {
    
    @NotNull(message = "ID do paciente é obrigatório")
    @Positive(message = "ID do paciente deve ser um número positivo")
    private Integer idPaciente;
    
    @NotNull(message = "ID do profissional é obrigatório")
    @Positive(message = "ID do profissional deve ser um número positivo")
    private Integer idProfissio;
    
    @NotBlank(message = "Código do procedimento é obrigatório")
    @Size(min = 1, max = 20, message = "Código do procedimento deve ter entre 1 e 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Código do procedimento deve conter apenas letras e números")
    private String codProced;
    
    @Positive(message = "ID da especialidade deve ser um número positivo")
    private Integer idEspec;
    
    @NotNull(message = "Data do atendimento é obrigatória")
    @PastOrPresent(message = "Data do atendimento não pode ser futura")
    private LocalDate dataAtendimento;
    
    // SOAP - campos com limite de tamanho para segurança
    @Size(max = 2000, message = "Campo Subjetivo deve ter no máximo 2000 caracteres")
    private String subjetivo;  // Queixa do paciente
    
    @Size(max = 2000, message = "Campo Objetivo deve ter no máximo 2000 caracteres")
    private String objetivo;   // Conduta realizada
    
    @Size(max = 2000, message = "Campo Avaliação deve ter no máximo 2000 caracteres")
    private String avaliacao;  // Resposta ao tratamento
    
    @Size(max = 2000, message = "Campo Plano deve ter no máximo 2000 caracteres")
    private String plano;      // Próximas ações
    
    // Campos opcionais
    @Size(max = 250, message = "Link do procedimento deve ter no máximo 250 caracteres")
    @Pattern(regexp = "^(https?://.*)?$", message = "Link deve ser uma URL válida ou vazio")
    private String linkProced;
    
    private Boolean autoPacVisu; // Autorização para paciente visualizar
}

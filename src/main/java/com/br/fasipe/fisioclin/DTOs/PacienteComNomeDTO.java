package com.br.fasipe.fisioclin.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para retornar dados do paciente junto com informações da pessoa física
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paciente com dados da pessoa física")
public class PacienteComNomeDTO {
    
    @Schema(description = "ID do paciente", example = "1")
    private Integer idPaciente;
    
    @Schema(description = "ID da pessoa física", example = "1")
    private Integer idPessoaFis;
    
    @Schema(description = "RG do paciente", example = "123456789")
    private String rgPaciente;
    
    @Schema(description = "Estado emissor do RG", example = "SP")
    private String estdoRgPac;
    
    @Schema(description = "Status do paciente (ativo/inativo)", example = "true")
    private Boolean statusPac;
    
    // Dados da pessoa física
    @Schema(description = "Nome completo da pessoa", example = "João da Silva")
    private String nomePessoa;
    
    @Schema(description = "CPF da pessoa", example = "12345678901")
    private String cpfPessoa;
    
    @Schema(description = "Data de nascimento", example = "1990-05-15")
    private LocalDate dataNascPes;
    
    @Schema(description = "Sexo da pessoa", example = "M", allowableValues = {"M", "F"})
    private String sexoPessoa;
}

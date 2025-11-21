package com.br.fasipe.fisioclin.DTOs;

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
public class PacienteComNomeDTO {
    
    private Integer idPaciente;
    private Integer idPessoaFis;
    private String rgPaciente;
    private String estdoRgPac;
    private Boolean statusPac;
    
    // Dados da pessoa física
    private String nomePessoa;
    private String cpfPessoa;
    private LocalDate dataNascPes;
    private String sexoPessoa;
}

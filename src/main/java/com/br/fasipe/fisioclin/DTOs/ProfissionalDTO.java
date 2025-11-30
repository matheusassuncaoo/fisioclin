package com.br.fasipe.fisioclin.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para exibição de profissionais
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalDTO {
    private Integer idProfissio;
    private String nomeProfissional;
    private String conselho;  // CREFITO, CRM, etc
    private String especialidade;
    private Boolean ativo;
}


package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PACIENTE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPACIENTE")
    private Integer idPaciente;
    
    @Column(name = "ID_PESSOAFIS", nullable = false, unique = true)
    private Integer idPessoaFis;
    
    @NotBlank(message = "RG do paciente é obrigatório")
    @Size(max = 15, message = "RG deve ter no máximo 15 caracteres")
    @Column(name = "RGPACIENTE", nullable = false, unique = true, length = 15)
    private String rgPaciente;
    
    @Size(max = 2, message = "Estado do RG deve ter 2 caracteres")
    @Column(name = "ESTDORGPAC", length = 2)
    private String estdoRgPac;
    
    @Builder.Default
    @Column(name = "STATUSPAC", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean statusPac = true;
}

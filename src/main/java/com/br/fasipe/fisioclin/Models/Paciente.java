package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "ID da pessoa física é obrigatório")
    @Column(name = "ID_PESSOAFIS", nullable = false, unique = true)
    private Integer idPessoaFis;
    
    @NotBlank(message = "RG do paciente é obrigatório")
    @Size(max = 15, message = "RG deve ter no máximo 15 caracteres")
    @Column(name = "RGPACIENTE", nullable = false, unique = true, length = 15)
    private String rgPaciente;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTDORGPAC")
    private EstadoRg estdoRgPac;
    
    @Column(name = "STATUSPAC", nullable = false)
    private Boolean statusPac = true;
    
    public enum EstadoRg {
        AC, AL, AP, AM, BA, CE, DF, ES, GO, MA,
        MT, MS, MG, PA, PB, PR, PE, PI, RJ, RN,
        RS, RO, RR, SC, SP, SE, TO
    }
}

package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PACIENTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPACIENTE")
    private Integer idPaciente;
    
    @Column(name = "ID_PESSOAFIS", nullable = false, unique = true)
    private Integer idPessoaFis;
    
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

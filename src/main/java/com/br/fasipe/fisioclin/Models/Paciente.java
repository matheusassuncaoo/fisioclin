package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;

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

    @Column(name = "RGPACIENTE", nullable = false, length = 15, unique = true)
    private String rgPaciente;

    @Column(name = "ESTDORGPAC", length = 2)
    private String estdOrgPac;

    @Column(name = "STATUSPAC", nullable = false)
    private Integer statusPac; // tinyint(1): 0=inativo, 1=ativo

    @Column(name = "IDDOCUMENTO", nullable = false, insertable = false, updatable = false)
    private BigInteger idDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDDOCUMENTO", referencedColumnName = "IDDOCUMENTO")
    private Documento documento;
}

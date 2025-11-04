package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROCPRESCAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcPrescAge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROCPRESCAGE")
    private Integer idProcPrescAge;
    
    @Column(name = "ID_PROCPRESC", nullable = false)
    private Integer idProcPresc;
    
    @Column(name = "ID_AGENDA", nullable = false)
    private Integer idAgenda;
}

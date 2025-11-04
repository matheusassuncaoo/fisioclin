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
@Table(name = "PROCPRESCATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcPrescAte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROCPRESCATE")
    private Integer idProcPrescAte;
    
    @Column(name = "ID_PROCPRESC", nullable = false)
    private Integer idProcPresc;
    
    @Column(name = "ID_PRONTU", nullable = false)
    private Integer idProntu;
    
    @Column(name = "STATUSATE", nullable = false)
    private Boolean statusAte = false;
}

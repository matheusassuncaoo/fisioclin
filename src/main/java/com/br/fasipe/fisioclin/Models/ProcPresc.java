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
@Table(name = "PROCPRESC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcPresc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROCPRESC")
    private Integer idProcPresc;
    
    @Column(name = "ID_ANAMNESE", nullable = false)
    private Integer idAnamnese;
    
    @Column(name = "ID_PROCED", nullable = false)
    private Integer idProced;
    
    @Column(name = "PROCEDQTD", nullable = false)
    private Integer procedQtd;
    
    @Column(name = "IMAGEMPROC", length = 250)
    private String imagemProc;
    
    @Column(name = "ORIENTACAO", length = 250)
    private String orientacao;
}

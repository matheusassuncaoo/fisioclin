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
import java.time.LocalDate;

@Entity
@Table(name = "ATENDIFISIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendiFisio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDATENDIFISIO")
    private Integer idAtendiFisio;
    
    @Column(name = "ID_PACIENTE", nullable = false)
    private Integer idPaciente;
    
    @Column(name = "ID_PROFISSIO", nullable = false)
    private Integer idProfissio;
    
    @Column(name = "ID_PROCED", nullable = false)
    private Integer idProced;
    
    @Column(name = "DATAATENDI", nullable = false)
    private LocalDate dataAtendi;
    
    @Column(name = "DESCRATENDI", length = 250)
    private String descrAtendi;
}

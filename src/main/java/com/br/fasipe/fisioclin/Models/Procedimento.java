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
import java.math.BigDecimal;

@Entity
@Table(name = "PROCEDIMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROCED")
    private Integer idProced;
    
    @Column(name = "CODPROCED", nullable = false, unique = true, length = 8)
    private String codProced;
    
    @Column(name = "DESCRPROC", nullable = false, length = 250)
    private String descrProc;
    
    @Column(name = "VALORPROC", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorProc;
}

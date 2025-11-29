package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabela de vínculo entre especialidades e procedimentos
 * Define quais procedimentos pertencem a cada especialidade
 */
@Entity
@Table(name = "ESPECPROCED")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecProced {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDESPECPROCED")
    private Integer idEspecProced;
    
    @Column(name = "ID_PROCED", nullable = false)
    private Integer idProced;
    
    @Column(name = "ID_ESPEC", nullable = false)
    private Integer idEspec;
    
    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROCED", insertable = false, updatable = false)
    private Procedimento procedimento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESPEC", insertable = false, updatable = false)
    private Especialidade especialidade;
}


package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabela de Conselhos Profissionais
 * Contém a descrição da especialidade do profissional
 * 
 * Exemplos:
 * - CREFITO = Fisioterapia
 * - CRM = Medicina
 * - CRO = Odontologia
 * - CRN = Nutrição
 * - CRP = Psicologia
 */
@Entity
@Table(name = "CONSEPROFI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConseProfi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCONSEPROFI")
    private Integer idConseProfi;
    
    @Column(name = "DESCRCONS", nullable = false, length = 100)
    private String descrCons;
    
    @Column(name = "SIGLACONS", length = 20)
    private String siglaCons;
    
    // Constantes para conselhos comuns
    public static final String CREFITO = "CREFITO"; // Fisioterapia
    public static final String CRM = "CRM";         // Medicina
    public static final String CRO = "CRO";         // Odontologia
    public static final String CRN = "CRN";         // Nutrição
    public static final String CRP = "CRP";         // Psicologia
    public static final String COREN = "COREN";     // Enfermagem
}


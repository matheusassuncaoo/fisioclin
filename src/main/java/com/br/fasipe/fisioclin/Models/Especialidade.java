package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabela de especialidades médicas/saúde
 * Código 30 = Fisioterapia
 */
@Entity
@Table(name = "ESPECIALIDADE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Especialidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDESPEC")
    private Integer idEspec;
    
    @Column(name = "CODESPEC", nullable = false, unique = true, length = 2)
    private String codEspec;
    
    @Column(name = "DESCESPEC", nullable = false, length = 100)
    private String descEspec;
    
    // Constantes para especialidades comuns
    public static final String COD_FISIOTERAPIA = "30";
    public static final String COD_BIOMEDICINA = "00";
    public static final String COD_ENFERMAGEM = "10";
    public static final String COD_FARMACIA = "20";
    public static final String COD_MEDICINA = "40";
    public static final String COD_NUTRICAO = "50";
    public static final String COD_ODONTOLOGIA = "60";
    public static final String COD_PSICOLOGIA = "70";
    public static final String COD_SERVICO_SOCIAL = "80";
}


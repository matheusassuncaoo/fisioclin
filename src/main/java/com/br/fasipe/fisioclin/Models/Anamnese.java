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
import java.time.LocalDateTime;

@Entity
@Table(name = "ANAMNESE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anamnese {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDANAMNESE")
    private Integer idAnamnese;
    
    @Column(name = "ID_PACIENTE", nullable = false)
    private Integer idPaciente;
    
    @Column(name = "ID_PROFISSIO", nullable = false)
    private Integer idProfissio;
    
    @Column(name = "DATAANAM", nullable = false)
    private LocalDateTime dataAnam;
    
    @Column(name = "NOMERESP", length = 100)
    private String nomeResp;
    
    @Column(name = "CPFRESP", length = 6)
    private String cpfResp;
    
    @Column(name = "AUTVISIB", nullable = false)
    private Integer autVisib; // tinyint(1)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUSANM", nullable = false)
    private StatusAnamnese statusAnm;
    
    @Column(name = "STATUSFUNC", nullable = false)
    private Integer statusFunc; // tinyint(1)
    
    @Column(name = "OBSERVACOES", columnDefinition = "TEXT")
    private String observacoes;
    
    public enum StatusAnamnese {
        APROVADO, REPROVADO, CANCELADO
    }
}

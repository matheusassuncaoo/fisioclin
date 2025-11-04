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
import java.time.LocalDateTime;

@Entity
@Table(name = "EXERCREALIZADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExercRealizado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDEXERCREALIZADO")
    private Integer idExercRealizado;
    
    @Column(name = "ID_EXERCPRESC", nullable = false)
    private Integer idExercPresc;
    
    @Column(name = "DATAHORA", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
    
    @Column(name = "OBSERVACAO", length = 100)
    private String observacao;
}

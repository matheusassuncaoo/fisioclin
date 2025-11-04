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
@Table(name = "EXERCPRESC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExercPresc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDEXERCPRESC")
    private Integer idExercPresc;
    
    @Column(name = "ID_EXERCICIO", nullable = false)
    private Integer idExercicio;
    
    @Column(name = "ID_ANAMNESE", nullable = false)
    private Integer idAnamnese;
    
    @Column(name = "QTDEXERC", nullable = false)
    private Integer qtdExerc;
    
    @Column(name = "ORIENTACAO", columnDefinition = "TEXT")
    private String orientacao;
}

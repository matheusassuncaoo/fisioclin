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
@Table(name = "EXERCICIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDEXERCICIO")
    private Integer idExercicio;
    
    @Column(name = "DESCREXERC", nullable = false, length = 250)
    private String descrExerc;
    
    @Column(name = "LINKVIDEO", length = 250)
    private String linkVideo;
}

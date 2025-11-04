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
@Table(name = "TOKENFISIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenFisio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTOKENFISIO")
    private Integer idTokenFisio;
    
    @Column(name = "ID_ANAMNESE", nullable = false, unique = true)
    private Integer idAnamnese;
    
    @Column(name = "TOKEN", nullable = false, unique = true, length = 5)
    private String token;
}

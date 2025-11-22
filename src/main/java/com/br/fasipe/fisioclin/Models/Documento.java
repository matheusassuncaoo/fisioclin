package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade DOCUMENTO - CPF ou CNPJ
 * Conforme dicionário de dados (dbanovo.csv)
 */
@Entity
@Table(name = "DOCUMENTO", 
       tablespace = "TS_DOCUMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    
    @Id
    @Column(name = "IDDOCUMENTO", nullable = false)
    private Long idDocumento; // BIGINT 14 - CPF (11) ou CNPJ (14)
}

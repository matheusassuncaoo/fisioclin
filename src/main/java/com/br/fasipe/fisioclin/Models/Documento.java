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
@Table(name = "DOCUMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

     @Column(name = "DOCUMENTO", nullable = false, unique = true)
     private BigInteger documento; // CPF (11 dígitos) ou CNPJ (14 dígitos)
}

package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade PESSOAFIS - Pessoas Físicas
 * Conforme dicionário de dados (dbanovo.csv)
 */
@Entity
@Table(name = "PESSOAFIS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOAFIS")
    private Integer idPessoaFis;
    
    @Column(name = "IDDOCUMENTO", nullable = false)
    private Long idDocumento; // FK para DOCUMENTO (CPF 11 dígitos)
    
    @Column(name = "ID_PESSOA", nullable = false, unique = true)
    private Integer idPessoa; // FK para PESSOA
    
    @Enumerated(EnumType.STRING)
    @Column(name = "SEXOPESSOA", nullable = false, length = 1)
    private Sexo sexoPessoa;
    
    @Column(name = "DATACRIACAO", nullable = false)
    private LocalDateTime dataCriacao;
    
    public enum Sexo {
        M, // Masculino
        F  // Feminino
    }
}

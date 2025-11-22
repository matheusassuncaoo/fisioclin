package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade PESSOAJUR - Pessoas Jurídicas
 * Conforme dicionário de dados (dbanovo.csv)
 */
@Entity
@Table(name = "PESSOAJUR",
       tablespace = "TS_AGENDA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaJur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOAJUR")
    private Integer idPessoaJur;
    
    @Column(name = "IDDOCUMENTO", nullable = false, unique = true)
    private Long idDocumento; // FK para DOCUMENTO (CNPJ 14 dígitos)
    
    @Column(name = "RAZSOCIAL", nullable = false, length = 100)
    private String razSocial;
    
    @Column(name = "NOMEFAN", nullable = false, length = 100)
    private String nomeFan;
    
    @Column(name = "CNAE", length = 7)
    private String cnae;
    
    @Column(name = "DATACRIACAO", nullable = false)
    private LocalDateTime dataCriacao;
}

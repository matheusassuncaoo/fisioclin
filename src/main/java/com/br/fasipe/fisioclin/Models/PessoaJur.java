package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "PESSOAJUR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaJur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOAJUR")
    private Integer idPessoaJur;
    
    @Column(name = "ID_PESSOA")
    private Integer idPessoa;
    
    @Column(name = "RAZSOCIAL", nullable = false, length = 100)
    private String razSocial;
    
    @Column(name = "NOMEFAN", nullable = false, length = 100)
    private String nomeFan;
    
    @Column(name = "CNAE", length = 7)
    private String cnae;
    
    @Column(name = "DATACRIACAO", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "IDDOCUMENTO", nullable = false, unique = true)
    private BigInteger idDocumento;
}

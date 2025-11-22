package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;
import java.time.LocalDateTime;

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
    
    @Column(name = "ID_PESSOA", nullable = false, insertable = false, updatable = false)
    private Integer idPessoa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA", referencedColumnName = "IDPESSOA")
    private Pessoa pessoa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "SEXOPESSOA", nullable = false)
    private Sexo sexoPessoa;
    
    @Column(name = "DATACRIACAO", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "IDDOCUMENTO", nullable = false, insertable = false, updatable = false)
    private BigInteger idDocumento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDDOCUMENTO", referencedColumnName = "IDDOCUMENTO")
    private Documento documento;
    
    public enum Sexo {
        M, F
    }
}

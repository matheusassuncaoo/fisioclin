package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade PESSOA - Tabela genérica de pessoas
 * Conforme dicionário de dados (dbanovo.csv)
 */
@Entity
@Table(name = "PESSOA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOA")
    private Integer idPessoa;
    
    @Column(name = "ID_DOCUMENTO", nullable = false, insertable = false, updatable = false)
    private Long idDocumento; // FK para DOCUMENTO
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOCUMENTO", referencedColumnName = "IDDOCUMENTO")
    private Documento documento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOPESSOA", nullable = false, length = 1)
    private TipoPessoa tipoPessoa; // 'F' ou 'J'
    
    @Column(name = "EMAIL", nullable = false, length = 50)
    private String email;
    
    @Column(name = "CEP", nullable = false)
    private String cep;
    
    @Column(name = "BAIRRO", nullable = false, length = 40)
    private String bairro;
    
    @Column(name = "NUMERO_ENDERECO", nullable = false, length = 50)
    private String numeroEndereco;
    
    @Column(name = "COMPLEMENTO", nullable = false, length = 10)
    private String complemento;
    
    @Column(name = "TELEFONE", nullable = false, length = 20)
    private String telefone;
    
    @Column(name = "ESTADO_RESIDE", nullable = false, length = 2)
    private String estadoReside;
    
    @Column(name = "CIDADE_RESIDE", nullable = false, length = 80)
    private String cidadeReside;
    
    @Column(name = "ESTADO_NASCIMENTO", nullable = false, length = 2)
    private String estadoNascimento;
    
    @Column(name = "CIDADE_NASCIMENTO", nullable = false, length = 80)
    private String cidadeNascimento;
    
    @Column(name = "NOMEPESSOA", nullable = false, length = 200)
    private String nomePessoa;
    
    public enum TipoPessoa {
        F, // Física
        J  // Jurídica
    }
}

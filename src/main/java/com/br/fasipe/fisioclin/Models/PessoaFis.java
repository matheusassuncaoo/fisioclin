package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PESSOAFIS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOAFIS")
    private Integer idPessoaFis;
    
    @NotNull(message = "ID da pessoa é obrigatório")
    @Column(name = "ID_PESSOA", nullable = false, unique = true)
    private Integer idPessoa;
    
    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 caracteres")
    @Column(name = "CPFPESSOA", nullable = false, unique = true, length = 11)
    private String cpfPessoa;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "NOMEPESSOA", nullable = false, length = 100)
    private String nomePessoa;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Column(name = "DATANASCPES", nullable = false)
    private LocalDate dataNascPes;
    
    @NotBlank(message = "Sexo é obrigatório")
    @Column(name = "SEXOPESSOA", nullable = false, columnDefinition = "ENUM('M','F')")
    private String sexoPessoa;
    
    @Builder.Default
    @Column(name = "DATACRIACAO", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataCriacao = LocalDateTime.now();
}

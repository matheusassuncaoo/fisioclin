package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PESSOA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPESSOA")
    private Integer idPessoa;
    
    @NotBlank(message = "Tipo de pessoa é obrigatório")
    @Column(name = "TIPOPESSOA", nullable = false, columnDefinition = "ENUM('F','J')")
    private String tipoPessoa;
}

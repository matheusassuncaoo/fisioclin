package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PACIENTE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
    
    @Column(name = "ID_IDDOCUMENTO", nullable = false)
    private Long idDocumento;
    
    @Id
    @Column(name = "RGPESSOA", nullable = false)
    private Integer rgPessoa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUSPESSOA", nullable = false)
    private StatusPessoa statusPessoa;
    
    public enum StatusPessoa {
        ATIVO,
        INATIVO
    }
}

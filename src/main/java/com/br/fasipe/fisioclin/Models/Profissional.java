package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFISSIONAL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profissional {
    
    @Column(name = "ID_IDDOCUMENTO", nullable = false)
    private Long idDocumento;
    
    @Column(name = "SUPERVISORDOC")
    private Integer supervisorDoc;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOPROFI", nullable = false, length = 1)
    private TipoProfi tipoProfi;
    
    @Column(name = "ID_SUPPROFI")
    private Integer idSupProfi;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUSPROFI", nullable = false, length = 1)
    private StatusProfi statusProfi;
    
    @Column(name = "IDCONSEPROFI")
    private Integer idConseProfi;
    
    @Column(name = "ESPECIALIDADE", nullable = false, length = 100)
    private String especialidade;
    
    public enum TipoProfi {
        _1, // administrativo
        _2, // técnico básico
        _3, // técnico supervisor
        _4  // master
    }
    
    public enum StatusProfi {
        _1, // ativo
        _2, // inativo
        _3  // suspenso
    }
}

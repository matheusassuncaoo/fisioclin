package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;

@Entity
@Table(name = "PROFISSIONAL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profissional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPROFISSIO")
    private Integer idProfissio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOPROFI", nullable = false)
    private TipoProfi tipoProfi;
    
    @Column(name = "ID_SUPPROFI")
    private Integer idSupProfi;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUSPROFI", nullable = false)
    private StatusProfi statusProfi;
    
    @Column(name = "ID_CONSEPROFI")
    private Integer idConseProfi;
    
    @Column(name = "ID_DOCUMENTO")
    private BigInteger idDocumento;
    
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

package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "PRONTUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prontuario {
    
    @Column(name = "ID_IDDOCUMENTO", nullable = false)
    private Integer idDocumento;
    
    @Column(name = "ID_PROFISSIO", nullable = false)
    private Integer idProfissio;
    
    @Column(name = "ID_ESPEC", nullable = false)
    private Integer idEspec;
    
    @Column(name = "ID_CODPROCED", nullable = false, length = 8)
    private String idCodProced;
    
    @Column(name = "DATAPROCED", nullable = false)
    private LocalDate dataProced;
    
    @Column(name = "DESCPROD", nullable = false, columnDefinition = "TEXT")
    private String descProd;
    
    @Column(name = "LINKPROCED", length = 250)
    private String linkProced;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "AUTOPACVISU", nullable = false)
    private AutorizacaoVisual autoPacVisu;
    
    public enum AutorizacaoVisual {
        SIM, NAO
    }
}

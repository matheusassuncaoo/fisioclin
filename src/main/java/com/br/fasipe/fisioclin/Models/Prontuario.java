package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPRONTU")
    private Integer idProntu;
    
    @Column(name = "ID_PACIENTE", nullable = false)
    private Integer idPaciente;
    
    @Column(name = "ID_PROFISSIO", nullable = false)
    private Integer idProfissio;
    
    @Column(name = "ID_ESPEC", nullable = false)
    private Integer idEspec;
    
    @Column(name = "ID_PROCED", nullable = false)
    private Integer idProced;
    
    @Column(name = "DATAPROCED", nullable = false)
    private LocalDate dataProced;
    
    @Column(name = "DESCRPRONTU", nullable = false, columnDefinition = "TEXT")
    private String descrProntu;
    
    @Column(name = "LINKPROCED", length = 250)
    private String linkProced;
    
    @Column(name = "AUTOPACVISU", nullable = false)
    private Boolean autoPacVisu;
}

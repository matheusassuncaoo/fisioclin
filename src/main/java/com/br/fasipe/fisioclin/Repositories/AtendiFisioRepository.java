package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.AtendiFisio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AtendiFisioRepository extends JpaRepository<AtendiFisio, Integer> {
    
    // Buscar atendimentos por paciente
    List<AtendiFisio> findByIdPacienteOrderByDataAtendiDesc(Integer idPaciente);
    
    // Buscar atendimentos por profissional
    List<AtendiFisio> findByIdProfissioOrderByDataAtendiDesc(Integer idProfissio);
    
    // Buscar atendimentos por data
    List<AtendiFisio> findByDataAtendiBetweenOrderByDataAtendiDesc(LocalDate dataInicio, LocalDate dataFim);
    
    // Buscar atendimentos por paciente e período (OTIMIZADO para evolução)
    @Query("SELECT a FROM AtendiFisio a WHERE a.idPaciente = :idPaciente AND a.dataAtendi BETWEEN :dataInicio AND :dataFim ORDER BY a.dataAtendi DESC")
    List<AtendiFisio> findByPacienteAndPeriodo(
        @Param("idPaciente") Integer idPaciente,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    // Último atendimento do paciente
    @Query("SELECT a FROM AtendiFisio a WHERE a.idPaciente = :idPaciente ORDER BY a.dataAtendi DESC LIMIT 1")
    AtendiFisio findUltimoAtendimento(@Param("idPaciente") Integer idPaciente);
    
    // Primeiro atendimento do paciente
    @Query("SELECT a FROM AtendiFisio a WHERE a.idPaciente = :idPaciente ORDER BY a.dataAtendi ASC LIMIT 1")
    AtendiFisio findPrimeiroAtendimento(@Param("idPaciente") Integer idPaciente);
    
    // Contar atendimentos do paciente
    Long countByIdPaciente(Integer idPaciente);
    
    // Buscar atendimentos por procedimento
    List<AtendiFisio> findByIdProced(Integer idProced);
    
    // ===== NOVOS MÉTODOS PARA DASHBOARD =====
    
    // Buscar atendimentos de hoje
    @Query("SELECT a FROM AtendiFisio a WHERE a.dataAtendi = :data ORDER BY a.idAtendiFisio DESC")
    List<AtendiFisio> findByData(@Param("data") LocalDate data);
    
    // Contar atendimentos de hoje
    @Query("SELECT COUNT(a) FROM AtendiFisio a WHERE a.dataAtendi = :data")
    Long countByData(@Param("data") LocalDate data);
    
    // Contar atendimentos de hoje por profissional
    @Query("SELECT COUNT(a) FROM AtendiFisio a WHERE a.dataAtendi = :data AND a.idProfissio = :idProfissio")
    Long countByDataAndProfissional(@Param("data") LocalDate data, @Param("idProfissio") Integer idProfissio);
    
    // Buscar atendimentos de hoje por profissional de fisioterapia (via conselho)
    @Query("SELECT a FROM AtendiFisio a " +
           "JOIN Profissional p ON a.idProfissio = p.idProfissio " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE a.dataAtendi = :data " +
           "AND (UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%') " +
           "ORDER BY a.idAtendiFisio DESC")
    List<AtendiFisio> findAtendimentosHojeFisioterapia(@Param("data") LocalDate data);
    
    // Contar atendimentos de fisioterapia hoje
    @Query("SELECT COUNT(a) FROM AtendiFisio a " +
           "JOIN Profissional p ON a.idProfissio = p.idProfissio " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE a.dataAtendi = :data " +
           "AND (UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%')")
    Long countAtendimentosHojeFisioterapia(@Param("data") LocalDate data);
}

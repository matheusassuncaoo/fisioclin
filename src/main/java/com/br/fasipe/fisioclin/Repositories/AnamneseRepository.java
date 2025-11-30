package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Anamnese;
import com.br.fasipe.fisioclin.Models.Anamnese.StatusAnamnese;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnamneseRepository extends JpaRepository<Anamnese, Integer> {
    
    // Buscar anamneses por paciente
    List<Anamnese> findByIdPacienteOrderByDataAnamDesc(Integer idPaciente);
    
    // Buscar anamneses por profissional
    List<Anamnese> findByIdProfissioOrderByDataAnamDesc(Integer idProfissio);
    
    // Buscar anamneses por status
    List<Anamnese> findByStatusAnm(StatusAnamnese statusAnm);
    
    // Buscar anamneses aprovadas do paciente
    @Query("SELECT a FROM Anamnese a WHERE a.idPaciente = :idPaciente AND a.statusAnm = 'APROVADO' ORDER BY a.dataAnam DESC")
    List<Anamnese> findAnamnesesAprovadasPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Última anamnese do paciente
    @Query("SELECT a FROM Anamnese a WHERE a.idPaciente = :idPaciente ORDER BY a.dataAnam DESC LIMIT 1")
    Anamnese findUltimaAnamnese(@Param("idPaciente") Integer idPaciente);
    
    // Anamneses funcionais ativas
    @Query("SELECT a FROM Anamnese a WHERE a.statusFunc = 1 ORDER BY a.dataAnam DESC")
    List<Anamnese> findAnamnesesFuncionais();
    
    // ===== NOVOS MÉTODOS PARA ANAMNESES PENDENTES =====
    
    // Contar anamneses pendentes (não aprovadas e status funcional = 0)
    @Query("SELECT COUNT(a) FROM Anamnese a WHERE a.statusAnm != 'APROVADO' AND a.statusFunc = 0")
    Long countAnamnesesPendentes();
    
    // Contar anamneses pendentes por paciente
    @Query("SELECT COUNT(a) FROM Anamnese a WHERE a.idPaciente = :idPaciente AND a.statusAnm != 'APROVADO'")
    Long countAnamnesesPendentesByPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Buscar anamneses pendentes (aguardando aprovação)
    @Query("SELECT a FROM Anamnese a WHERE a.statusAnm != 'APROVADO' AND a.statusFunc = 0 ORDER BY a.dataAnam DESC")
    List<Anamnese> findAnamnesesPendentes();
    
    // Buscar anamneses pendentes por paciente
    @Query("SELECT a FROM Anamnese a WHERE a.idPaciente = :idPaciente AND a.statusAnm != 'APROVADO' ORDER BY a.dataAnam DESC")
    List<Anamnese> findAnamnesesPendentesByPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Buscar anamneses pendentes de fisioterapia (via conselho do profissional)
    @Query("SELECT a FROM Anamnese a " +
           "JOIN Profissional p ON a.idProfissio = p.idProfissio " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE a.statusAnm != 'APROVADO' " +
           "AND (UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%') " +
           "ORDER BY a.dataAnam DESC")
    List<Anamnese> findAnamnesesPendentesFisioterapia();
    
    // Contar anamneses pendentes de fisioterapia
    @Query("SELECT COUNT(a) FROM Anamnese a " +
           "JOIN Profissional p ON a.idProfissio = p.idProfissio " +
           "JOIN ConseProfi c ON p.idConseProfi = c.idConseProfi " +
           "WHERE a.statusAnm != 'APROVADO' " +
           "AND (UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%')")
    Long countAnamnesesPendentesFisioterapia();
}

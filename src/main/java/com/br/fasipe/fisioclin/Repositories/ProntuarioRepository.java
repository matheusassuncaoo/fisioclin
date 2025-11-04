package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Integer> {
    
    // Buscar prontuários por paciente ordenados por data (EVOLUÇÃO CRONOLÓGICA)
    List<Prontuario> findByIdPacienteOrderByDataProcedDesc(Integer idPaciente);
    
    // Buscar prontuários por paciente e período
    @Query("SELECT p FROM Prontuario p WHERE p.idPaciente = :idPaciente AND p.dataProced BETWEEN :dataInicio AND :dataFim ORDER BY p.dataProced DESC")
    List<Prontuario> findByPacienteAndPeriodo(
        @Param("idPaciente") Integer idPaciente,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
    
    // Buscar prontuários por profissional
    List<Prontuario> findByIdProfissioOrderByDataProcedDesc(Integer idProfissio);
    
    // Buscar prontuários por especialidade
    List<Prontuario> findByIdEspecOrderByDataProcedDesc(Integer idEspec);
    
    // Buscar prontuários por procedimento
    List<Prontuario> findByIdProcedOrderByDataProcedDesc(Integer idProced);
    
    // Último prontuário do paciente
    @Query("SELECT p FROM Prontuario p WHERE p.idPaciente = :idPaciente ORDER BY p.dataProced DESC LIMIT 1")
    Prontuario findUltimoProntuario(@Param("idPaciente") Integer idPaciente);
    
    // Prontuários visíveis ao paciente
    @Query("SELECT p FROM Prontuario p WHERE p.idPaciente = :idPaciente AND p.autoPacVisu = true ORDER BY p.dataProced DESC")
    List<Prontuario> findProntuariosVisiveisPaciente(@Param("idPaciente") Integer idPaciente);
    
    // Contar prontuários do paciente
    Long countByIdPaciente(Integer idPaciente);
}

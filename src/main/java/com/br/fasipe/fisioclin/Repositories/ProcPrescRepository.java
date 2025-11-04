package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.ProcPresc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcPrescRepository extends JpaRepository<ProcPresc, Integer> {
    
    // Buscar prescrições por anamnese
    List<ProcPresc> findByIdAnamnese(Integer idAnamnese);
    
    // Buscar prescrições por procedimento
    List<ProcPresc> findByIdProced(Integer idProced);
    
    // Buscar prescrições por paciente (através da anamnese)
    @Query("SELECT pp FROM ProcPresc pp " +
           "JOIN Anamnese a ON pp.idAnamnese = a.idAnamnese " +
           "WHERE a.idPaciente = :idPaciente")
    List<ProcPresc> findByPaciente(@Param("idPaciente") Integer idPaciente);
}

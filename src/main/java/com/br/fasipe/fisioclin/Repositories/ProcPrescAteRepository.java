package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.ProcPrescAte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcPrescAteRepository extends JpaRepository<ProcPrescAte, Integer> {
    
    // Buscar por prescrição
    List<ProcPrescAte> findByIdProcPresc(Integer idProcPresc);
    
    // Buscar por prontuário
    List<ProcPrescAte> findByIdProntu(Integer idProntu);
    
    // Buscar atendimentos concluídos
    @Query("SELECT ppa FROM ProcPrescAte ppa WHERE ppa.statusAte = 1")
    List<ProcPrescAte> findByStatusAteTrue();
    
    // Buscar atendimentos pendentes
    @Query("SELECT ppa FROM ProcPrescAte ppa WHERE ppa.statusAte = 0")
    List<ProcPrescAte> findByStatusAteFalse();
    
    // Verificar se prescrição tem atendimentos pendentes
    @Query("SELECT CASE WHEN COUNT(ppa) > 0 THEN true ELSE false END FROM ProcPrescAte ppa WHERE ppa.idProcPresc = :idProcPresc AND ppa.statusAte = 0")
    Boolean hasPendencias(@Param("idProcPresc") Integer idProcPresc);
}

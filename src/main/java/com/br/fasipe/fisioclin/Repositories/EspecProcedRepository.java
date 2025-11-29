package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.EspecProced;
import com.br.fasipe.fisioclin.Models.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecProcedRepository extends JpaRepository<EspecProced, Integer> {
    
    // Buscar vínculos por especialidade
    List<EspecProced> findByIdEspec(Integer idEspec);
    
    // Buscar vínculos por procedimento
    List<EspecProced> findByIdProced(Integer idProced);
    
    /**
     * Buscar todos os procedimentos de uma especialidade específica
     * @param idEspec ID da especialidade
     * @return Lista de procedimentos
     */
    @Query("SELECT ep.procedimento FROM EspecProced ep WHERE ep.idEspec = :idEspec ORDER BY ep.procedimento.descrProc")
    List<Procedimento> findProcedimentosByEspecialidade(@Param("idEspec") Integer idEspec);
    
    /**
     * Buscar procedimentos de fisioterapia (código 30)
     * @param codEspec Código da especialidade
     * @return Lista de procedimentos
     */
    @Query("SELECT ep.procedimento FROM EspecProced ep " +
           "JOIN Especialidade e ON ep.idEspec = e.idEspec " +
           "WHERE e.codEspec = :codEspec " +
           "ORDER BY ep.procedimento.descrProc")
    List<Procedimento> findProcedimentosByCodEspec(@Param("codEspec") String codEspec);
    
    /**
     * Verificar se um procedimento pertence a uma especialidade
     */
    boolean existsByIdProcedAndIdEspec(Integer idProced, Integer idEspec);
}


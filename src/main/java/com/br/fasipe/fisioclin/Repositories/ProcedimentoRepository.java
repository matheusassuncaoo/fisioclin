package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcedimentoRepository extends JpaRepository<Procedimento, Integer> {
    
    // Buscar por código
    Optional<Procedimento> findByCodProced(String codProced);
    
    // Buscar por descrição (like)
    @Query("SELECT p FROM Procedimento p WHERE LOWER(p.descrProc) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Procedimento> findByDescricaoContaining(@Param("descricao") String descricao);
    
    // Buscar procedimentos ordenados por valor
    List<Procedimento> findAllByOrderByValorProcAsc();
}

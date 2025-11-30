package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.ConseProfi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConseProfiRepository extends JpaRepository<ConseProfi, Integer> {
    
    // Buscar por sigla do conselho
    Optional<ConseProfi> findBySiglaCons(String siglaCons);
    
    // Buscar por descrição (parcial)
    @Query("SELECT c FROM ConseProfi c WHERE LOWER(c.descrCons) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<ConseProfi> findByDescricaoContaining(@Param("descricao") String descricao);
    
    // Buscar conselho de fisioterapia (CREFITO)
    @Query("SELECT c FROM ConseProfi c WHERE UPPER(c.siglaCons) = 'CREFITO' OR UPPER(c.descrCons) LIKE '%FISIOTER%'")
    Optional<ConseProfi> findConselhoFisioterapia();
}


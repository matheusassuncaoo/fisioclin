package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Integer> {
    
    // Buscar por código da especialidade
    Optional<Especialidade> findByCodEspec(String codEspec);
    
    // Buscar por descrição (like)
    java.util.List<Especialidade> findByDescEspecContainingIgnoreCase(String descricao);
}


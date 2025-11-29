package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Integer> {
    
    // Verificar se profissional existe
    boolean existsByIdProfissio(Integer idProfissio);
}


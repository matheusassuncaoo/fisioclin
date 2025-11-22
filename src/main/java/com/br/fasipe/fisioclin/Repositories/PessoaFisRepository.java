package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaFisRepository extends JpaRepository<PessoaFis, Integer> {
    
    // Buscar por ID da pessoa (chave estrangeira)
    Optional<PessoaFis> findByIdPessoa(Integer idPessoa);
    
    // Buscar por documento
    Optional<PessoaFis> findByIdDocumento(BigInteger idDocumento);
    
    // Buscar por sexo
    List<PessoaFis> findBySexoPessoa(PessoaFis.Sexo sexoPessoa);
}


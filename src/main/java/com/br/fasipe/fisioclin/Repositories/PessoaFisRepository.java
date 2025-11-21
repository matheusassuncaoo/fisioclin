package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaFisRepository extends JpaRepository<PessoaFis, Integer> {
    
    Optional<PessoaFis> findByCpfPessoa(String cpfPessoa);
    
    Optional<PessoaFis> findByIdPessoa(Integer idPessoa);
    
    List<PessoaFis> findByNomePessoaContainingIgnoreCase(String nome);
    
    List<PessoaFis> findBySexoPessoa(String sexo);
    
    List<PessoaFis> findByDataNascPesBetween(LocalDate dataInicio, LocalDate dataFim);
}

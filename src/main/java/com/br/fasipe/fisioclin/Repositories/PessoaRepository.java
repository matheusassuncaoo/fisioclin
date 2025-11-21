package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    List<Pessoa> findByTipoPessoa(String tipoPessoa);
}

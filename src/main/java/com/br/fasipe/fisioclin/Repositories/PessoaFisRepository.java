package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // Buscar nome do paciente pelo ID
    // Usa subquery para encontrar o idDocumento do paciente e depois busca o nome
    @Query("SELECT p.nomePessoa FROM PessoaFis pf " +
           "JOIN pf.pessoa p " +
           "WHERE pf.idDocumento = (SELECT pac.idDocumento FROM Paciente pac WHERE pac.idPaciente = :idPaciente)")
    Optional<String> findNomePaciente(@Param("idPaciente") Integer idPaciente);
    
    // Buscar nome do profissional pelo ID
    // Usa subquery para encontrar o idDocumento do profissional e depois busca o nome
    @Query("SELECT p.nomePessoa FROM PessoaFis pf " +
           "JOIN pf.pessoa p " +
           "WHERE pf.idDocumento = (SELECT pr.idDocumento FROM Profissional pr WHERE pr.idProfissio = :idProfissio)")
    Optional<String> findNomeProfissional(@Param("idProfissio") Integer idProfissio);
    
    // Método alternativo sem JOIN (mais seguro)
    @Query(value = "SELECT pe.NOMEPESSOA FROM PESSOAFIS pf " +
           "INNER JOIN PESSOA pe ON pf.ID_PESSOA = pe.IDPESSOA " +
           "INNER JOIN PROFISSIONAL pr ON pf.ID_DOCUMENTO = pr.ID_DOCUMENTO " +
           "WHERE pr.IDPROFISSIO = :idProfissio LIMIT 1", nativeQuery = true)
    Optional<String> findNomeProfissionalNative(@Param("idProfissio") Integer idProfissio);
}


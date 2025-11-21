package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    
    // Buscar por RG
    Optional<Paciente> findByRgPaciente(String rgPaciente);
    
    // Listar apenas pacientes ativos
    List<Paciente> findByStatusPacTrue();
    
    // Listar apenas pacientes inativos
    List<Paciente> findByStatusPacFalse();
    
    // Contar pacientes ativos
    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.statusPac = true")
    Long countPacientesAtivos();
    
    // Verificar se paciente existe e está ativo
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Paciente p WHERE p.idPaciente = :id AND p.statusPac = true")
    Boolean isPacienteAtivo(@Param("id") Integer id);
    
    // Buscar pacientes com nome da pessoa física
    @Query(value = "SELECT p.IDPACIENTE as idPaciente, p.ID_PESSOAFIS as idPessoaFis, " +
                   "p.RGPACIENTE as rgPaciente, p.ESTDORGPAC as estdoRgPac, p.STATUSPAC as statusPac, " +
                   "pf.NOMEPESSOA as nomePessoa, pf.CPFPESSOA as cpfPessoa, " +
                   "pf.DATANASCPES as dataNascPes, pf.SEXOPESSOA as sexoPessoa " +
                   "FROM PACIENTE p " +
                   "INNER JOIN PESSOAFIS pf ON p.ID_PESSOAFIS = pf.IDPESSOAFIS " +
                   "WHERE p.STATUSPAC = true " +
                   "ORDER BY pf.NOMEPESSOA", 
           nativeQuery = true)
    List<Map<String, Object>> findAllPacientesComNomeAtivos();
    
    @Query(value = "SELECT p.IDPACIENTE as idPaciente, p.ID_PESSOAFIS as idPessoaFis, " +
                   "p.RGPACIENTE as rgPaciente, p.ESTDORGPAC as estdoRgPac, p.STATUSPAC as statusPac, " +
                   "pf.NOMEPESSOA as nomePessoa, pf.CPFPESSOA as cpfPessoa, " +
                   "pf.DATANASCPES as dataNascPes, pf.SEXOPESSOA as sexoPessoa " +
                   "FROM PACIENTE p " +
                   "INNER JOIN PESSOAFIS pf ON p.ID_PESSOAFIS = pf.IDPESSOAFIS " +
                   "WHERE p.STATUSPAC = false " +
                   "ORDER BY pf.NOMEPESSOA", 
           nativeQuery = true)
    List<Map<String, Object>> findAllPacientesComNomeInativos();
    
    @Query(value = "SELECT p.IDPACIENTE as idPaciente, p.ID_PESSOAFIS as idPessoaFis, " +
                   "p.RGPACIENTE as rgPaciente, p.ESTDORGPAC as estdoRgPac, p.STATUSPAC as statusPac, " +
                   "pf.NOMEPESSOA as nomePessoa, pf.CPFPESSOA as cpfPessoa, " +
                   "pf.DATANASCPES as dataNascPes, pf.SEXOPESSOA as sexoPessoa " +
                   "FROM PACIENTE p " +
                   "INNER JOIN PESSOAFIS pf ON p.ID_PESSOAFIS = pf.IDPESSOAFIS " +
                   "ORDER BY pf.NOMEPESSOA", 
           nativeQuery = true)
    List<Map<String, Object>> findAllPacientesComNome();
    
    @Query(value = "SELECT p.IDPACIENTE as idPaciente, p.ID_PESSOAFIS as idPessoaFis, " +
                   "p.RGPACIENTE as rgPaciente, p.ESTDORGPAC as estdoRgPac, p.STATUSPAC as statusPac, " +
                   "pf.NOMEPESSOA as nomePessoa, pf.CPFPESSOA as cpfPessoa, " +
                   "pf.DATANASCPES as dataNascPes, pf.SEXOPESSOA as sexoPessoa " +
                   "FROM PACIENTE p " +
                   "INNER JOIN PESSOAFIS pf ON p.ID_PESSOAFIS = pf.IDPESSOAFIS " +
                   "WHERE p.IDPACIENTE = :id", 
           nativeQuery = true)
    Optional<Map<String, Object>> findPacienteComNomeById(@Param("id") Integer id);
}

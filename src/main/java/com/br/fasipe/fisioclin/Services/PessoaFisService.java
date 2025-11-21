package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import com.br.fasipe.fisioclin.Repositories.PessoaFisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PessoaFisService {

    @Autowired
    private PessoaFisRepository pessoaFisRepository;

    public List<PessoaFis> listarTodos() {
        return pessoaFisRepository.findAll();
    }

    public Optional<PessoaFis> buscarPorId(Integer id) {
        return pessoaFisRepository.findById(id);
    }

    public Optional<PessoaFis> buscarPorCpf(String cpf) {
        return pessoaFisRepository.findByCpfPessoa(cpf);
    }

    public List<PessoaFis> buscarPorNome(String nome) {
        return pessoaFisRepository.findByNomePessoaContainingIgnoreCase(nome);
    }

    public List<PessoaFis> buscarPorSexo(String sexo) {
        return pessoaFisRepository.findBySexoPessoa(sexo);
    }

    public List<PessoaFis> buscarPorDataNascimento(LocalDate dataInicio, LocalDate dataFim) {
        return pessoaFisRepository.findByDataNascPesBetween(dataInicio, dataFim);
    }

    public PessoaFis salvar(PessoaFis pessoaFis) {
        return pessoaFisRepository.save(pessoaFis);
    }

    public PessoaFis atualizar(Integer id, PessoaFis pessoaFisAtualizada) {
        return pessoaFisRepository.findById(id)
                .map(pessoaFis -> {
                    pessoaFis.setIdPessoa(pessoaFisAtualizada.getIdPessoa());
                    pessoaFis.setCpfPessoa(pessoaFisAtualizada.getCpfPessoa());
                    pessoaFis.setNomePessoa(pessoaFisAtualizada.getNomePessoa());
                    pessoaFis.setDataNascPes(pessoaFisAtualizada.getDataNascPes());
                    pessoaFis.setSexoPessoa(pessoaFisAtualizada.getSexoPessoa());
                    return pessoaFisRepository.save(pessoaFis);
                })
                .orElseThrow(() -> new RuntimeException("PessoaFis não encontrada com ID: " + id));
    }

    public void deletar(Integer id) {
        pessoaFisRepository.deleteById(id);
    }
}

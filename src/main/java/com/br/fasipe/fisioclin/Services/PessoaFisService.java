package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.PessoaFis;
import com.br.fasipe.fisioclin.Repositories.PessoaFisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PessoaFis salvar(PessoaFis pessoaFis) {
        return pessoaFisRepository.save(pessoaFis);
    }

    public PessoaFis atualizar(Integer id, PessoaFis pessoaFisAtualizada) {
        return pessoaFisRepository.findById(id)
                .map(pessoaFis -> {
                    pessoaFis.setIdPessoa(pessoaFisAtualizada.getIdPessoa());
                    pessoaFis.setIdDocumento(pessoaFisAtualizada.getIdDocumento());
                    pessoaFis.setSexoPessoa(pessoaFisAtualizada.getSexoPessoa());
                    pessoaFis.setDataCriacao(pessoaFisAtualizada.getDataCriacao());
                    return pessoaFisRepository.save(pessoaFis);
                })
                .orElseThrow(() -> new RuntimeException("PessoaFis não encontrada com ID: " + id));
    }

    public void deletar(Integer id) {
        pessoaFisRepository.deleteById(id);
    }
}

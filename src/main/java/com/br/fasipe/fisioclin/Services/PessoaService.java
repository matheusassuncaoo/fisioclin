package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.Pessoa;
import com.br.fasipe.fisioclin.Repositories.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public List<Pessoa> listarTodos() {
        return pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPorId(Integer id) {
        return pessoaRepository.findById(id);
    }

    public List<Pessoa> buscarPorTipo(String tipoPessoa) {
        return pessoaRepository.findByTipoPessoa(tipoPessoa);
    }

    public Pessoa salvar(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    public Pessoa atualizar(Integer id, Pessoa pessoaAtualizada) {
        return pessoaRepository.findById(id)
                .map(pessoa -> {
                    pessoa.setTipoPessoa(pessoaAtualizada.getTipoPessoa());
                    return pessoaRepository.save(pessoa);
                })
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com ID: " + id));
    }

    public void deletar(Integer id) {
        pessoaRepository.deleteById(id);
    }
}

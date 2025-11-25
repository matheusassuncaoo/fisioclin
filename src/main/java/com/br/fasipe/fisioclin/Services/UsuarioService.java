package com.br.fasipe.fisioclin.Services;

import com.br.fasipe.fisioclin.Models.Usuario;
import com.br.fasipe.fisioclin.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // listar todos os usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // buscar por idUsuario
    public Usuario buscarPorIdUsuario(Integer idUsuario) {
        return usuarioRepository.findByIdUsuario(idUsuario);
    }

    // buscar usuario por idProfissional
    public Usuario buscarPorIdProfissional(Integer idProfissional) {
        return usuarioRepository.findByIdProfissional(idProfissional);
    }

    // buscar por pessoa Fisica
    public Usuario buscarPorPessoaFis(Integer idPessoaFis) {
        return usuarioRepository.findByIdPessoaFis(idPessoaFis);
    }

    // buscar por login usuario
    public Usuario buscarPorLoginUsuario(String loginUsuario) {
        return usuarioRepository.findByLoginUsuario(loginUsuario);
    }

    // salvar usuario quando tem login e senha validos
    public Usuario salvarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // editar usuario se ele existir
    public Usuario editarUsuario(Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findByIdUsuario(usuario.getIdUsuario());
        if (usuarioExistente != null) {
            return usuarioRepository.save(usuario);
        } else {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }

    //deletar usuario se o master existir e usuario é VALIDO
    public void deletarUsuario(Integer idUsuario) {
        Usuario usuarioExistente = usuarioRepository.findByIdUsuario(idUsuario);
        if (usuarioExistente != null) {
            usuarioRepository.deleteById(idUsuario);
        } else {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }
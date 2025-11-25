package com.br.fasipe.fisioclin.Repositories;

import com.br.fasipe.fisioclin.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    //buscar todos os usuarios por idProfissional
    Usuario findByIdProfissional(Integer idProfissional);

    //buscar todos os usuarios por idUsuario
    Usuario findByIdUsuario(Integer idUsuario);

    //buscar todos os usuarios por login e senha
    Usuario findByLoginUsuarioAndSenhaUsuario(String loginUsuario, String senhaUsuario);

    //buscar todos os usuario
    List<Usuario> findAll();

    //buscar todos os usuarios por idPessoaFis
    Usuario findByIdPessoaFis(Integer idPessoaFis);

    //buscar usuario por login
    Usuario findByLoginUsuario(String loginUsuario);



    

}

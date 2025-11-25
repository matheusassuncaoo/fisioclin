package com.br.fasipe.fisioclin.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUSUARIO")
    private Integer idUsuario;

    @Column(name = "ID_PROFISSIONAL", unique = true)
    private Integer idProfissional;

    @Column(name = "ID_PESSOAFIS", nullable = false, unique = true)
    private Integer idPessoaFis;

    @Column(name = "LOGUSUARIO", nullable = true, unique = true, length = 100)
    private String loginUsuario;

    @Column(name = "SENHAUSUA", nullable = false, length = 250)
    private String senhaUsuario;
}
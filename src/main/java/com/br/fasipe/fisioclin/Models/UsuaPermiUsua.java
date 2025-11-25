package com.br.fasipe.fisioclin.Models;


@Entity
@Table(name = "USUAPERMIUSUA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuaPermiUsua {
    // Classe de modelo para representar a relação entre Usuário e Permissão de Usuário
    // Implementação dos atributos e métodos conforme necessário

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUSUAPERMIUSUA", unique = true, nullable = false)
    private Integer idUsuaPermiUsua;

    @Column(name = "IDUSUARIO", nullable = false)
    private Integer idUsuario;

    @Column(name = "ID_PERMIUSUA", nullable = false)
    private Integer idPermiUsua;


}
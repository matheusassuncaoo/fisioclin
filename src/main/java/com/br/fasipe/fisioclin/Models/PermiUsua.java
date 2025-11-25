package com.br.fasipe.fisioclin.Models;

import java.beans.ConstructorProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "EXERCREALIZADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermiUsua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPERMIUSUA")
    private Integer idPermiUsua;

    @Column(name = "DESCPERMI", nullable = false, length = 50)
    private String descPermi;
    
}
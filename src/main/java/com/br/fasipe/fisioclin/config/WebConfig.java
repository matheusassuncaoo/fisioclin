package com.br.fasipe.fisioclin.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configurações Web adicionais
 * 
 * NOTA: A configuração CORS principal foi movida para SecurityConfig
 * para evitar conflitos entre CorsFilter e Spring Security
 */
@Configuration
public class WebConfig {
    // A configuração CORS está no SecurityConfig.corsConfigurationSource()
    // para garantir integração correta com Spring Security
}

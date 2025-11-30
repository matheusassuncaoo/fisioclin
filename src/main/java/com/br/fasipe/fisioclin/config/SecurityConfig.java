package com.br.fasipe.fisioclin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de segurança do sistema FisioClin
 * 
 * IMPORTANTE: Em produção, configurar autenticação JWT ou OAuth2
 * Por enquanto, mantém endpoints abertos para desenvolvimento,
 * mas com headers de segurança ativados.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500}")
    private String corsAllowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configuração CORS para Spring Security
     * Lê origens permitidas da variável de ambiente CORS_ALLOWED_ORIGINS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciais
        config.setAllowCredentials(true);
        
        // Ler origens da variável de ambiente (separadas por vírgula)
        List<String> origins = Arrays.asList(corsAllowedOrigins.split(","));
        
        // Adicionar padrões de localhost para desenvolvimento
        config.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://*.onrender.com"
        ));
        
        // Adicionar origens específicas da config
        config.setAllowedOrigins(origins);
        
        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Headers expostos
        config.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count", "X-RateLimit-Remaining"));
        
        // Cache preflight
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configuração para ambiente de DESENVOLVIMENTO
     * Permite acesso a todos os endpoints, mas com headers de segurança
     */
    @Bean
    @Profile({"dev", "default"})
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF - Desabilitado para APIs REST stateless
            // Em produção com sessões, habilitar!
            .csrf(csrf -> csrf.disable())
            
            // CORS - usar a configuração definida acima
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Headers de Segurança
            .headers(headers -> headers
                // Proteção contra XSS
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                // Previne MIME sniffing
                .contentTypeOptions(contentType -> {})
                // Previne clickjacking
                .frameOptions(frame -> frame.deny())
                // Cache control para dados sensíveis
                .cacheControl(cache -> {})
                // Content Security Policy básico
                .contentSecurityPolicy(csp -> 
                    csp.policyDirectives("default-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com https://cdn.jsdelivr.net https://unpkg.com; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://unpkg.com; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                        "img-src 'self' data:; " +
                        "font-src 'self' data: https://fonts.gstatic.com; " +
                        "connect-src 'self' https://cdn.jsdelivr.net https://unpkg.com;"))
            )
            
            // Sessão stateless para API REST
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Autorização - DEV: permite tudo
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos
                .requestMatchers(
                    "/",
                    "/frontend/**",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()
                // Swagger/OpenAPI
                .requestMatchers(
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                // Health check
                .requestMatchers("/actuator/health").permitAll()
                // DEV: permite todos os endpoints de API
                .requestMatchers("/api/**").permitAll()
                // Qualquer outra requisição
                .anyRequest().permitAll()
            );
        
        return http.build();
    }

    /**
     * Configuração para ambiente de PRODUÇÃO
     * Requer autenticação para todos os endpoints de API
     * TODO: Implementar JWT ou OAuth2 antes de ir para produção
     */
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF - Desabilitado para API REST stateless
            .csrf(csrf -> csrf.disable())
            
            // CORS - usar a configuração definida
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Headers de Segurança (mais restritivos em prod)
            .headers(headers -> headers
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(contentType -> {})
                .frameOptions(frame -> frame.deny())
                .cacheControl(cache -> {})
                .contentSecurityPolicy(csp -> 
                    csp.policyDirectives("default-src 'self'; script-src 'self' https://cdn.jsdelivr.net https://unpkg.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:;"))
                // HSTS para forçar HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true))
            )
            
            // Sessão stateless
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Autorização - PROD: mais restritiva
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos
                .requestMatchers(
                    "/",
                    "/frontend/**",
                    "/static/**",
                    "/css/**",
                    "/js/**"
                ).permitAll()
                // Swagger - desabilitar em prod ou proteger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").denyAll()
                // Health check público
                .requestMatchers("/actuator/health").permitAll()
                // APIs de autenticação
                .requestMatchers("/api/auth/**").permitAll()
                // Outras APIs requerem autenticação
                // TODO: Descomentar quando implementar JWT
                // .requestMatchers("/api/**").authenticated()
                .requestMatchers("/api/**").permitAll() // Temporário
                .anyRequest().denyAll()
            );
        
        return http.build();
    }
}

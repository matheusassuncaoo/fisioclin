package com.br.fasipe.fisioclin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring MVC
 * Registra interceptors e outras configurações de request handling
 */
@Configuration
@EnableScheduling
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Rate limiting para endpoints de API
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/health",
                "/api-docs/**",
                "/swagger-ui/**"
            );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configura o handler para servir arquivos estáticos do frontend
        // O Spring Boot já serve arquivos de classpath:/static/ na raiz por padrão,
        // mas explicitamos aqui para garantir
        registry.addResourceHandler("/frontend/**")
            .addResourceLocations("classpath:/static/frontend/");
        
        // Configura o handler para servir Assets (CSS, JS, etc) acessíveis via /Assets/
        registry.addResourceHandler("/Assets/**")
            .addResourceLocations("classpath:/static/frontend/Assets/");
    }
    
    /**
     * Limpa entradas antigas do rate limiter a cada 5 minutos
     * Evita memory leak em servidores de longa execução
     */
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void cleanupRateLimiter() {
        rateLimitInterceptor.cleanupOldEntries();
    }
}


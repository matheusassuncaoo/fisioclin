package com.br.fasipe.fisioclin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interceptor para Rate Limiting simples
 * Protege contra ataques de força bruta e DDoS
 * 
 * Configuração:
 * - Máximo de 100 requisições por minuto por IP
 * - Reset automático a cada minuto
 * 
 * NOTA: Em produção, usar solução mais robusta como Redis ou Bucket4j
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final int MAX_REQUESTS_PER_MINUTE_WRITE = 30; // POST, PUT, DELETE mais restritivo
    private static final long ONE_MINUTE = 60_000L;
    
    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        
        // OPTIONS sempre permitido (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
        
        // Reset se passou 1 minuto
        if (System.currentTimeMillis() - counter.startTime > ONE_MINUTE) {
            counter.reset();
        }
        
        // Limite diferente para operações de escrita
        int limit = isWriteOperation(method) ? MAX_REQUESTS_PER_MINUTE_WRITE : MAX_REQUESTS_PER_MINUTE;
        
        if (counter.getCount() >= limit) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"Limite de requisições excedido. Tente novamente em 1 minuto.\",\"status\":429}"
            );
            return false;
        }
        
        counter.increment();
        
        // Adiciona headers de rate limit
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limit - counter.getCount())));
        response.setHeader("X-RateLimit-Reset", String.valueOf((counter.startTime + ONE_MINUTE) / 1000));
        
        return true;
    }
    
    private String getClientIp(HttpServletRequest request) {
        // Tenta pegar IP real se estiver atrás de proxy
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private boolean isWriteOperation(String method) {
        return "POST".equalsIgnoreCase(method) || 
               "PUT".equalsIgnoreCase(method) || 
               "DELETE".equalsIgnoreCase(method) ||
               "PATCH".equalsIgnoreCase(method);
    }
    
    /**
     * Limpa contadores antigos periodicamente (evita memory leak)
     * Deve ser chamado por um scheduler
     */
    public void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        requestCounts.entrySet().removeIf(entry -> 
            now - entry.getValue().startTime > ONE_MINUTE * 5);
    }
    
    private static class RequestCounter {
        volatile long startTime = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);
        
        void increment() {
            count.incrementAndGet();
        }
        
        int getCount() {
            return count.get();
        }
        
        void reset() {
            startTime = System.currentTimeMillis();
            count.set(0);
        }
    }
}


package com.br.fasipe.fisioclin.config;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * Utilitário para sanitização de entrada de dados
 * Protege contra XSS e injection attacks
 */
@Component
public class InputSanitizer {
    
    // Padrões perigosos que devem ser removidos
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
        "on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
        "javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(--|;|'|\"|\\/\\*|\\*\\/|xp_|sp_|exec\\s|execute\\s|union\\s|select\\s|insert\\s|update\\s|delete\\s|drop\\s|alter\\s|create\\s)", 
        Pattern.CASE_INSENSITIVE);
    
    /**
     * Sanitiza texto removendo tags HTML e caracteres perigosos
     */
    public String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String result = input;
        
        // Remove scripts
        result = SCRIPT_PATTERN.matcher(result).replaceAll("");
        
        // Remove event handlers (onclick, onload, etc)
        result = EVENT_PATTERN.matcher(result).replaceAll("");
        
        // Remove javascript: protocol
        result = JAVASCRIPT_PATTERN.matcher(result).replaceAll("");
        
        // Escape HTML entities
        result = HtmlUtils.htmlEscape(result);
        
        // Remove múltiplos espaços em branco
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }
    
    /**
     * Sanitiza texto mantendo quebras de linha (para campos de texto multiline como SOAP)
     */
    public String sanitizeMultiline(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String result = input;
        
        // Remove scripts
        result = SCRIPT_PATTERN.matcher(result).replaceAll("");
        
        // Remove event handlers
        result = EVENT_PATTERN.matcher(result).replaceAll("");
        
        // Remove javascript: protocol
        result = JAVASCRIPT_PATTERN.matcher(result).replaceAll("");
        
        // Escape HTML entities mas preserva quebras de linha
        result = HtmlUtils.htmlEscape(result);
        
        return result.trim();
    }
    
    /**
     * Verifica se o texto contém possível SQL injection
     * NOTA: Isso é uma camada extra de proteção. 
     * O JPA já protege contra SQL injection via prepared statements.
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Sanitiza um código (como código de procedimento)
     * Remove tudo que não seja alfanumérico
     */
    public String sanitizeCode(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }
    
    /**
     * Sanitiza um CPF/RG (apenas números)
     */
    public String sanitizeDocument(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("[^0-9]", "");
    }
}


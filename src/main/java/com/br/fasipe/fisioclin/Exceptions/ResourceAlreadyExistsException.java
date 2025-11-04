package com.br.fasipe.fisioclin.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception lançada quando há um conflito, como tentativa de criar um recurso duplicado.
 * Retorna HTTP 409 Conflict automaticamente.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: '%s'", resourceName, fieldName, fieldValue));
    }
    
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

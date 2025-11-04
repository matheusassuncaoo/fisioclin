package com.br.fasipe.fisioclin.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception lançada quando uma operação não é permitida devido ao estado atual do recurso.
 * Exemplo: tentar agendar para um paciente inativo.
 * Retorna HTTP 403 Forbidden automaticamente.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperationNotAllowedException extends RuntimeException {
    
    public OperationNotAllowedException(String message) {
        super(message);
    }
    
    public OperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

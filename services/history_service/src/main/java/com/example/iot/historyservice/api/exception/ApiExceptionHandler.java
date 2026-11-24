package com.example.iot.historyservice.api.exception;

import com.example.iot.historyservice.dto.response.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(Exception exception) {
        String message = exception instanceof ConstraintViolationException violation
                ? violation.getConstraintViolations().stream().findFirst()
                    .map(item -> item.getMessage()).orElse("Los parámetros de la solicitud no son válidos")
                : "Los parámetros de la solicitud no son válidos";
        return ResponseEntity.badRequest().body(ApiErrorResponse.error(message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("Error consultando el historial", exception);
        return ResponseEntity.internalServerError()
                .body(ApiErrorResponse.error("No se pudo consultar el historial"));
    }
}

package com.uber.pagos.infrastructure.adapter.in.rest;

import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PagoNoEncontradoException.class)
    public ResponseEntity<ApiError> handleNoEncontrado(PagoNoEncontradoException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(EstadoPagoInvalidoException.class)
    public ResponseEntity<ApiError> handleEstadoInvalido(EstadoPagoInvalidoException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(ErrorExternoException.class)
    public ResponseEntity<ApiError> handleErrorExterno(ErrorExternoException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, mensaje, req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}

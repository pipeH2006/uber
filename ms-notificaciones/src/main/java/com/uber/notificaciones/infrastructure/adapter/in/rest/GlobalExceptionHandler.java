package com.uber.notificaciones.infrastructure.adapter.in.rest;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.exception.PlantillaNoEncontradaException;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.ApiError;
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

    @ExceptionHandler(NotificacionNoEncontradaException.class)
    public ResponseEntity<ApiError> handleNoEncontrada(NotificacionNoEncontradaException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(PlantillaNoEncontradaException.class)
    public ResponseEntity<ApiError> handlePlantillaNoEncontrada(PlantillaNoEncontradaException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ApiError> handleEstadoInvalido(EstadoInvalidoException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(EnvioNotificacionException.class)
    public ResponseEntity<ApiError> handleEnvio(EnvioNotificacionException ex, HttpServletRequest req) {
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

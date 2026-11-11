package com.uber.pagos.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}

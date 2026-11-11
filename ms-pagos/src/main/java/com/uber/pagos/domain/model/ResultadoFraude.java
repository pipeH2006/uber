package com.uber.pagos.domain.model;

public record ResultadoFraude(String riesgo, int score, String justificacion) {
}

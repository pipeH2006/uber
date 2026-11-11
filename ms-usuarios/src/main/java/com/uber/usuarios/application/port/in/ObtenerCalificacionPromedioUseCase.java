package com.uber.usuarios.application.port.in;

public interface ObtenerCalificacionPromedioUseCase {

    CalificacionResumen promedio(Long usuarioId);
}

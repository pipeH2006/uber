package com.uber.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    private Long id;
    private String destinatario;
    private Canal canal;
    private TipoNotificacion tipo;
    private String mensaje;
    private EstadoNotificacion estado;
    private LocalDateTime fechaEnvio;
    private int intentos;
}

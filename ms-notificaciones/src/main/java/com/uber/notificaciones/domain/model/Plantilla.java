package com.uber.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plantilla {
    private Long id;
    private String codigo;
    private String asunto;
    private String mensaje;
}

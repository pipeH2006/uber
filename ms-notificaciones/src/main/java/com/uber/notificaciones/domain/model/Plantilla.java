package com.uber.notificaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Plantilla reutilizable para componer mensajes. El campo {@code mensaje}
 * puede contener marcadores del tipo {@code {nombre}} que se reemplazan
 * al momento de enviar la notificación.
 */
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

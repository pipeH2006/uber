package com.uber.pagos.domain.model.gateway;

import java.util.Optional;

public interface UsuarioGateway {
    boolean usuarioExiste(Long usuarioId);

    Optional<String> obtenerChatIdTelegram(Long usuarioId);
}

package com.uber.viajes.domain.model.gateway;

import com.uber.viajes.domain.model.UsuarioResumen;

import java.util.Optional;

public interface UsuarioGateway {
    Optional<UsuarioResumen> buscarUsuario(Long usuarioId);

    Optional<String> obtenerChatIdTelegram(Long usuarioId);
}

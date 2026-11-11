package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

public interface CalificarUsuarioUseCase {

    Usuario calificar(Long usuarioId, Long calificadorId, Integer puntaje, String comentario);
}

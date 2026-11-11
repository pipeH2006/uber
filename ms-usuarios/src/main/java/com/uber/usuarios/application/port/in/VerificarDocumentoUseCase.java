package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

public interface VerificarDocumentoUseCase {

    Usuario verificarDocumento(Long id);
}

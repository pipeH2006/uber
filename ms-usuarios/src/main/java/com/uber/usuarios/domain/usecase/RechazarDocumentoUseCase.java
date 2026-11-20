package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

/**
 * Marca como RECHAZADO el documento (licencia) de un conductor. Se puede volver a verificar después.
 */
@RequiredArgsConstructor
public class RechazarDocumentoUseCase {

    private final UsuarioGateway usuarioGateway;

    public Usuario rechazarDocumento(Long id) {
        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        if (!usuario.esConductor()) {
            throw new DocumentoNoVerificableException(id);
        }
        usuario.setEstadoDocumento(EstadoDocumento.RECHAZADO);
        return usuarioGateway.guardar(usuario);
    }
}

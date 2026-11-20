package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificarDocumentoUseCase {
    private final UsuarioGateway usuarioGateway;

    public Usuario verificarDocumento(Long id) {
        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (!usuario.esConductor()) {
            throw new DocumentoNoVerificableException(id);
        }
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            throw new EstadoInvalidoException("No se puede verificar el documento de un usuario suspendido");
        }
        usuario.setEstadoDocumento(EstadoDocumento.VERIFICADO);
        return usuarioGateway.guardar(usuario);
    }
}

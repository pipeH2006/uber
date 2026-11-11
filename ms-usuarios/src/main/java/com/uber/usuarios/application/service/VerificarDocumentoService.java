package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.VerificarDocumentoUseCase;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificarDocumentoService implements VerificarDocumentoUseCase {

    private final UsuarioRepositoryPort repository;

    @Override
    public Usuario verificarDocumento(Long id) {
        Usuario usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (!usuario.esConductor()) {
            throw new DocumentoNoVerificableException(id);
        }
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            throw new EstadoInvalidoException("No se puede verificar el documento de un usuario suspendido");
        }
        usuario.setEstadoDocumento(EstadoDocumento.VERIFICADO);
        return repository.guardar(usuario);
    }
}

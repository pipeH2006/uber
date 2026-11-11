package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.UsuarioCrudUseCase;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.EmailYaRegistradoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioCrudService implements UsuarioCrudUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (repository.existePorEmail(usuario.getEmail())) {
            throw new EmailYaRegistradoException(usuario.getEmail());
        }
        usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
        usuario.setEstado(EstadoUsuario.PENDIENTE);
        usuario.setEstadoDocumento(usuario.esConductor() ? EstadoDocumento.PENDIENTE : EstadoDocumento.NO_APLICA);
        usuario.setFechaRegistro(LocalDateTime.now());
        return repository.guardar(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Override
    public List<Usuario> listar() {
        return repository.listar();
    }

    @Override
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = buscarPorId(id);
        if (datos.getNombre() != null) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getTelefono() != null) {
            existente.setTelefono(datos.getTelefono());
        }
        if (datos.getEmail() != null && !datos.getEmail().equals(existente.getEmail())) {
            if (repository.existePorEmail(datos.getEmail())) {
                throw new EmailYaRegistradoException(datos.getEmail());
            }
            existente.setEmail(datos.getEmail());
        }
        if (datos.getRol() != null) {
            existente.setRol(datos.getRol());
            existente.setEstadoDocumento(datos.getRol() == Rol.CONDUCTOR
                    ? EstadoDocumento.PENDIENTE : EstadoDocumento.NO_APLICA);
        }
        return repository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new UsuarioNoEncontradoException(id);
        }
        repository.eliminar(id);
    }
}

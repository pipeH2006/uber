package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.EmailYaRegistradoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class UsuarioCrudUseCase {
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;

    private static final int MIN_DIGITOS_TELEFONO = 7;

    public Usuario registrar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (usuarioGateway.existePorEmail(usuario.getEmail())) {
            throw new EmailYaRegistradoException(usuario.getEmail());
        }
        usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
        usuario.setEstado(EstadoUsuario.PENDIENTE);
        usuario.setEstadoDocumento(usuario.esConductor() ? EstadoDocumento.PENDIENTE : EstadoDocumento.NO_APLICA);
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuarioGateway.guardar(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public List<Usuario> listar() {
        return usuarioGateway.listar();
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = buscarPorId(id);
        if (datos.getNombre() != null) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getTelefono() != null) {
            existente.setTelefono(datos.getTelefono());
        }
        if (datos.getTelegramChatId() != null) {
            existente.setTelegramChatId(datos.getTelegramChatId());
        }
        if (datos.getEmail() != null && !datos.getEmail().equals(existente.getEmail())) {
            if (usuarioGateway.existePorEmail(datos.getEmail())) {
                throw new EmailYaRegistradoException(datos.getEmail());
            }
            existente.setEmail(datos.getEmail());
        }
        if (datos.getRol() != null) {
            existente.setRol(datos.getRol());
            existente.setEstadoDocumento(datos.getRol() == Rol.CONDUCTOR
                    ? EstadoDocumento.PENDIENTE : EstadoDocumento.NO_APLICA);
        }
        return usuarioGateway.guardar(existente);
    }

    public void eliminar(Long id) {
        if (usuarioGateway.buscarPorId(id).isEmpty()) {
            throw new UsuarioNoEncontradoException(id);
        }
        usuarioGateway.eliminar(id);
    }

    public List<Usuario> buscarPorTelefono(String telefono) {
        String buscado = soloDigitos(telefono);
        if (buscado.length() < MIN_DIGITOS_TELEFONO) {
            return List.of();
        }
        return usuarioGateway.listar().stream()
                .filter(u -> coincide(buscado, soloDigitos(u.getTelefono())))
                .toList();
    }

    private static boolean coincide(String buscado, String guardado) {
        return guardado.length() >= MIN_DIGITOS_TELEFONO
                && (buscado.endsWith(guardado) || guardado.endsWith(buscado));
    }

    private static String soloDigitos(String telefono) {
        return telefono == null ? "" : telefono.replaceAll("\\D", "");
    }
}

package com.uber.usuarios.infrastructure.adapter.out.persistence;

import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida {@link UsuarioRepositoryPort}
 * delegando en Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario guardar(Usuario usuario) {
        return toDomain(jpaRepository.save(toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<Usuario> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private UsuarioJpaEntity toEntity(Usuario u) {
        return UsuarioJpaEntity.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .contrasenaHash(u.getContrasenaHash())
                .rol(u.getRol())
                .estado(u.getEstado())
                .estadoDocumento(u.getEstadoDocumento())
                .fechaRegistro(u.getFechaRegistro())
                .build();
    }

    private Usuario toDomain(UsuarioJpaEntity e) {
        return Usuario.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .email(e.getEmail())
                .telefono(e.getTelefono())
                .contrasenaHash(e.getContrasenaHash())
                .rol(e.getRol())
                .estado(e.getEstado())
                .estadoDocumento(e.getEstadoDocumento())
                .fechaRegistro(e.getFechaRegistro())
                .build();
    }
}

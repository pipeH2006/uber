package com.uber.usuarios.infraestructure.driver_adapters.jpa_repository;

import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import com.uber.usuarios.infraestructure.mapper.MapperUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGateway {
    private final UsuarioDataJpaRepository repository;
    private final MapperUsuario mapperUsuario;

    @Override
    public Usuario guardar(Usuario usuario) {
        return mapperUsuario.toUsuario(repository.save(mapperUsuario.toData(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(mapperUsuario::toUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(mapperUsuario::toUsuario);
    }

    @Override
    public List<Usuario> listar() {
        return repository.findAll().stream().map(mapperUsuario::toUsuario).toList();
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }
}

package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.PagoCrudUseCase;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoCrudService implements PagoCrudUseCase {

    private final PagoRepositoryPort repository;

    @Override
    public Pago crear(Pago pago) {
        if (pago.getMoneda() == null || pago.getMoneda().isBlank()) {
            pago.setMoneda("USD");
        }
        if (pago.getEstado() == null) {
            pago.setEstado(EstadoPago.PENDIENTE);
        }
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDateTime.now());
        }
        return repository.guardar(pago);
    }

    @Override
    public Pago buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));
    }

    @Override
    public List<Pago> listar() {
        return repository.listar();
    }

    @Override
    public Pago actualizar(Long id, Pago datos) {
        Pago existente = buscarPorId(id);
        if (datos.getMonto() != null) {
            existente.setMonto(datos.getMonto());
        }
        if (datos.getMoneda() != null) {
            existente.setMoneda(datos.getMoneda());
        }
        if (datos.getMetodoPago() != null) {
            existente.setMetodoPago(datos.getMetodoPago());
        }
        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }
        return repository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new PagoNoEncontradoException(id);
        }
        repository.eliminar(id);
    }
}

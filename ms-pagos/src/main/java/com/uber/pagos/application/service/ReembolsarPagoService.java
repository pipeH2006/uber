package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.ReembolsarPagoUseCase;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReembolsarPagoService implements ReembolsarPagoUseCase {

    private final PagoRepositoryPort repository;

    @Override
    public Pago reembolsar(Long id) {
        Pago pago = repository.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new EstadoPagoInvalidoException(
                    "Solo se puede reembolsar un pago en estado APROBADO, estado actual: " + pago.getEstado());
        }
        pago.setEstado(EstadoPago.REEMBOLSADO);
        return repository.guardar(pago);
    }
}

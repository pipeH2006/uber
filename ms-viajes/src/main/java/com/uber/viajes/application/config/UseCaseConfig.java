package com.uber.viajes.application.config;

import com.uber.viajes.domain.model.gateway.MapsGateway;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.TipoCambioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import com.uber.viajes.domain.usecase.CancelarViajeUseCase;
import com.uber.viajes.domain.usecase.EstimarViajeUseCase;
import com.uber.viajes.domain.usecase.FinalizarViajeUseCase;
import com.uber.viajes.domain.usecase.IniciarViajeUseCase;
import com.uber.viajes.domain.usecase.SeguimientoViajeUseCase;
import com.uber.viajes.domain.usecase.SolicitarViajeUseCase;
import com.uber.viajes.domain.usecase.ViajeCrudUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los casos de uso (domain/usecase) como beans de Spring,
 * manteniendo el dominio libre de anotaciones de Spring.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public ViajeCrudUseCase viajeCrudUseCase(ViajeGateway viajeGateway) {
        return new ViajeCrudUseCase(viajeGateway);
    }

    @Bean
    public EstimarViajeUseCase estimarViajeUseCase(MapsGateway mapsGateway,
                                                    TipoCambioGateway tipoCambioGateway) {
        return new EstimarViajeUseCase(mapsGateway, tipoCambioGateway);
    }

    @Bean
    public SolicitarViajeUseCase solicitarViajeUseCase(EstimarViajeUseCase estimarViajeUseCase,
                                                       NotificacionGateway notificacionGateway,
                                                       ViajeGateway viajeGateway) {
        return new SolicitarViajeUseCase(estimarViajeUseCase, notificacionGateway, viajeGateway);
    }

    @Bean
    public IniciarViajeUseCase iniciarViajeUseCase(ViajeGateway viajeGateway) {
        return new IniciarViajeUseCase(viajeGateway);
    }

    @Bean
    public FinalizarViajeUseCase finalizarViajeUseCase(ViajeGateway viajeGateway,
                                                       NotificacionGateway notificacionGateway) {
        return new FinalizarViajeUseCase(viajeGateway, notificacionGateway);
    }

    @Bean
    public CancelarViajeUseCase cancelarViajeUseCase(ViajeGateway viajeGateway) {
        return new CancelarViajeUseCase(viajeGateway);
    }

    @Bean
    public SeguimientoViajeUseCase seguimientoViajeUseCase(ViajeGateway viajeGateway,
                                                           MapsGateway mapsGateway) {
        return new SeguimientoViajeUseCase(viajeGateway, mapsGateway);
    }
}

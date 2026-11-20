package com.uber.pagos.application.config;

import com.uber.pagos.domain.model.gateway.FraudeGateway;
import com.uber.pagos.domain.model.gateway.NotificacionGateway;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
import com.uber.pagos.domain.model.gateway.UsuarioGateway;
import com.uber.pagos.domain.usecase.ConsultarTipoCambioUseCase;
import com.uber.pagos.domain.usecase.EvaluarFraudeUseCase;
import com.uber.pagos.domain.usecase.GenerarFacturaUseCase;
import com.uber.pagos.domain.usecase.PagoCrudUseCase;
import com.uber.pagos.domain.usecase.ProcesarPagoUseCase;
import com.uber.pagos.domain.usecase.ReembolsarPagoUseCase;
import com.uber.pagos.domain.usecase.ResumenIngresosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public PagoCrudUseCase pagoCrudUseCase(PagoGateway pagoGateway) {
        return new PagoCrudUseCase(pagoGateway);
    }

    @Bean
    public ProcesarPagoUseCase procesarPagoUseCase(PagoGateway pagoGateway,
                                                   NotificacionGateway notificacionGateway,
                                                   UsuarioGateway usuarioGateway) {
        return new ProcesarPagoUseCase(pagoGateway, notificacionGateway, usuarioGateway);
    }

    @Bean
    public ReembolsarPagoUseCase reembolsarPagoUseCase(PagoGateway pagoGateway) {
        return new ReembolsarPagoUseCase(pagoGateway);
    }

    @Bean
    public GenerarFacturaUseCase generarFacturaUseCase(PagoGateway pagoGateway,
                                                       TipoCambioGateway tipoCambioGateway) {
        return new GenerarFacturaUseCase(pagoGateway, tipoCambioGateway);
    }

    @Bean
    public EvaluarFraudeUseCase evaluarFraudeUseCase(PagoGateway pagoGateway,
                                                     FraudeGateway fraudeGateway) {
        return new EvaluarFraudeUseCase(pagoGateway, fraudeGateway);
    }

    @Bean
    public ConsultarTipoCambioUseCase consultarTipoCambioUseCase(TipoCambioGateway tipoCambioGateway) {
        return new ConsultarTipoCambioUseCase(tipoCambioGateway);
    }

    @Bean
    public ResumenIngresosUseCase resumenIngresosUseCase(PagoGateway pagoGateway) {
        return new ResumenIngresosUseCase(pagoGateway);
    }
}

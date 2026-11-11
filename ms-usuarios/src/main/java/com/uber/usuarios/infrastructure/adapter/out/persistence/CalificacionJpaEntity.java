package com.uber.usuarios.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "calificador_id", nullable = false)
    private Long calificadorId;

    @Column(nullable = false)
    private Integer puntaje;

    private String comentario;

    private LocalDateTime fecha;
}

package com.uber.pagos.infrastructure.config;

import com.uber.pagos.infrastructure.config.DatabaseUrlConfig.DatabaseUrl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseUrlConfigTest {

    @Test
    void parse_urlPostgresql_convierteAJdbc() {
        DatabaseUrl result = DatabaseUrlConfig.parse("postgresql://usuario:clave@host-ejemplo:5432/midb");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host-ejemplo:5432/midb");
        assertThat(result.username()).isEqualTo("usuario");
        assertThat(result.password()).isEqualTo("clave");
    }

    @Test
    void parse_urlPostgresSinPuerto_usa5432() {
        DatabaseUrl result = DatabaseUrlConfig.parse("postgres://user:pass@host/midb");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host:5432/midb");
        assertThat(result.username()).isEqualTo("user");
        assertThat(result.password()).isEqualTo("pass");
    }
}

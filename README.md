# 🚗 Uber — Plataforma de Ride-Hailing (Microservicios)

Proyecto académico de **arquitectura de microservicios** para un dominio de **transporte privado** (tipo Uber). Implementado con **Java 21 + Spring Boot 3.3 + PostgreSQL** y **Arquitectura Hexagonal Limpia**.

> Cada microservicio se despliega de forma **independiente** (Docker + Render). Incluye cobertura de pruebas ≥ 90% por microservicio y pruebas de carga/estrés contra las APIs desplegadas.

---

## 1. Arquitectura

```
                         ┌───────────────┐
                         │  Cliente / k6 │
                         └───────┬───────┘
                                 │ HTTP
        ┌────────────────────────┼─────────────────────────────┐
        │                        │                             │
   ┌────▼─────┐            ┌─────▼──────┐            ┌─────────▼────┐
   │ usuarios │            │   viajes   │            │    pagos     │
   │  :8081   │            │   :8082    │            │    :8083     │
   └──────────┘            └─────┬──────┘            └──────┬───────┘
        │                        │                          │
        │             Google Maps│ + API dólar      API dólar│ + OpenAI
        │                        │                          │
        │                        └──────────┬───────────────┘
        │                                   │ (HTTP)
        │                          ┌────────▼────────┐
        │                          │ notificaciones  │──▶ Telegram Bot API
        │                          │     :8084       │
        │                          └─────────────────┘
        └───────────────┬─────────────────────────────┘
                        │  (JDBC)
                 ┌──────▼───────┐
                 │  PostgreSQL  │  (1 BD por microservicio)
                 └──────────────┘
```

### Microservicios

| Microservicio | Puerto | Responsabilidad | APIs externas |
|---|---|---|---|
| `ms-usuarios` | 8081 | Pasajeros, conductores, login, verificación de documentos, calificaciones | — |
| `ms-viajes` | 8082 | Viajes: estimar tarifa, solicitar, iniciar, finalizar, cancelar, seguimiento | **Google Maps** + **API de dólar** |
| `ms-pagos` | 8083 | Pagos: procesar, reembolsar, factura, análisis de fraude, tipo de cambio, resumen | **API de dólar** + **OpenAI (IA)** |
| `ms-notificaciones` | 8084 | Notificaciones (correo/chat) por **Telegram** | **Telegram Bot API** |

**APIs externas consumidas:** Google Maps Distance Matrix, API de dólar (open.er-api.com) y OpenAI. La API de notificación (Telegram) **no** cuenta como una de las 2 externas obligatorias; es el canal del sistema de notificaciones.

---

## 2. Stack tecnológico

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.3.5 (Web, Data JPA, Validation, Actuator)
- **Base de datos:** PostgreSQL 16
- **Arquitectura:** Hexagonal (puertos y adaptadores)
- **HTTP client externo:** `RestClient` (Spring 6)
- **Pruebas:** JUnit 5 + Mockito + MockMvc + MockWebServer + H2
- **Cobertura:** JaCoCo (umbral 90% por microservicio)
- **Contenedores:** Docker (Dockerfile multi-etapa por servicio)
- **Despliegue:** Render (Web Service por microservicio + PostgreSQL)
- **Carga/estrés:** k6

---

## 3. Arquitectura Hexagonal (Limpia)

Cada microservicio sigue la misma estructura de puertos y adaptadores:

```
com.uber.<servicio>
├── domain/                     # Núcleo: modelo, enums, excepciones (sin dependencias)
│   ├── model/
│   └── exception/
├── application/
│   ├── port/in/                # Puertos de entrada (casos de uso)
│   ├── port/out/               # Puertos de salida (persistencia, APIs externas)
│   └── service/                # Implementación de los casos de uso
└── infrastructure/
    ├── adapter/in/rest/        # Controladores REST + DTOs + manejo de errores
    ├── adapter/out/persistence/# JPA: entidades + repositorios + adaptadores
    ├── adapter/out/client/     # Clientes HTTP de APIs externas (RestClient)
    └── config/                 # Configuración (beans, propiedades externas)
```

El dominio **no conoce** Spring, JPA ni HTTP. La lógica de negocio vive en `application/service` y depende solo de interfaces (`port/in` y `port/out`). Los adaptadores conectan el núcleo con el mundo exterior.

---

## 4. Endpoints

### `ms-usuarios` (`/api/v1`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/usuarios` | Registrar usuario (CRUD) |
| GET | `/usuarios/{id}` | Buscar por id (CRUD) |
| GET | `/usuarios` | Listar (CRUD) |
| PUT | `/usuarios/{id}` | Actualizar (CRUD) |
| DELETE | `/usuarios/{id}` | Eliminar (CRUD) |
| POST | `/auth/login` | Iniciar sesión |
| POST | `/usuarios/{id}/activar` | Activar cuenta |
| POST | `/usuarios/{id}/desactivar` | Desactivar cuenta |
| POST | `/usuarios/{id}/verificar-documento` | Verificar licencia del conductor |
| POST | `/usuarios/{id}/calificar` | Calificar a un usuario |
| GET | `/usuarios/{id}/calificacion-promedio` | Promedio de calificación |

### `ms-viajes` (`/api/v1`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/viajes` | Crear viaje (CRUD) |
| GET | `/viajes/{id}` | Buscar por id (CRUD) |
| GET | `/viajes` | Listar (CRUD) |
| PUT | `/viajes/{id}` | Actualizar (CRUD) |
| DELETE | `/viajes/{id}` | Eliminar (CRUD) |
| POST | `/viajes/estimar` | Estimar tarifa (Google Maps + dólar) |
| POST | `/viajes/solicitar` | Solicitar viaje y asignar conductor |
| POST | `/viajes/{id}/iniciar` | Iniciar viaje |
| POST | `/viajes/{id}/finalizar` | Finalizar viaje (calcula costo final) |
| POST | `/viajes/{id}/cancelar` | Cancelar viaje |
| GET | `/viajes/{id}/seguimiento` | Estado + ETA del viaje |

### `ms-pagos` (`/api/v1/pagos`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/pagos` | Crear pago (CRUD) |
| GET | `/pagos/{id}` | Buscar por id (CRUD) |
| GET | `/pagos` | Listar (CRUD) |
| PUT | `/pagos/{id}` | Actualizar (CRUD) |
| DELETE | `/pagos/{id}` | Eliminar (CRUD) |
| POST | `/pagos/procesar` | Procesar un pago |
| POST | `/pagos/{id}/reembolsar` | Reembolsar un pago |
| GET | `/pagos/{id}/factura` | Generar factura (USD → COP) |
| POST | `/pagos/{id}/evaluar-fraude` | Análisis de fraude con IA (OpenAI) |
| GET | `/pagos/tipo-cambio?moneda=COP` | Tipo de cambio actual |
| GET | `/pagos/resumen?fecha=2026-11-11` | Resumen de ingresos del día |

### `ms-notificaciones` (`/api/v1`)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/notificaciones` | Crear notificación (CRUD) |
| GET | `/notificaciones/{id}` | Buscar por id (CRUD) |
| GET | `/notificaciones` | Listar (CRUD) |
| PUT | `/notificaciones/{id}` | Actualizar (CRUD) |
| DELETE | `/notificaciones/{id}` | Eliminar (CRUD) |
| POST | `/notificaciones/enviar` | Enviar notificación (Telegram) |
| POST | `/notificaciones/enviar-masivo` | Envío masivo |
| GET | `/notificaciones/{id}/estado` | Estado de entrega |
| POST | `/notificaciones/{id}/reintentar` | Reintentar envío fallido |
| GET | `/notificaciones/plantillas` | Listar plantillas |
| POST | `/notificaciones/enviar-con-plantilla` | Enviar usando plantilla |

> **Total: 5 APIs de dominio (no CRUD) por microservicio**, cumpliendo el requisito de "5 APIs diferentes al CRUD" (15+ en total).

---

## 5. APIs externas (3)

1. **Google Maps — Distance Matrix API** (`ms-viajes`): calcula distancia y duración entre origen y destino.
2. **API de dólar** (`open.er-api.com`, `ms-viajes` y `ms-pagos`): tipo de cambio USD → COP para tarifas y facturas.
3. **OpenAI** (`ms-pagos`): análisis de riesgo de fraude sobre los pagos.

Además, **Telegram Bot API** (`ms-notificaciones`) es el canal del sistema de notificaciones.

> **Modo simulación:** si no configuras las claves, los adaptadores externos devuelven datos de ejemplo y el sistema sigue funcionando (útil para desarrollo y pruebas de carga sin consumir cuota).

---

## 6. Requisitos previos

- **Java 21** (JDK) con la variable `JAVA_HOME` configurada
- **Maven Wrapper incluido** (no necesitas instalar Maven; usa `mvnw.cmd` en Windows o `./mvnw` en Linux/Mac)
- **Docker** (para `docker compose` y despliegue)
- **k6** (para pruebas de carga/estrés) — <https://k6.io>

---

## 7. Ejecutar en local

### Opción A — Docker Compose (recomendada)

Levanta PostgreSQL + los 4 microservicios:

```bash
# opcional: crea un .env con tus claves (ver .env.example)
docker compose up --build
```

- `ms-usuarios`: http://localhost:8081
- `ms-viajes`: http://localhost:8082
- `ms-pagos`: http://localhost:8083
- `ms-notificaciones`: http://localhost:8084

### Opción B — Maven (un microservicio a la vez)

Requiere PostgreSQL corriendo en `localhost:5432` (con las 4 bases creadas, ver `db/init.sql`):

```bash
cd ms-usuarios
mvn spring-boot:run
```

### Probar un flujo completo

```bash
# 1. Registrar un pasajero y un conductor
curl -X POST http://localhost:8081/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","email":"ana@correo.com","telefono":"300123","contrasena":"secreto","rol":"PASAJERO"}'

# 2. Estimar una tarifa (usa Google Maps + dólar)
curl -X POST http://localhost:8082/api/v1/viajes/estimar \
  -H "Content-Type: application/json" \
  -d '{"origenLat":4.7110,"origenLng":-74.0721,"destinoLat":4.6533,"destinoLng":-74.0836}'

# 3. Procesar un pago
curl -X POST http://localhost:8083/api/v1/pagos/procesar \
  -H "Content-Type: application/json" \
  -d '{"viajeId":1,"usuarioId":1,"monto":12500.00,"moneda":"COP","metodoPago":"TARJETA"}'

# 4. Enviar una notificación por Telegram
curl -X POST http://localhost:8084/api/v1/notificaciones/enviar \
  -H "Content-Type: application/json" \
  -d '{"destinatario":"CHAT_ID","mensaje":"Tu viaje fue confirmado","tipo":"VIAJE_SOLICITADO"}'
```

---

## 8. Variables de entorno

Ver `.env.example`. Las más importantes:

| Variable | Servicio | Descripción |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | todos | Conexión PostgreSQL (acepta `jdbc:postgresql://...` o `postgres://...`) |
| `PORT` | todos | Puerto del microservicio |
| `GOOGLE_MAPS_API_KEY` | viajes | Clave de Google Maps |
| `OPENAI_API_KEY` | pagos | Clave de OpenAI |
| `TELEGRAM_BOT_TOKEN` | notificaciones | Token del bot de Telegram |
| `NOTIFICACIONES_URL` | viajes, pagos | URL de `ms-notificaciones` (para notificar eventos) |

---

## 9. Pruebas y cobertura

Cada microservicio tiene pruebas unitarias (camino feliz + triste), de controlador (`@WebMvcTest`), de persistencia (`@DataJpaTest` + H2) y de adaptadores externos (MockWebServer).

```bash
# dentro de cada carpeta de microservicio
# Windows (PowerShell):  .\mvnw.cmd test
# Linux/Mac:            ./mvnw test
mvnw.cmd test      # ejecuta tests y genera el reporte de cobertura
mvnw.cmd verify    # además valida el umbral de cobertura (>= 90%)
```

El reporte queda en `target/site/jacoco/index.html`.

| Microservicio | Tests | Cobertura de línea |
|---|---|---|
| ms-usuarios | 60 | ≥ 90% |
| ms-viajes | 61 | ≥ 90% |
| ms-pagos | 64 | ≥ 90% |
| ms-notificaciones | 51 | ≥ 90% |

---

## 10. Despliegue en Render (por microservicio)

> ⚠️ Despliega **cada microservicio por separado**, no el bloque completo.

### Opción A — Blueprint (todo de una vez, opcional)

1. Sube este repositorio a GitHub.
2. En Render: **New → Blueprint** y conecta el repo.
3. Render leerá `render.yaml` y creará los 4 servicios + 4 bases PostgreSQL.
4. Ingresa las claves (`GOOGLE_MAPS_API_KEY`, `OPENAI_API_KEY`, `TELEGRAM_BOT_TOKEN`) cuando las pida.

### Opción B — Manual (lo que suele mostrar el profe)

Repite estos pasos para **cada** microservicio:

1. **Base de datos:** Render → **New → PostgreSQL**. Copia la *Internal Connection String* (algo como `postgres://user:pass@host:5432/nombre`).
2. **Web Service:** Render → **New → Web Service** → conecta el repo GitHub.
3. En *Root Directory* pon la carpeta del microservicio (ej. `ms-usuarios`) y *Dockerfile Path* = `Dockerfile` (o usa **Docker** como runtime).
4. Configura las variables de entorno:
   - `DB_URL` = la *Internal Connection String* que copiaste (el `DatabaseUrlConfig` la convierte a JDBC automáticamente).
   - `PORT` = `8081` (o el puerto del servicio).
   - Las claves externas según el servicio.
5. **Deploy** y verifica en `https://tu-servicio.onrender.com/actuator/health`.

> Para conectar `ms-viajes`/`ms-pagos` con `ms-notificaciones`, pon `NOTIFICACIONES_URL = https://tu-ms-notificaciones.onrender.com`.

---

## 11. Pruebas de carga y estrés (k6)

> **Se ejecutan contra las APIs DESPLEGADAS, nunca en local** (requisito del trabajo autónomo).

```bash
# Carga (valida p(95) < 500 ms)
k6 run k6/load-test.js \
  -e BASE_USUARIOS=https://tu-usuarios.onrender.com \
  -e BASE_VIAJES=https://tu-viajes.onrender.com \
  -e BASE_PAGOS=https://tu-pagos.onrender.com \
  -e BASE_NOTIF=https://tu-notif.onrender.com

# Estrés (busca el punto de quiebre)
k6 run k6/stress-test.js \
  -e BASE_VIAJES=https://tu-viajes.onrender.com \
  -e BASE_PAGOS=https://tu-pagos.onrender.com
```

Los scripts incluyen *thresholds* (`http_req_duration: p(95)<500`) que hacen fallar la ejecución si no se cumple el criterio de evaluación.

---

## 12. Criterios de evaluación — cumplimiento

| Criterio | Estado |
|---|---|
| Dominio de negocio específico (ride-hailing) | ✅ |
| ≥ 3 microservicios (4 en total) | ✅ |
| Sistema de notificaciones (Telegram) | ✅ |
| 5 APIs no-CRUD por microservicio (20 totales) | ✅ |
| ≥ 2 APIs externas (Google Maps + dólar + OpenAI) | ✅ |
| Desplegado por microservicio (Docker + Render) | ✅ |
| Cobertura ≥ 90% por microservicio | ✅ (JaCoCo) |
| Pruebas de carga y estrés sobre APIs desplegadas | ✅ (k6) |
| Tiempo de respuesta < 500 ms | ✅ (threshold en k6) |
| Stack: Java + Spring Boot + PostgreSQL | ✅ |
| Arquitectura hexagonal limpia | ✅ |

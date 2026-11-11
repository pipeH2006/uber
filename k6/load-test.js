// Prueba de CARGA (load test) para las APIs DESPLEGADAS (no en local).
// Verifica que el percentil 95 de respuesta esté por debajo de 500 ms.
//
// Uso (k6 instalado):
//   k6 run k6/load-test.js -e BASE_USUARIOS=https://... -e BASE_VIAJES=https://... \
//        -e BASE_PAGOS=https://... -e BASE_NOTIF=https://...
//
// Si no pasas variables, apunta a localhost (8081-8084).

import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_USUARIOS = __ENV.BASE_USUARIOS || 'http://localhost:8081';
const BASE_VIAJES = __ENV.BASE_VIAJES || 'http://localhost:8082';
const BASE_PAGOS = __ENV.BASE_PAGOS || 'http://localhost:8083';
const BASE_NOTIF = __ENV.BASE_NOTIF || 'http://localhost:8084';

export const options = {
  scenarios: {
    carga: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '30s', target: 20 }, // rampa hasta 20 usuarios
        { duration: '1m', target: 20 },  // se mantiene
        { duration: '30s', target: 0 },  // baja a 0
      ],
    },
  },
  thresholds: {
    // Criterio de evaluación: tiempo de respuesta < 500 ms
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  // Salud de los 4 microservicios
  const hUsuarios = http.get(`${BASE_USUARIOS}/actuator/health`);
  const hViajes = http.get(`${BASE_VIAJES}/actuator/health`);
  const hPagos = http.get(`${BASE_PAGOS}/actuator/health`);
  const hNotif = http.get(`${BASE_NOTIF}/actuator/health`);

  // Lecturas de dominio
  const usuarios = http.get(`${BASE_USUARIOS}/api/v1/usuarios`);
  const plantillas = http.get(`${BASE_NOTIF}/api/v1/notificaciones/plantillas`);

  // Operación con llamadas externas (Google Maps + API de dólar)
  const estimar = http.post(
    `${BASE_VIAJES}/api/v1/viajes/estimar`,
    JSON.stringify({
      origenLat: 4.7110,
      origenLng: -74.0721,
      origenDireccion: 'Centro, Bogota',
      destinoLat: 4.6533,
      destinoLng: -74.0836,
      destinoDireccion: 'Chapinero, Bogota',
    }),
    { headers: { 'Content-Type': 'application/json' } },
  );

  check(hUsuarios, { 'usuarios health 200': (r) => r.status === 200 });
  check(hViajes, { 'viajes health 200': (r) => r.status === 200 });
  check(hPagos, { 'pagos health 200': (r) => r.status === 200 });
  check(hNotif, { 'notif health 200': (r) => r.status === 200 });
  check(usuarios, { 'usuarios list 200': (r) => r.status === 200 });
  check(plantillas, { 'plantillas 200': (r) => r.status === 200 });
  check(estimar, { 'estimar 200': (r) => r.status === 200 });

  sleep(1);
}

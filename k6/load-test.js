
import http from 'k6/http';
import { check, sleep } from 'k6';
import { calentar, TAGS_MEDICION } from './calentamiento.js';

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
        { duration: '30s', target: 20 },
        { duration: '1m', target: 20 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    'http_req_duration{fase:medicion}': ['p(95)<500'],
    'http_req_failed{fase:medicion}': ['rate<0.01'],
  },
};

export function setup() {
  calentar({ usuarios: BASE_USUARIOS, viajes: BASE_VIAJES, pagos: BASE_PAGOS, notif: BASE_NOTIF });
}

export default function () {
  const medicion = { tags: TAGS_MEDICION };

  const hUsuarios = http.get(`${BASE_USUARIOS}/actuator/health`, medicion);
  const hViajes = http.get(`${BASE_VIAJES}/actuator/health`, medicion);
  const hPagos = http.get(`${BASE_PAGOS}/actuator/health`, medicion);
  const hNotif = http.get(`${BASE_NOTIF}/actuator/health`, medicion);

  const usuarios = http.get(`${BASE_USUARIOS}/api/v1/usuarios`, medicion);
  const plantillas = http.get(`${BASE_NOTIF}/api/v1/notificaciones/plantillas`, medicion);

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
    { headers: { 'Content-Type': 'application/json' }, tags: TAGS_MEDICION },
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

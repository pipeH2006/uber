// Prueba de ESTRÉS (stress test) para las APIs DESPLEGADAS (no en local).
// Busca el punto de quiebre subiendo la carga de forma agresiva.
//
// Antes de medir, setup() despierta los servicios (ver calentamiento.js): las peticiones de
// calentamiento no cuentan en los umbrales.
//
// Uso:
//   k6 run k6/stress-test.js -e BASE_USUARIOS=https://... -e BASE_VIAJES=https://... \
//        -e BASE_PAGOS=https://... -e BASE_NOTIF=https://...

import http from 'k6/http';
import { check, sleep } from 'k6';
import { calentar, TAGS_MEDICION } from './calentamiento.js';

const BASE_USUARIOS = __ENV.BASE_USUARIOS || 'http://localhost:8081';
const BASE_VIAJES = __ENV.BASE_VIAJES || 'http://localhost:8082';
const BASE_PAGOS = __ENV.BASE_PAGOS || 'http://localhost:8083';
const BASE_NOTIF = __ENV.BASE_NOTIF || 'http://localhost:8084';

export const options = {
  scenarios: {
    estres: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '1m', target: 200 }, // punto de quiebre esperado
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    // En estrés toleramos algo más de errores, pero no colapso total.
    'http_req_failed{fase:medicion}': ['rate<0.10'],
  },
};

export function setup() {
  calentar({ usuarios: BASE_USUARIOS, viajes: BASE_VIAJES, pagos: BASE_PAGOS, notif: BASE_NOTIF });
}

export default function () {
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

  const salud = http.get(`${BASE_VIAJES}/actuator/health`, { tags: TAGS_MEDICION });
  const tipoCambio = http.get(`${BASE_PAGOS}/api/v1/pagos/tipo-cambio?moneda=COP`, { tags: TAGS_MEDICION });

  check(estimar, { 'estimar 200': (r) => r.status === 200 });
  check(salud, { 'health 200': (r) => r.status === 200 });
  check(tipoCambio, { 'tipo-cambio 200': (r) => r.status === 200 });

  sleep(0.5);
}

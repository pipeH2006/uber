
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
        { duration: '1m', target: 200 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    'http_req_failed{fase:medicion}': ['rate<0.10'],
    'http_req_duration{fase:medicion}': [{ threshold: 'p(95)<2000', abortOnFail: false }],
  },
};

export function setup() {
  calentar({ usuarios: BASE_USUARIOS, viajes: BASE_VIAJES, pagos: BASE_PAGOS, notif: BASE_NOTIF });
}

export default function () {
  const salud = http.get(`${BASE_VIAJES}/actuator/health`, { tags: TAGS_MEDICION });
  const usuarios = http.get(`${BASE_USUARIOS}/api/v1/usuarios`, { tags: TAGS_MEDICION });
  const plantillas = http.get(`${BASE_NOTIF}/api/v1/notificaciones/plantillas`, { tags: TAGS_MEDICION });
  const tipoCambio = http.get(`${BASE_PAGOS}/api/v1/pagos/tipo-cambio?moneda=COP`, { tags: TAGS_MEDICION });

  check(salud, { 'health 200': (r) => r.status === 200 });
  check(usuarios, { 'usuarios 200': (r) => r.status === 200 });
  check(plantillas, { 'plantillas 200': (r) => r.status === 200 });
  check(tipoCambio, { 'tipo-cambio 200': (r) => r.status === 200 });

  sleep(0.5);
}

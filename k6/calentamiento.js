// Calentamiento previo a las pruebas de carga y estrés.
//
// Los servicios en Render (plan gratuito) se duermen tras unos minutos sin tráfico y la base en Neon
// suspende su cómputo. La primera petición tarda varios segundos (arranque en frío) y falsearía el
// criterio de p(95) < 500 ms. Aquí se despierta todo antes de medir; estas peticiones llevan la
// etiqueta fase=calentamiento, por lo que los umbrales (fase=medicion) no las cuentan.

import http from 'k6/http';
import { sleep } from 'k6';
import exec from 'k6/execution';

export const TAGS_CALENTAMIENTO = { fase: 'calentamiento' };
export const TAGS_MEDICION = { fase: 'medicion' };

const MAX_INTENTOS = 12;
const ESPERA_ENTRE_INTENTOS_S = 5;

const CUERPO_ESTIMAR = JSON.stringify({
  origenLat: 4.7110,
  origenLng: -74.0721,
  origenDireccion: 'Centro, Bogota',
  destinoLat: 4.6533,
  destinoLng: -74.0836,
  destinoDireccion: 'Chapinero, Bogota',
});

function despertar(nombre, base) {
  const url = `${base}/actuator/health`;
  for (let intento = 1; intento <= MAX_INTENTOS; intento++) {
    const res = http.get(url, { timeout: '60s', tags: TAGS_CALENTAMIENTO });
    if (res.status === 200) {
      console.log(`${nombre} listo (intento ${intento}, ${Math.round(res.timings.duration)} ms)`);
      return;
    }
    sleep(ESPERA_ENTRE_INTENTOS_S);
  }
  exec.test.abort(`${nombre} no respondió 200 tras ${MAX_INTENTOS} intentos: ${url}`);
}

// bases: { usuarios, viajes, pagos, notif }
export function calentar(bases) {
  despertar('usuarios', bases.usuarios);
  despertar('viajes', bases.viajes);
  despertar('pagos', bases.pagos);
  despertar('notificaciones', bases.notif);

  // Calienta los caminos reales (JVM, conexión a la base, OSRM y caché del tipo de cambio).
  const params = { timeout: '60s', tags: TAGS_CALENTAMIENTO };
  const json = { ...params, headers: { 'Content-Type': 'application/json' } };
  for (let i = 0; i < 2; i++) {
    http.post(`${bases.viajes}/api/v1/viajes/estimar`, CUERPO_ESTIMAR, json);
    http.get(`${bases.usuarios}/api/v1/usuarios`, params);
    http.get(`${bases.notif}/api/v1/notificaciones/plantillas`, params);
    http.get(`${bases.pagos}/api/v1/pagos/tipo-cambio?moneda=COP`, params);
  }
}

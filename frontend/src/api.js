const USUARIOS = import.meta.env.VITE_USUARIOS_URL || 'https://uber-ms-usuarios.onrender.com';
const VIAJES = import.meta.env.VITE_VIAJES_URL || 'https://uber-ms-viajes.onrender.com';
const PAGOS = import.meta.env.VITE_PAGOS_URL || 'https://uber-ms-pagos.onrender.com';
const NOTIF = import.meta.env.VITE_NOTIF_URL || 'https://uber-ms-notificaciones.onrender.com';

export const BOT_USERNAME = import.meta.env.VITE_BOT_USERNAME || 'pipeH2006_uber_bot';

async function request(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });
  if (!res.ok) {
    let message = `Error ${res.status}`;
    try {
      const body = await res.json();
      message = body.message || message;
    } catch {
    }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  registrarUsuario: (data) =>
    request(`${USUARIOS}/api/v1/usuarios`, { method: 'POST', body: JSON.stringify(data) }),
  listarUsuarios: () => request(`${USUARIOS}/api/v1/usuarios`),
  activar: (id) => request(`${USUARIOS}/api/v1/usuarios/${id}/activar`, { method: 'POST' }),
  desactivar: (id) => request(`${USUARIOS}/api/v1/usuarios/${id}/desactivar`, { method: 'POST' }),
  suspender: (id) => request(`${USUARIOS}/api/v1/usuarios/${id}/suspender`, { method: 'POST' }),
  verificarDocumento: (id) =>
    request(`${USUARIOS}/api/v1/usuarios/${id}/verificar-documento`, { method: 'POST' }),
  rechazarDocumento: (id) =>
    request(`${USUARIOS}/api/v1/usuarios/${id}/rechazar-documento`, { method: 'POST' }),

  estimar: (data) =>
    request(`${VIAJES}/api/v1/viajes/estimar`, { method: 'POST', body: JSON.stringify(data) }),
  solicitar: (data) =>
    request(`${VIAJES}/api/v1/viajes/solicitar`, { method: 'POST', body: JSON.stringify(data) }),
  iniciar: (id) => request(`${VIAJES}/api/v1/viajes/${id}/iniciar`, { method: 'POST' }),
  finalizar: (id) => request(`${VIAJES}/api/v1/viajes/${id}/finalizar`, { method: 'POST' }),
  cancelar: (id) => request(`${VIAJES}/api/v1/viajes/${id}/cancelar`, { method: 'POST' }),
  seguimiento: (id) => request(`${VIAJES}/api/v1/viajes/${id}/seguimiento`),
  listarViajes: () => request(`${VIAJES}/api/v1/viajes`),

  procesarPago: (data) =>
    request(`${PAGOS}/api/v1/pagos/procesar`, { method: 'POST', body: JSON.stringify(data) }),
  evaluarFraude: (id) => request(`${PAGOS}/api/v1/pagos/${id}/evaluar-fraude`, { method: 'POST' }),
  tipoCambio: (moneda) => request(`${PAGOS}/api/v1/pagos/tipo-cambio?moneda=${moneda}`),
  listarPagos: () => request(`${PAGOS}/api/v1/pagos`),

  enviarNotificacion: (data) =>
    request(`${NOTIF}/api/v1/notificaciones/enviar`, { method: 'POST', body: JSON.stringify(data) }),
};

export default api;

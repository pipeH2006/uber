import { useState } from 'react';
import MapPicker from './components/MapPicker.jsx';
import api from './api.js';

export default function App() {
  const [pasajero, setPasajero] = useState(null);
  const [conductor, setConductor] = useState(null);
  const [origen, setOrigen] = useState(null);
  const [destino, setDestino] = useState(null);
  const [estimacion, setEstimacion] = useState(null);
  const [viaje, setViaje] = useState(null);
  const [pago, setPago] = useState(null);
  const [fraude, setFraude] = useState(null);
  const [chatId, setChatId] = useState('8662556785');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [log, setLog] = useState([]);

  function addLog(msg) {
    setLog((prev) => [`${new Date().toLocaleTimeString()} · ${msg}`, ...prev].slice(0, 8));
  }

  function manejarClickMapa(p) {
    if (!origen) {
      setOrigen(p);
      addLog(`Origen: ${p.lat.toFixed(4)}, ${p.lng.toFixed(4)}`);
    } else if (!destino) {
      setDestino(p);
      addLog(`Destino: ${p.lat.toFixed(4)}, ${p.lng.toFixed(4)}`);
    } else {
      setOrigen(p);
      setDestino(null);
      setEstimacion(null);
      setViaje(null);
      setPago(null);
      setFraude(null);
      addLog(`Nuevo origen: ${p.lat.toFixed(4)}, ${p.lng.toFixed(4)}`);
    }
  }

  async function registrar(e) {
    e.preventDefault();
    const form = new FormData(e.target);
    const data = {
      nombre: form.get('nombre'),
      email: form.get('email'),
      telefono: form.get('telefono'),
      contrasena: form.get('contrasena'),
      rol: form.get('rol'),
    };
    setLoading(true);
    setError('');
    try {
      const u = await api.registrarUsuario(data);
      if (u.rol === 'PASAJERO') setPasajero(u);
      else setConductor(u);
      addLog(`Usuario ${u.rol} registrado: id=${u.id} (${u.nombre})`);
      e.target.reset();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function estimar() {
    if (!origen || !destino) return;
    setLoading(true);
    setError('');
    try {
      const r = await api.estimar({
        origenLat: origen.lat,
        origenLng: origen.lng,
        destinoLat: destino.lat,
        destinoLng: destino.lng,
      });
      setEstimacion(r);
      addLog(`Tarifa estimada: $${r.costoUSD} USD · ${r.distanciaKm} km · ${r.duracionMin} min`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function solicitar() {
    if (!pasajero || !conductor || !origen || !destino) {
      setError('Necesitas registrar un pasajero y un conductor, y elegir origen/destino.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      const v = await api.solicitar({
        pasajeroId: pasajero.id,
        conductorId: conductor.id,
        origenLat: origen.lat,
        origenLng: origen.lng,
        origenDireccion: 'Origen',
        destinoLat: destino.lat,
        destinoLng: destino.lng,
        destinoDireccion: 'Destino',
      });
      setViaje(v);
      addLog(`Viaje ${v.id} solicitado: estado=${v.estado}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function accionViaje(accion) {
    if (!viaje) return;
    setLoading(true);
    setError('');
    try {
      const v = await api[accion](viaje.id);
      setViaje(v);
      addLog(`Viaje ${v.id}: ${accion} → estado=${v.estado}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function pagar() {
    if (!viaje || !pasajero) return;
    setLoading(true);
    setError('');
    try {
      const p = await api.procesarPago({
        viajeId: viaje.id,
        usuarioId: pasajero.id,
        monto: viaje.costoFinal ?? viaje.costoEstimado,
        moneda: 'USD',
        metodoPago: 'TARJETA',
      });
      setPago(p);
      addLog(`Pago ${p.id} procesado: estado=${p.estado}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function evaluarFraude() {
    if (!pago) return;
    setLoading(true);
    setError('');
    try {
      const f = await api.evaluarFraude(pago.id);
      setFraude(f);
      addLog(`Fraude (Groq): riesgo=${f.riesgo}, score=${f.score}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function notificar() {
    setLoading(true);
    setError('');
    try {
      const n = await api.enviarNotificacion({
        destinatario: chatId,
        mensaje: 'Hola desde el frontend de Uber 🚗',
        tipo: 'VIAJE_SOLICITADO',
      });
      addLog(`Notificación ${n.id} enviada: estado=${n.estado}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app">
      <header className="header">
        <h1>🚗 Uber — Ride Hailing</h1>
        <p>Microservicios · Spring Boot · OSRM · Groq · Telegram</p>
      </header>

      <div className="layout">
        <main className="col">
          <section className="card">
            <h2>🗺️ Mapa (clic para elegir origen y destino)</h2>
            <MapPicker origen={origen} destino={destino} onPick={manejarClickMapa} />
            <div className="coords">
              <span className="pill green">
                Origen: {origen ? `${origen.lat.toFixed(4)}, ${origen.lng.toFixed(4)}` : '—'}
              </span>
              <span className="pill red">
                Destino: {destino ? `${destino.lat.toFixed(4)}, ${destino.lng.toFixed(4)}` : '—'}
              </span>
            </div>
          </section>

          <section className="card">
            <h2>🛣️ Flujo del viaje</h2>
            <div className="row">
              <button onClick={estimar} disabled={loading || !origen || !destino}>
                Estimar tarifa
              </button>
              <button onClick={solicitar} disabled={loading || !pasajero || !conductor}>
                Solicitar viaje
              </button>
              <button onClick={() => accionViaje('iniciar')} disabled={loading || !viaje}>
                Iniciar
              </button>
              <button onClick={() => accionViaje('finalizar')} disabled={loading || !viaje}>
                Finalizar
              </button>
              <button onClick={() => accionViaje('cancelar')} disabled={loading || !viaje}>
                Cancelar
              </button>
            </div>

            {estimacion && (
              <div className="result">
                <strong>Estimación:</strong> {estimacion.distanciaKm} km · {estimacion.duracionMin} min ·{' '}
                <b>${estimacion.costoUSD} USD</b> · {estimacion.costoCOP} COP
              </div>
            )}
            {viaje && (
              <div className="result">
                <strong>Viaje #{viaje.id}:</strong> estado <b>{viaje.estado}</b> · costo final ${viaje.costoFinal ?? '—'}
              </div>
            )}
          </section>

          <section className="card">
            <h2>💳 Pago y fraude (Groq)</h2>
            <div className="row">
              <button onClick={pagar} disabled={loading || !viaje}>
                Procesar pago
              </button>
              <button onClick={evaluarFraude} disabled={loading || !pago}>
                Evaluar fraude (IA)
              </button>
              <button onClick={notificar} disabled={loading}>
                Enviar notificación Telegram
              </button>
            </div>
            <div className="inline">
              <label>Chat ID Telegram:</label>
              <input value={chatId} onChange={(e) => setChatId(e.target.value)} />
            </div>
            {pago && (
              <div className="result">
                <strong>Pago #{pago.id}:</strong> {pago.estado} · ref {pago.referencia}
              </div>
            )}
            {fraude && (
              <div className={`result fraud ${fraude.riesgo.toLowerCase()}`}>
                <strong>Fraude:</strong> riesgo <b>{fraude.riesgo}</b> · score {fraude.score}
                <p className="muted">{fraude.justificacion}</p>
              </div>
            )}
          </section>
        </main>

        <aside className="col side">
          <section className="card">
            <h2>👤 Registrar usuario</h2>
            <form onSubmit={registrar} className="form">
              <select name="rol" defaultValue="PASAJERO">
                <option value="PASAJERO">Pasajero</option>
                <option value="CONDUCTOR">Conductor</option>
              </select>
              <input name="nombre" placeholder="Nombre" required />
              <input name="email" type="email" placeholder="Email" required />
              <input name="telefono" placeholder="Teléfono" />
              <input name="contrasena" type="password" placeholder="Contraseña (min 6)" required minLength={6} />
              <button type="submit" disabled={loading}>
                Registrar
              </button>
            </form>
            <div className="status">
              <div>Pasajero: <b>{pasajero ? `#${pasajero.id} ${pasajero.nombre}` : '—'}</b></div>
              <div>Conductor: <b>{conductor ? `#${conductor.id} ${conductor.nombre}` : '—'}</b></div>
            </div>
          </section>

          <section className="card">
            <h2>📜 Actividad</h2>
            {error && <div className="error">{error}</div>}
            {loading && <div className="loading">Cargando…</div>}
            <ul className="log">
              {log.length === 0 && <li className="muted">Aún no hay actividad.</li>}
              {log.map((m, i) => (
                <li key={i}>{m}</li>
              ))}
            </ul>
          </section>
        </aside>
      </div>
    </div>
  );
}

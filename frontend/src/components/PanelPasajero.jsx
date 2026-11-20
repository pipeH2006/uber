import { useEffect, useState } from 'react';
import api from '../api.js';
import EstadoViaje from './EstadoViaje.jsx';
import IdentidadUsuario from './IdentidadUsuario.jsx';
import MapPicker from './MapPicker.jsx';
import MapaViaje from './MapaViaje.jsx';

const ACTIVOS = ['ASIGNADO', 'EN_CURSO'];
const dinero = (n, moneda = 'USD') => `${Number(n).toFixed(2)} ${moneda}`;

export default function PanelPasajero({ usuarios, viajes, pagos, refrescar }) {
  const [pasajeroId, setPasajeroId] = useState(() => Number(localStorage.getItem('uber.pasajeroId')) || null);
  const [origen, setOrigen] = useState(null);
  const [destino, setDestino] = useState(null);
  const [estimacion, setEstimacion] = useState(null);
  const [conductorId, setConductorId] = useState('');
  const [fraude, setFraude] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (pasajeroId) localStorage.setItem('uber.pasajeroId', String(pasajeroId));
  }, [pasajeroId]);

  const yo = usuarios.find((u) => u.id === pasajeroId && u.rol === 'PASAJERO') || null;
  const conductores = usuarios.filter((u) => u.rol === 'CONDUCTOR');
  const misViajes = viajes.filter((v) => v.pasajeroId === yo?.id).sort((a, b) => b.id - a.id);
  const activo = misViajes.find((v) => ACTIVOS.includes(v.estado)) || null;
  const ultimo = misViajes[0] || null;
  const pagoUltimo = ultimo ? pagos.find((p) => p.viajeId === ultimo.id) : null;
  const porPagar = ultimo && ultimo.estado === 'FINALIZADO' && !pagoUltimo ? ultimo : null;
  const conductorElegido = conductorId || (conductores[0] ? String(conductores[0].id) : '');
  const conductorDelActivo = activo ? usuarios.find((u) => u.id === activo.conductorId) : null;

  async function ejecutar(accion) {
    setCargando(true);
    setError('');
    try {
      await accion();
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }

  function elegirPunto(p) {
    if (!origen || (origen && destino)) {
      setOrigen(p);
      setDestino(null);
      setEstimacion(null);
    } else {
      setDestino(p);
      setEstimacion(null);
    }
  }

  const estimar = () =>
    ejecutar(async () => {
      setEstimacion(
        await api.estimar({
          origenLat: origen.lat,
          origenLng: origen.lng,
          destinoLat: destino.lat,
          destinoLng: destino.lng,
        }),
      );
    });

  const solicitar = () =>
    ejecutar(async () => {
      await api.solicitar({
        pasajeroId: yo.id,
        conductorId: Number(conductorElegido),
        origenLat: origen.lat,
        origenLng: origen.lng,
        origenDireccion: `${origen.lat.toFixed(4)}, ${origen.lng.toFixed(4)}`,
        destinoLat: destino.lat,
        destinoLng: destino.lng,
        destinoDireccion: `${destino.lat.toFixed(4)}, ${destino.lng.toFixed(4)}`,
      });
      setOrigen(null);
      setDestino(null);
      setEstimacion(null);
      setFraude(null);
      await refrescar.viajes();
    });

  const cancelar = () =>
    ejecutar(async () => {
      await api.cancelar(activo.id);
      await refrescar.viajes();
    });

  const pagar = () =>
    ejecutar(async () => {
      await api.procesarPago({
        viajeId: porPagar.id,
        usuarioId: yo.id,
        monto: porPagar.costoFinal ?? porPagar.costoEstimado,
        moneda: porPagar.moneda || 'USD',
        metodoPago: 'TARJETA',
      });
      setFraude(null);
      await refrescar.pagos();
    });

  const evaluarFraude = () =>
    ejecutar(async () => {
      setFraude(await api.evaluarFraude(pagoUltimo.id));
    });

  return (
    <section className="panel pasajero">
      <header className="panel-cabecera">
        <span className="panel-icono" aria-hidden="true">🧍</span>
        <div>
          <h1>Pasajero</h1>
          <p>Pide un viaje y síguelo en tiempo real</p>
        </div>
      </header>

      <div className="panel-cuerpo">
        <IdentidadUsuario
          rol="PASAJERO"
          etiqueta="pasajero"
          usuarios={usuarios}
          seleccionadoId={pasajeroId}
          onSeleccionar={setPasajeroId}
          onCambio={refrescar.usuarios}
        />

        {error && <div className="aviso error">{error}</div>}

        {yo && activo && (
          <section className="bloque">
            <h2>Tu viaje #{activo.id}</h2>
            <EstadoViaje estado={activo.estado} />
            <MapaViaje viaje={activo} />
            <p className="dato">
              <b>Conductor:</b> {conductorDelActivo ? `#${conductorDelActivo.id} ${conductorDelActivo.nombre}` : `#${activo.conductorId}`}
              {' · '}
              {activo.distanciaKm.toFixed(1)} km · {Math.round(activo.duracionMin)} min ·{' '}
              <b>{dinero(activo.costoEstimado, activo.moneda)}</b>
            </p>
            <p className="ayuda">
              {activo.estado === 'ASIGNADO'
                ? 'Esperando a que el conductor acepte y comience el viaje…'
                : 'Tu conductor va en camino. El estado se actualiza solo.'}
            </p>
            {activo.estado === 'ASIGNADO' ? (
              <button type="button" className="peligro" onClick={cancelar} disabled={cargando}>
                Cancelar viaje
              </button>
            ) : (
              <p className="ayuda">El conductor ya inició el viaje: ya no se puede cancelar.</p>
            )}
          </section>
        )}

        {yo && porPagar && (
          <section className="bloque destacado">
            <h2>Viaje #{porPagar.id} finalizado</h2>
            <p className="dato">
              Total a pagar: <b>{dinero(porPagar.costoFinal ?? porPagar.costoEstimado, porPagar.moneda)}</b>
            </p>
            <button type="button" onClick={pagar} disabled={cargando}>
              💳 Pagar con tarjeta
            </button>
          </section>
        )}

        {yo && pagoUltimo && ultimo.estado === 'FINALIZADO' && (
          <section className="bloque destacado ok">
            <h2>Pago {pagoUltimo.estado.toLowerCase()}</h2>
            <p className="dato">
              {dinero(pagoUltimo.monto, pagoUltimo.moneda)} · ref {pagoUltimo.referencia}
            </p>
            <button type="button" className="secundario" onClick={evaluarFraude} disabled={cargando}>
              🛡️ Evaluar fraude (IA)
            </button>
            {fraude && (
              <div className={`resultado riesgo-${String(fraude.riesgo).toLowerCase()}`}>
                Riesgo <b>{fraude.riesgo}</b> · score {fraude.score}
                <p className="ayuda">{fraude.justificacion}</p>
              </div>
            )}
          </section>
        )}

        {yo && ultimo && ultimo.estado === 'CANCELADO' && !activo && (
          <section className="bloque">
            <EstadoViaje estado="CANCELADO" />
            <p className="ayuda">El viaje #{ultimo.id} fue cancelado. Puedes pedir otro abajo.</p>
          </section>
        )}

        {yo && !activo && (
          <section className="bloque">
            <h2>Pedir un viaje</h2>
            <p className="ayuda">Toca el mapa: primero el origen (verde) y luego el destino (rojo).</p>
            <MapPicker origen={origen} destino={destino} onPick={elegirPunto} />

            <div className="fila">
              <button type="button" className="secundario" onClick={estimar} disabled={cargando || !origen || !destino}>
                Estimar tarifa
              </button>
            </div>
            {estimacion && (
              <div className="resultado">
                {estimacion.distanciaKm} km · {estimacion.duracionMin} min · <b>${estimacion.costoUSD} USD</b> ·{' '}
                {estimacion.costoCOP} COP
              </div>
            )}

            {conductores.length === 0 ? (
              <p className="aviso">Aún no hay conductores. Regístrate uno en el panel de la derecha.</p>
            ) : (
              <label className="campo">
                Conductor
                <select value={conductorElegido} onChange={(e) => setConductorId(e.target.value)}>
                  {conductores.map((c) => (
                    <option key={c.id} value={c.id}>
                      #{c.id} {c.nombre}
                    </option>
                  ))}
                </select>
              </label>
            )}

            <button
              type="button"
              onClick={solicitar}
              disabled={cargando || !origen || !destino || conductores.length === 0}
            >
              🚖 Solicitar viaje
            </button>
          </section>
        )}
      </div>
    </section>
  );
}

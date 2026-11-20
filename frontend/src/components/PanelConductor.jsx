import { useEffect, useState } from 'react';
import api from '../api.js';
import EstadoViaje from './EstadoViaje.jsx';
import IdentidadUsuario from './IdentidadUsuario.jsx';
import MapaViaje from './MapaViaje.jsx';

const dinero = (n, moneda = 'USD') => `${Number(n).toFixed(2)} ${moneda}`;

export default function PanelConductor({ usuarios, viajes, refrescar }) {
  const [conductorId, setConductorId] = useState(() => Number(localStorage.getItem('uber.conductorId')) || null);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (conductorId) localStorage.setItem('uber.conductorId', String(conductorId));
  }, [conductorId]);

  const yo = usuarios.find((u) => u.id === conductorId && u.rol === 'CONDUCTOR') || null;
  const mios = viajes.filter((v) => v.conductorId === yo?.id).sort((a, b) => b.id - a.id);
  const entrantes = mios.filter((v) => v.estado === 'ASIGNADO');
  const enCurso = mios.filter((v) => v.estado === 'EN_CURSO');
  const historial = mios.filter((v) => v.estado === 'FINALIZADO' || v.estado === 'CANCELADO').slice(0, 5);
  const habilitado = yo?.estado === 'ACTIVO' && yo?.estadoDocumento === 'VERIFICADO';
  const nombrePasajero = (id) => {
    const p = usuarios.find((u) => u.id === id);
    return p ? `#${p.id} ${p.nombre}` : `#${id}`;
  };

  async function accion(fn, id) {
    setCargando(true);
    setError('');
    try {
      await fn(id);
      await refrescar.viajes();
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }

  return (
    <section className="panel conductor">
      <header className="panel-cabecera">
        <span className="panel-icono" aria-hidden="true">🚗</span>
        <div>
          <h1>Conductor</h1>
          <p>Recibe solicitudes, acepta y completa los viajes</p>
        </div>
      </header>

      <div className="panel-cuerpo">
        <IdentidadUsuario
          rol="CONDUCTOR"
          etiqueta="conductor"
          usuarios={usuarios}
          seleccionadoId={conductorId}
          onSeleccionar={setConductorId}
          onCambio={refrescar.usuarios}
        />

        {error && <div className="aviso error">{error}</div>}

        {yo && !habilitado && (
          <div className="aviso">
            Para recibir viajes tu cuenta debe estar <b>ACTIVA</b> (ahora: {yo.estado}) y tu documento{' '}
            <b>VERIFICADO</b> (ahora: {yo.estadoDocumento}). Se cambia en <b>Gestión de usuarios</b>.
          </div>
        )}

        {yo && habilitado && entrantes.length === 0 && enCurso.length === 0 && (
          <section className="bloque vacio">
            <div className="radar" aria-hidden="true" />
            <p>Esperando solicitudes…</p>
            <p className="ayuda">Cuando un pasajero te pida un viaje aparecerá aquí al instante.</p>
          </section>
        )}

        {entrantes.map((v) => (
          <section key={v.id} className="bloque entrante">
            <h2>🔔 Nueva solicitud · viaje #{v.id}</h2>
            <p className="dato">
              <b>Pasajero:</b> {nombrePasajero(v.pasajeroId)}
            </p>
            <MapaViaje viaje={v} altura={180} />
            <p className="dato">
              {v.distanciaKm.toFixed(1)} km · {Math.round(v.duracionMin)} min · <b>{dinero(v.costoEstimado, v.moneda)}</b>
            </p>
            <div className="fila">
              <button type="button" onClick={() => accion(api.iniciar, v.id)} disabled={cargando}>
                ✅ Aceptar y comenzar
              </button>
              <button type="button" className="peligro" onClick={() => accion(api.cancelar, v.id)} disabled={cargando}>
                Rechazar
              </button>
            </div>
          </section>
        ))}

        {enCurso.map((v) => (
          <section key={v.id} className="bloque destacado">
            <h2>Viaje #{v.id} en curso</h2>
            <EstadoViaje estado={v.estado} />
            <MapaViaje viaje={v} altura={180} />
            <p className="dato">
              <b>Pasajero:</b> {nombrePasajero(v.pasajeroId)} · <b>{dinero(v.costoEstimado, v.moneda)}</b>
            </p>
            <button type="button" onClick={() => accion(api.finalizar, v.id)} disabled={cargando}>
              🏁 Finalizar viaje
            </button>
          </section>
        ))}

        {yo && historial.length > 0 && (
          <section className="bloque">
            <h2>Últimos viajes</h2>
            <ul className="historial">
              {historial.map((v) => (
                <li key={v.id}>
                  <span>
                    #{v.id} · {nombrePasajero(v.pasajeroId)}
                  </span>
                  <span className={`insignia ${v.estado === 'FINALIZADO' ? 'ok' : 'cancelado'}`}>
                    {v.estado === 'FINALIZADO' ? `Finalizado · ${dinero(v.costoFinal ?? v.costoEstimado, v.moneda)}` : 'Cancelado'}
                  </span>
                </li>
              ))}
            </ul>
          </section>
        )}
      </div>
    </section>
  );
}

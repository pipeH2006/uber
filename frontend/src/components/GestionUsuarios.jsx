import { useState } from 'react';
import api from '../api.js';

const CLASE_ESTADO = {
  ACTIVO: 'ok',
  PENDIENTE: 'aviso',
  INACTIVO: 'neutro',
  SUSPENDIDO: 'cancelado',
};

const CLASE_DOCUMENTO = {
  VERIFICADO: 'ok',
  PENDIENTE: 'aviso',
  RECHAZADO: 'cancelado',
  NO_APLICA: 'neutro',
};

/**
 * Gestión de usuarios: ver todas las cuentas y cambiar su estado y el estado del documento del conductor.
 * Los botones solo aparecen cuando la transición es válida (el backend aplica las mismas reglas).
 */
export default function GestionUsuarios({ usuarios, refrescar }) {
  const [filtroRol, setFiltroRol] = useState('TODOS');
  const [busqueda, setBusqueda] = useState('');
  const [ocupado, setOcupado] = useState(null);
  const [error, setError] = useState('');

  const texto = busqueda.trim().toLowerCase();
  const visibles = usuarios
    .filter((u) => filtroRol === 'TODOS' || u.rol === filtroRol)
    .filter((u) => !texto || `${u.nombre} ${u.email} ${u.id}`.toLowerCase().includes(texto));

  const cuenta = (estado) => usuarios.filter((u) => u.estado === estado).length;
  const conductoresSinVerificar = usuarios.filter(
    (u) => u.rol === 'CONDUCTOR' && u.estadoDocumento !== 'VERIFICADO',
  ).length;

  async function ejecutar(usuario, accion) {
    setOcupado(usuario.id);
    setError('');
    try {
      await accion(usuario.id);
      await refrescar();
    } catch (e) {
      setError(e.message);
    } finally {
      setOcupado(null);
    }
  }

  function acciones(u) {
    const lista = [];
    if (['PENDIENTE', 'INACTIVO', 'SUSPENDIDO'].includes(u.estado)) {
      lista.push({ clave: 'activar', texto: u.estado === 'SUSPENDIDO' ? 'Reactivar' : 'Activar', fn: api.activar });
    }
    if (u.estado === 'ACTIVO') {
      lista.push({ clave: 'desactivar', texto: 'Desactivar', fn: api.desactivar });
    }
    if (u.estado !== 'SUSPENDIDO') {
      lista.push({ clave: 'suspender', texto: 'Suspender', fn: api.suspender, peligro: true });
    }
    if (u.rol === 'CONDUCTOR') {
      if (u.estadoDocumento !== 'VERIFICADO' && u.estado !== 'SUSPENDIDO') {
        lista.push({ clave: 'verificar', texto: 'Verificar documento', fn: api.verificarDocumento });
      }
      if (u.estadoDocumento !== 'RECHAZADO') {
        lista.push({ clave: 'rechazar', texto: 'Rechazar documento', fn: api.rechazarDocumento, peligro: true });
      }
    }
    return lista;
  }

  return (
    <main className="gestion">
      <header className="gestion-cabecera">
        <h1>Gestión de usuarios</h1>
        <p>
          Una cuenta solo puede viajar o conducir si está <b>ACTIVA</b>; además, un conductor necesita el documento{' '}
          <b>VERIFICADO</b>. Cambia aquí esos estados.
        </p>
      </header>

      <div className="resumen">
        <span className="resumen-dato">
          <b>{usuarios.length}</b> usuarios
        </span>
        <span className="resumen-dato">
          <b>{cuenta('ACTIVO')}</b> activos
        </span>
        <span className="resumen-dato">
          <b>{cuenta('PENDIENTE')}</b> pendientes
        </span>
        <span className="resumen-dato">
          <b>{cuenta('SUSPENDIDO')}</b> suspendidos
        </span>
        <span className="resumen-dato">
          <b>{conductoresSinVerificar}</b> conductores sin verificar
        </span>
      </div>

      <div className="filtros">
        <select value={filtroRol} onChange={(e) => setFiltroRol(e.target.value)} aria-label="Filtrar por rol">
          <option value="TODOS">Todos los roles</option>
          <option value="PASAJERO">Pasajeros</option>
          <option value="CONDUCTOR">Conductores</option>
        </select>
        <input
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          placeholder="Buscar por nombre, email o id"
          aria-label="Buscar usuario"
        />
      </div>

      {error && <div className="aviso error">{error}</div>}

      <div className="tabla-envoltorio">
        <table className="tabla">
          <thead>
            <tr>
              <th>Id</th>
              <th>Usuario</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Documento</th>
              <th>Telegram</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {visibles.length === 0 && (
              <tr>
                <td colSpan={7} className="vacio-tabla">
                  No hay usuarios que coincidan.
                </td>
              </tr>
            )}
            {visibles.map((u) => (
              <tr key={u.id}>
                <td>#{u.id}</td>
                <td>
                  <div className="usuario-nombre">{u.nombre}</div>
                  <div className="ayuda">{u.email}</div>
                </td>
                <td>{u.rol === 'CONDUCTOR' ? 'Conductor' : 'Pasajero'}</td>
                <td>
                  <span className={`insignia ${CLASE_ESTADO[u.estado] || 'neutro'}`}>{u.estado}</span>
                </td>
                <td>
                  <span className={`insignia ${CLASE_DOCUMENTO[u.estadoDocumento] || 'neutro'}`}>
                    {u.estadoDocumento === 'NO_APLICA' ? 'No aplica' : u.estadoDocumento}
                  </span>
                </td>
                <td>{u.telegramChatId ? 'Vinculado' : 'Sin vincular'}</td>
                <td>
                  <div className="acciones">
                    {acciones(u).map((a) => (
                      <button
                        key={a.clave}
                        type="button"
                        className={`chico ${a.peligro ? 'peligro' : 'secundario'}`}
                        disabled={ocupado === u.id}
                        onClick={() => ejecutar(u, a.fn)}
                      >
                        {a.texto}
                      </button>
                    ))}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </main>
  );
}

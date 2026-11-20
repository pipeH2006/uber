import { useState } from 'react';
import api, { BOT_USERNAME } from '../api.js';

export default function IdentidadUsuario({ rol, etiqueta, usuarios, seleccionadoId, onSeleccionar, onCambio }) {
  const [registrando, setRegistrando] = useState(false);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');

  const candidatos = usuarios.filter((u) => u.rol === rol);
  const yo = candidatos.find((u) => u.id === seleccionadoId) || null;

  async function registrar(e) {
    e.preventDefault();
    const form = new FormData(e.target);
    setCargando(true);
    setError('');
    try {
      const nuevo = await api.registrarUsuario({
        nombre: form.get('nombre'),
        email: form.get('email'),
        telefono: form.get('telefono') || null,
        contrasena: form.get('contrasena'),
        rol,
      });
      await onCambio();
      onSeleccionar(nuevo.id);
      setRegistrando(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }

  function vincularTelegram() {
    window.open(`https://t.me/${BOT_USERNAME}?start=${yo.id}`, '_blank', 'noopener');
  }

  const mostrarFormulario = registrando || candidatos.length === 0;

  return (
    <section className="bloque identidad">
      <h2>Tu cuenta de {etiqueta}</h2>

      {!mostrarFormulario && (
        <div className="fila">
          <select value={yo ? yo.id : ''} onChange={(e) => onSeleccionar(Number(e.target.value) || null)}>
            <option value="">— Elige tu usuario —</option>
            {candidatos.map((u) => (
              <option key={u.id} value={u.id}>
                #{u.id} {u.nombre}
              </option>
            ))}
          </select>
          <button type="button" className="secundario" onClick={() => setRegistrando(true)}>
            + Nuevo
          </button>
        </div>
      )}

      {mostrarFormulario && (
        <form onSubmit={registrar} className="formulario">
          <input name="nombre" placeholder="Nombre" required />
          <input name="email" type="email" placeholder="Email" required />
          <input name="telefono" type="tel" placeholder="Teléfono (el mismo de tu Telegram)" />
          <input name="contrasena" type="password" placeholder="Contraseña (mín. 6)" minLength={6} required />
          <div className="fila">
            <button type="submit" disabled={cargando}>
              {cargando ? 'Registrando…' : `Registrarme como ${etiqueta}`}
            </button>
            {candidatos.length > 0 && (
              <button type="button" className="secundario" onClick={() => setRegistrando(false)}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      )}

      {error && <div className="aviso error">{error}</div>}

      {yo && (
        <div className="telegram">
          {yo.telegramChatId ? (
            <span className="insignia ok">✅ Telegram vinculado: recibirás avisos de tus viajes</span>
          ) : (
            <>
              <button type="button" className="telegram-btn" onClick={vincularTelegram}>
                ✈️ Vincular Telegram
              </button>
              <p className="ayuda">
                Se abre el bot: pulsa <b>Iniciar</b> y vuelve aquí. Esta pantalla se actualiza sola.
              </p>
              <p className="ayuda">
                ¿Prefieres usar tu teléfono? Abre <a href={`https://t.me/${BOT_USERNAME}`} target="_blank" rel="noreferrer">el bot</a>,
                escribe /start y pulsa «Compartir mi número»: lo reconocemos por el teléfono con el que te registraste.
              </p>
            </>
          )}
        </div>
      )}
    </section>
  );
}

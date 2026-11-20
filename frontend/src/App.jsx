import { useState } from 'react';
import api from './api.js';
import GestionUsuarios from './components/GestionUsuarios.jsx';
import PanelConductor from './components/PanelConductor.jsx';
import PanelPasajero from './components/PanelPasajero.jsx';
import usePolling from './hooks/usePolling.js';

export default function App() {
  const [vista, setVista] = useState('viajes');
  const usuarios = usePolling(api.listarUsuarios, 4000, []);
  const viajes = usePolling(api.listarViajes, 3000, []);
  const pagos = usePolling(api.listarPagos, 5000, []);

  const sinConexion = usuarios.error || viajes.error || pagos.error;

  return (
    <div className="app">
      <div className="barra">
        <strong>Uber</strong>
        <nav className="pestanas" aria-label="Secciones">
          <button
            type="button"
            className={vista === 'viajes' ? 'pestana activa' : 'pestana'}
            onClick={() => setVista('viajes')}
          >
            Viajes
          </button>
          <button
            type="button"
            className={vista === 'usuarios' ? 'pestana activa' : 'pestana'}
            onClick={() => setVista('usuarios')}
          >
            Gestión de usuarios
          </button>
        </nav>
        {sinConexion && <span className="barra-estado">Conectando con los servicios… puede tardar si estaban dormidos</span>}
      </div>

      {vista === 'viajes' ? (
        <main className="dividido">
          <PanelPasajero
            usuarios={usuarios.data}
            viajes={viajes.data}
            pagos={pagos.data}
            refrescar={{ usuarios: usuarios.refrescar, viajes: viajes.refrescar, pagos: pagos.refrescar }}
          />
          <PanelConductor
            usuarios={usuarios.data}
            viajes={viajes.data}
            refrescar={{ usuarios: usuarios.refrescar, viajes: viajes.refrescar }}
          />
        </main>
      ) : (
        <GestionUsuarios usuarios={usuarios.data} refrescar={usuarios.refrescar} />
      )}
    </div>
  );
}

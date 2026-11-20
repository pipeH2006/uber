import api from './api.js';
import PanelConductor from './components/PanelConductor.jsx';
import PanelPasajero from './components/PanelPasajero.jsx';
import usePolling from './hooks/usePolling.js';

/**
 * Pantalla dividida: pasajero a la izquierda y conductor a la derecha. Ambos paneles comparten
 * los datos que se consultan cada pocos segundos, así que una acción de un lado se ve en el otro.
 */
export default function App() {
  const usuarios = usePolling(api.listarUsuarios, 4000, []);
  const viajes = usePolling(api.listarViajes, 3000, []);
  const pagos = usePolling(api.listarPagos, 5000, []);

  const sinConexion = usuarios.error || viajes.error || pagos.error;

  return (
    <div className="app">
      <div className="barra">
        <strong>Uber</strong>
        <span>Microservicios · Spring Boot · OSRM · Groq · Telegram</span>
        {sinConexion && <span className="barra-estado">Conectando con los servicios… puede tardar si estaban dormidos</span>}
      </div>

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
    </div>
  );
}

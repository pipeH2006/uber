import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, CircleMarker, Polyline } from 'react-leaflet';
import api from '../api.js';

export default function MapaViaje({ viaje, altura = 220 }) {
  const [ruta, setRuta] = useState([]);

  useEffect(() => {
    let activo = true;
    setRuta([]);
    api
      .seguimiento(viaje.id)
      .then((s) => {
        if (activo) setRuta((s.ruta || []).map((p) => [p.lat, p.lng]));
      })
      .catch(() => {
      });
    return () => {
      activo = false;
    };
  }, [viaje.id]);

  const origen = [viaje.origenLat, viaje.origenLng];
  const destino = [viaje.destinoLat, viaje.destinoLng];
  const hayRuta = ruta.length > 1;

  return (
    <MapContainer
      key={viaje.id}
      bounds={[origen, destino]}
      boundsOptions={{ padding: [40, 40] }}
      scrollWheelZoom={false}
      style={{ height: `${altura}px`, width: '100%', borderRadius: '12px' }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {hayRuta ? (
        <Polyline positions={ruta} pathOptions={{ color: '#2563eb', weight: 5, opacity: 0.85 }} />
      ) : (
        <Polyline positions={[origen, destino]} pathOptions={{ color: '#64748b', dashArray: '6 8', weight: 3 }} />
      )}
      <CircleMarker center={origen} radius={9} pathOptions={{ color: '#16a34a', fillColor: '#16a34a', fillOpacity: 0.9 }} />
      <CircleMarker center={destino} radius={9} pathOptions={{ color: '#dc2626', fillColor: '#dc2626', fillOpacity: 0.9 }} />
    </MapContainer>
  );
}

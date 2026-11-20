import { MapContainer, TileLayer, CircleMarker, Polyline } from 'react-leaflet';

/** Mapa de solo lectura con origen (verde) y destino (rojo) de un viaje. */
export default function MapaViaje({ viaje, altura = 220 }) {
  const origen = [viaje.origenLat, viaje.origenLng];
  const destino = [viaje.destinoLat, viaje.destinoLng];
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
      <Polyline positions={[origen, destino]} pathOptions={{ color: '#64748b', dashArray: '6 8', weight: 3 }} />
      <CircleMarker center={origen} radius={9} pathOptions={{ color: '#16a34a', fillColor: '#16a34a', fillOpacity: 0.9 }} />
      <CircleMarker center={destino} radius={9} pathOptions={{ color: '#dc2626', fillColor: '#dc2626', fillOpacity: 0.9 }} />
    </MapContainer>
  );
}

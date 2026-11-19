import { MapContainer, TileLayer, CircleMarker, useMapEvents } from 'react-leaflet';

function ClickCatcher({ onPick }) {
  useMapEvents({
    click(e) {
      onPick({ lat: e.latlng.lat, lng: e.latlng.lng });
    },
  });
  return null;
}

export default function MapPicker({ origen, destino, onPick }) {
  const center = origen || { lat: 4.711, lng: -74.0721 };

  return (
    <MapContainer
      center={[center.lat, center.lng]}
      zoom={12}
      scrollWheelZoom
      style={{ height: '420px', width: '100%', borderRadius: '12px' }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <ClickCatcher onPick={onPick} />
      {origen && (
        <CircleMarker
          center={[origen.lat, origen.lng]}
          radius={9}
          pathOptions={{ color: '#16a34a', fillColor: '#16a34a', fillOpacity: 0.9 }}
        />
      )}
      {destino && (
        <CircleMarker
          center={[destino.lat, destino.lng]}
          radius={9}
          pathOptions={{ color: '#dc2626', fillColor: '#dc2626', fillOpacity: 0.9 }}
        />
      )}
    </MapContainer>
  );
}

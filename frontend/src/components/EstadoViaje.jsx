const PASOS = [
  { estado: 'ASIGNADO', etiqueta: 'Conductor asignado' },
  { estado: 'EN_CURSO', etiqueta: 'En camino' },
  { estado: 'FINALIZADO', etiqueta: 'Finalizado' },
];

export default function EstadoViaje({ estado }) {
  if (estado === 'CANCELADO') {
    return <div className="insignia cancelado">Viaje cancelado</div>;
  }
  const actual = PASOS.findIndex((p) => p.estado === estado);
  return (
    <ol className="pasos">
      {PASOS.map((p, i) => (
        <li key={p.estado} className={i < actual ? 'hecho' : i === actual ? 'actual' : ''}>
          <span className="punto">{i < actual ? '✓' : i + 1}</span>
          {p.etiqueta}
        </li>
      ))}
    </ol>
  );
}

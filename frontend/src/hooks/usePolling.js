import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * Consulta `fn` al montar y cada `intervaloMs`. Devuelve los datos, el último error y una función
 * `refrescar` para volver a consultar de inmediato (por ejemplo, tras una acción del usuario).
 */
export default function usePolling(fn, intervaloMs, inicial) {
  const [data, setData] = useState(inicial);
  const [error, setError] = useState(null);
  const fnRef = useRef(fn);
  fnRef.current = fn;
  const montado = useRef(true);

  const refrescar = useCallback(async () => {
    try {
      const resultado = await fnRef.current();
      if (montado.current) {
        setData(resultado);
        setError(null);
      }
    } catch (e) {
      if (montado.current) setError(e.message);
    }
  }, []);

  useEffect(() => {
    montado.current = true;
    refrescar();
    const id = setInterval(refrescar, intervaloMs);
    return () => {
      montado.current = false;
      clearInterval(id);
    };
  }, [refrescar, intervaloMs]);

  return { data, error, refrescar };
}

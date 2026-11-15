"""Carga y extrae las 12 variables: lecturas, diferencias y medias moviles (5)."""
from __future__ import annotations
import csv
from collections import defaultdict, deque
from pathlib import Path
import numpy as np

CANALES = ("mq2", "mq6", "mq135", "llama")
FEATURES = [*CANALES, *("delta_" + x for x in CANALES), *("media5_" + x for x in CANALES)]
CLASES = ("seguro", "precaucion", "peligro")
ETIQUETA_ID = {e: i for i, e in enumerate(CLASES)}


def leer_dataset(rutas: list[Path]):
    """Combina sin duplicar (sesion,ms); exige que la etiqueta provenga del escenario real."""
    filas, claves = [], {}
    for ruta in rutas:
        with Path(ruta).open(encoding="utf-8-sig", newline="") as f:
            for r in csv.DictReader(f):
                if not r.get("sesion") or r.get("etiqueta") not in ETIQUETA_ID:
                    raise ValueError(f"Fila sin sesion/etiqueta valida en {ruta}")
                rec = dict(r)
                rec["ms"] = int(r["ms"])
                if rec["ms"] < 0:
                    raise ValueError(f"Tiempo negativo en {ruta}")
                for x in CANALES:
                    rec[x] = int(r[x])
                    if not (0 <= rec[x] <= 4095):
                        raise ValueError(f"Lectura fuera de rango en {ruta}: {rec}")
                clave = (rec["sesion"], rec["ms"])
                if clave in claves:
                    anterior = claves[clave]
                    if any(rec.get(c) != anterior.get(c) for c in (*CANALES, "etiqueta", "escenario")):
                        raise ValueError(f"Datos contradictorios para {clave} en {ruta}; usa una sesion distinta por captura/arranque")
                    continue
                claves[clave] = rec
                filas.append(rec)
    if not filas:
        raise ValueError("Dataset vacio")
    filas.sort(key=lambda r: (r["sesion"], r["ms"]))
    return filas


def construir_features(filas):
    """Las medias/deltas se calculan dentro de cada sesion; no hay fuga entre sesiones."""
    historiales = defaultdict(lambda: deque(maxlen=5))
    previos = {}
    datos = []
    for r in filas:
        actual = np.array([r[c] for c in CANALES], dtype=np.float64)
        antes = previos.get(r["sesion"])
        delta = (actual - antes) if antes is not None else np.zeros(len(CANALES))
        historiales[r["sesion"]].append(actual)
        media5 = np.mean(np.stack(historiales[r["sesion"]]), axis=0)
        datos.append(np.concatenate((actual, delta, media5)))
        previos[r["sesion"]] = actual
    X = np.asarray(datos, dtype=np.float32)
    y = np.asarray([ETIQUETA_ID[r["etiqueta"]] for r in filas], dtype=np.int64)
    grupos = np.asarray([r["sesion"] for r in filas], dtype=str)
    return X, y, grupos

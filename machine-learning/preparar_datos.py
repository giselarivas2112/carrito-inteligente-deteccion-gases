"""Importa lineas DATA del monitor serial sin inventar etiquetas.

Ejemplo:
 python preparar_datos.py --archivo registro.txt --sesion sesion_03 --etiqueta seguro --escenario aire_limpio --salida datos/nueva_sesion.csv
"""
from __future__ import annotations
import argparse
import csv
from pathlib import Path
from rutas import DATOS
from protocolo_serial import interpretar_linea

CAMPOS = ["sesion", "ms", "mq2", "mq6", "mq135", "llama", "etiqueta", "escenario", "origen", "riesgo_provisional", "llama_detectada_provisional", "fecha_pc"]
ETIQUETAS = {"seguro", "precaucion", "peligro"}


def extraer_data(archivo: Path, sesion: str, etiqueta: str, escenario: str) -> list[dict]:
    """Devuelve solo lineas validas DATA,ms,mq2,mq6,mq135,llama,riesgo,llama_detectada."""
    registros = []
    vistos = {}
    for linea in archivo.read_text(encoding="utf-8-sig", errors="replace").splitlines():
        if not linea.strip().startswith("DATA,"):
            continue
        lectura = interpretar_linea(linea, formato="data")
        if lectura is None:
            continue
        ms, mq2, mq6, mq135, llama, riesgo, deteccion = (
            lectura[k] for k in ("ms", "mq2", "mq6", "mq135", "llama",
                                "riesgo_provisional", "llama_detectada_provisional"))
        clave = (ms, mq2, mq6, mq135, llama, riesgo, deteccion)
        if ms in vistos:
            if vistos[ms] != clave:
                raise ValueError(f"Lecturas contradictorias en ms={ms}; separa los arranques en sesiones distintas")
            continue  # omite duplicados, p. ej. bloques repetidos al pegar el monitor
        if vistos and ms < max(vistos):
            raise ValueError('El reloj retrocede dentro del TXT; separa los arranques en sesiones distintas')
        vistos[ms] = clave
        registros.append({
            "sesion": sesion, "ms": ms, "mq2": mq2, "mq6": mq6, "mq135": mq135,
            "llama": llama, "etiqueta": etiqueta, "escenario": escenario,
            "origen": archivo.name, "riesgo_provisional": riesgo,
            "llama_detectada_provisional": deteccion, "fecha_pc": "",
        })
    registros.sort(key=lambda r: r["ms"])
    return registros


def escribir_csv(destino: Path, filas: list[dict], anexar: bool = False) -> None:
    destino.parent.mkdir(parents=True, exist_ok=True)
    if anexar and destino.exists():
        with destino.open(encoding="utf-8-sig", newline="") as f:
            actuales = list(csv.DictReader(f))
        claves = {(f["sesion"], int(f["ms"])): f for f in actuales}
        for fila in filas:
            clave = (fila["sesion"], int(fila["ms"]))
            if clave in claves and any(str(fila[c]) != str(claves[clave][c])
                                      for c in ("mq2", "mq6", "mq135", "llama", "etiqueta", "escenario")):
                raise ValueError(f'Datos contradictorios al anexar {clave}')
        filas = actuales + [f for f in filas if (f["sesion"], int(f["ms"])) not in claves]
    with destino.open("w", encoding="utf-8-sig", newline="") as f:
        w = csv.DictWriter(f, fieldnames=CAMPOS)
        w.writeheader()
        w.writerows(filas)


def main() -> None:
    p = argparse.ArgumentParser(description="Prepara un CSV desde las lineas DATA del ESP32")
    p.add_argument("--archivo", type=Path, required=True)
    p.add_argument("--sesion", required=True, help="ID unico de la sesion; por ejemplo sesion_03_seguro")
    p.add_argument("--etiqueta", choices=sorted(ETIQUETAS), required=True, help="Condicion REAL declarada por el experimentador, no el riesgo del ESP32")
    p.add_argument("--escenario", required=True, help="Describe el escenario observado; sin inventar condiciones peligrosas")
    p.add_argument("--salida", type=Path, default=DATOS / "sesiones.csv")
    p.add_argument("--anexar", action="store_true")
    args = p.parse_args()
    if args.salida.exists() and not args.anexar:
        p.error('La salida ya existe; usa otro archivo o --anexar para combinar sin duplicados.')
    registros = extraer_data(args.archivo, args.sesion, args.etiqueta, args.escenario)
    if not registros:
        raise SystemExit("No se encontraron lineas DATA validas.")
    escribir_csv(args.salida, registros, args.anexar)
    print(f"Se guardaron {len(registros)} lecturas de {args.sesion} en {args.salida}")
    print("Nota: la etiqueta es una anotacion experimental, NO una medicion certificada de seguridad.")

if __name__ == "__main__":
    main()

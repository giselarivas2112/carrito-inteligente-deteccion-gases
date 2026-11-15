"""Captura DATA o el CSV de cuatro canales de Firmware/Firmware.ino."""
from __future__ import annotations
import argparse
import csv
from datetime import datetime, timezone
from pathlib import Path
import time

from preparar_datos import CAMPOS, ETIQUETAS
from protocolo_serial import interpretar_linea


def capturar(ser, archivo, *, sesion, etiqueta, escenario, puerto,
             duracion=180, espera=120, formato="auto"):
    """Duracion desde la primera muestra; espera limita silencios y datos invalidos."""
    writer = csv.DictWriter(archivo, fieldnames=CAMPOS)
    writer.writeheader()
    inicio = None
    apertura = ultima_valida = time.monotonic()
    anterior_ms = None
    reloj_sesion = None
    contador = 0
    while True:
        ahora = time.monotonic()
        if inicio is not None and duracion > 0 and ahora - inicio >= duracion:
            return contador
        if ahora - ultima_valida >= espera:
            raise ValueError(f'Sin lecturas validas durante {espera:g} s; revisa puerto, baudios y formato.')
        raw = ser.readline().decode('utf-8', errors='replace')
        ms_pc = int((time.monotonic() - apertura) * 1000)
        rec = interpretar_linea(raw, formato, ms_pc)
        if rec is None:
            continue
        reloj = rec.pop('reloj')
        if reloj_sesion is not None and reloj != reloj_sesion:
            raise ValueError('Cambio de formato durante la captura; inicia otra sesion.')
        reloj_sesion = reloj
        if anterior_ms is not None:
            if reloj == 'esp32' and rec['ms'] < anterior_ms:
                raise ValueError('El reloj del ESP32 retrocedio: posible reinicio. Inicia otra sesion y archivo.')
            if rec['ms'] <= anterior_ms:
                if reloj == 'esp32':
                    continue
                rec['ms'] = anterior_ms + 1
        anterior_ms = rec['ms']
        ultima_valida = time.monotonic()
        if inicio is None:
            inicio = ultima_valida
            print(f'Primera muestra recibida; tiempo de origen: {reloj}.')
        writer.writerow({**rec, 'sesion': sesion, 'etiqueta': etiqueta,
                         'escenario': escenario, 'origen': f'serial:{puerto};reloj={reloj}',
                         'fecha_pc': datetime.now(timezone.utc).isoformat(timespec='milliseconds')})
        archivo.flush()
        contador += 1
        if contador % 25 == 0:
            print(f'{contador} filas guardadas.')


def main():
    p = argparse.ArgumentParser(description='Captura real por USB, compatible con el firmware del repo')
    p.add_argument('--puerto', required=True, help='Ej.: COM5, /dev/ttyUSB0')
    p.add_argument('--baudios', type=int, default=115200)
    p.add_argument('--sesion', required=True, help='ID unico por captura/arranque')
    p.add_argument('--etiqueta', required=True, choices=sorted(ETIQUETAS))
    p.add_argument('--escenario', required=True)
    p.add_argument('--duracion', type=float, default=180, help='Segundos desde la primera muestra; 0 = Ctrl+C')
    p.add_argument('--espera', type=float, default=120, help='Maximo de segundos sin muestras validas')
    p.add_argument('--formato', choices=('auto', 'data', 'firmware-repo'), default='auto')
    p.add_argument('--salida', type=Path, required=True, help='Archivo nuevo; no sobrescribe capturas')
    args = p.parse_args()
    if args.duracion < 0 or args.espera <= 0:
        p.error('--duracion debe ser >= 0 y --espera debe ser > 0')
    if args.salida.exists():
        p.error('El archivo de salida ya existe. Usa un nombre nuevo para conservar la captura anterior.')
    import serial
    args.salida.parent.mkdir(parents=True, exist_ok=True)
    print(f'Abriendo {args.puerto}. Cierra antes el Monitor Serial de Arduino IDE.')
    print('Los sensores deben estar estabilizados antes de capturar; el firmware del repo no espera ni calibra.')
    try:
        with serial.Serial(args.puerto, args.baudios, timeout=1) as ser:
            with args.salida.open('x', encoding='utf-8-sig', newline='') as archivo:
                contador = capturar(ser, archivo, sesion=args.sesion, etiqueta=args.etiqueta,
                                    escenario=args.escenario, puerto=args.puerto,
                                    duracion=args.duracion, espera=args.espera, formato=args.formato)
        print(f'Listo: {contador} filas en {args.salida}')
    except KeyboardInterrupt:
        print(f'Captura interrumpida. Las filas recibidas quedan en {args.salida}')
    except (serial.SerialException, ValueError, OSError) as exc:
        raise SystemExit(f'Captura detenida: {exc}')


if __name__ == '__main__':
    main()

"""Puntuacion EXPLORATORIA de anomalias. No diagnostica gas, fuego ni peligro.

Este script lee sesiones con el formato de datos del proyecto, calcula las mismas
12 variables que en entrenamiento y guarda las puntuaciones en un CSV.
"""
from __future__ import annotations
import argparse
import csv
from pathlib import Path

import joblib
from funciones_ml import FEATURES, construir_features, leer_dataset
from rutas import DATOS, RESULTADOS


def main():
    p=argparse.ArgumentParser(description='Detectar lecturas atipicas (solo demostracion)')
    p.add_argument('--modelo',type=Path,default=RESULTADOS / 'modelo_anomalias_DEMO.joblib')
    p.add_argument('--datos',type=Path,nargs='+',default=[DATOS / 'dataset_real_383_seguro.csv'])
    p.add_argument('--salida',type=Path,default=RESULTADOS / 'puntuaciones_anomalias_DEMO.csv')
    a=p.parse_args()
    if not a.modelo.is_file():
        raise SystemExit('Primero ejecuta analizar_aire_limpio.py para generar el modelo local.')
    paquete=joblib.load(a.modelo)  # Solo abre modelos locales de confianza.
    if paquete.get('features') != FEATURES:
        raise SystemExit('Las variables del modelo no coinciden con las del programa.')
    filas=leer_dataset(a.datos)
    X,_,_=construir_features(filas)
    atipico=paquete['modelo'].predict(X)
    puntuacion=paquete['modelo'].decision_function(X)
    a.salida.parent.mkdir(parents=True,exist_ok=True)
    with a.salida.open('w',encoding='utf-8-sig',newline='') as f:
        campos=['sesion','ms','mq2','mq6','mq135','llama','etiqueta_observada','atipico_demo','puntuacion_demo']
        w=csv.DictWriter(f,fieldnames=campos)
        w.writeheader()
        for r,pred,sc in zip(filas,atipico,puntuacion):
            w.writerow({'sesion':r['sesion'],'ms':r['ms'],'mq2':r['mq2'],
                        'mq6':r['mq6'],'mq135':r['mq135'],'llama':r['llama'],
                        'etiqueta_observada':r['etiqueta'],
                        'atipico_demo':int(pred==-1),'puntuacion_demo':round(float(sc),6)})
    print(f'Archivo creado: {a.salida} ({len(filas)} filas)')
    print('ATENCION: atipico_demo=1 no significa peligro, gas ni llama.')
    print('Para una evaluacion honesta usa reporte_aire_limpio.json; este CSV incluye tambien datos de entrenamiento.')

if __name__=='__main__':
    main()

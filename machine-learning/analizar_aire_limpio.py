"""Estadisticas reales de dos sesiones y detector ML de anomalias SOLO exploratorio.

NO permite afirmar que una anomalía equivale a gas, fuego, precaucion o peligro.
"""
from __future__ import annotations
import argparse
import json
from collections import defaultdict
from pathlib import Path

import joblib
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler

from funciones_ml import CANALES, construir_features, leer_dataset
from rutas import DATOS, RESULTADOS


def main():
    p=argparse.ArgumentParser()
    p.add_argument('--datos',type=Path,default=DATOS / 'dataset_real_383_seguro.csv')
    p.add_argument('--salida',type=Path,default=RESULTADOS)
    args=p.parse_args()
    filas=leer_dataset([args.datos])
    if any(r['etiqueta']!='seguro' for r in filas):
        raise SystemExit('Este ejemplo es EXCLUSIVAMENTE para sesiones de aire limpio; abortando.')
    X,y,grupos=construir_features(filas)
    idx_train,idx_prueba=[],[]
    for g in sorted(set(grupos)):
        idx=np.where(grupos==g)[0]
        corte=max(1,int(.70*len(idx)))
        idx_train.extend(idx[:corte])
        idx_prueba.extend(idx[corte:])
    idx_train=np.asarray(idx_train,dtype=int)
    idx_prueba=np.asarray(idx_prueba,dtype=int)
    if not len(idx_prueba):
        raise SystemExit('No hay muestras suficientes para la revision temporal; agrega lecturas por sesion.')
    # El algoritmo NO aprende peligro; solo patrones del aire limpio disponible.
    modelo=make_pipeline(StandardScaler(), IsolationForest(n_estimators=120,contamination=.03,random_state=42))
    modelo.fit(X[idx_train])
    score= modelo.decision_function(X[idx_prueba])
    pred= modelo.predict(X[idx_prueba])
    tasa=float(np.mean(pred==-1))
    estadisticas={}
    for canal in CANALES:
        arr=np.asarray([r[canal] for r in filas])
        estadisticas[canal]={"min":int(arr.min()),"max":int(arr.max()),"media":round(float(arr.mean()),2),"desviacion":round(float(arr.std(ddof=1)),2)}
    reporte={
        'objetivo':'Demostracion de deteccion de anomalias entrenada SOLO en aire limpio',
        'total_muestras':len(filas),
        'por_sesion':{g:int(np.sum(grupos==g)) for g in sorted(set(grupos))},
        'por_clase':{'seguro':len(filas),'precaucion':0,'peligro':0},
        'entrenamiento_aire_limpio':len(idx_train),
        'revision_aire_limpio':len(idx_prueba),
        'fraccion_marcada_atipica_en_revision':round(tasa,4),
        'metodo_revision':'ultimo 30% de cada sesion; comparte entornos de sesion, no extrapolar',
        'mediciones':estadisticas,
        'advertencia':'Atipico NO significa gas ni peligro; no hay clasificador de 3 clases ni sensibilidad validada.'
    }
    args.salida.mkdir(parents=True,exist_ok=True)
    (args.salida/'reporte_aire_limpio.json').write_text(json.dumps(reporte,indent=2,ensure_ascii=False),encoding='utf-8')
    joblib.dump({'modelo':modelo,'features':list(__import__('funciones_ml').FEATURES),'uso':'exploratorio; anomalías != peligro'},args.salida/'modelo_anomalias_DEMO.joblib')
    fig, axes = plt.subplots(4,1,figsize=(10,9),sharex=False)
    grupo_data=defaultdict(list)
    for r in filas:
        grupo_data[r['sesion']].append(r)
    for ax,canal in zip(axes,CANALES):
        for sesion,rs in sorted(grupo_data.items()):
            x=np.asarray([(r['ms']-rs[0]['ms'])/1000 for r in rs])
            ax.plot(x,[r[canal] for r in rs],label=sesion,linewidth=1.15)
        ax.set_ylabel(f'{canal} (ADC)')
        ax.grid(alpha=.25)
    axes[0].legend(fontsize=8)
    axes[-1].set_xlabel('Segundos desde el primer registro de cada sesion')
    fig.suptitle('ESP32 · Datos reales en aire limpio (2 sesiones)')
    fig.tight_layout()
    fig.savefig(args.salida/'grafica_sesiones_aire_limpio.png',dpi=170)
    plt.close(fig)
    print(json.dumps(reporte,ensure_ascii=False,indent=2))

if __name__=='__main__':
    main()

"""Entrenamiento REAL de 3 clases, solo si existen datos etiquetados de las 3 clases.

Separa por sesion (no por fila) para evitar fuga temporal. Los resultados de test son
independientes de validacion y nunca se usan para seleccionar el modelo.
"""
from __future__ import annotations
import argparse
import json
from collections import Counter, defaultdict
from pathlib import Path
from itertools import combinations

import joblib
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (ConfusionMatrixDisplay, accuracy_score,
                             classification_report, confusion_matrix,
                             f1_score, precision_score, recall_score)
from sklearn.model_selection import GroupShuffleSplit
from sklearn.neighbors import KNeighborsClassifier
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.tree import DecisionTreeClassifier

from funciones_ml import CLASES, FEATURES, construir_features, leer_dataset
from rutas import RESULTADOS


def obtener_particiones(y, grupos):
    """Busca particiones por sesiones que contengan las 3 clases en train/val/test."""
    g_unicos = set(grupos)
    if len(g_unicos) < 3:
        raise ValueError("Hacen falta al menos 3 sesiones independientes; idealmente 4 o mas.")
    y_conjunto = set(int(x) for x in y)
    if y_conjunto != set(range(3)):
        raise ValueError("Faltan clases: se requieren seguro, precaucion y peligro.")
    for clase in range(3):
        sesiones = set(grupos[y == clase])
        if len(sesiones) < 3:
            raise ValueError(f"La clase {CLASES[clase]} solo aparece en {len(sesiones)} sesiones; se requieren >=3 para evaluar por sesion.")

    n = len(g_unicos)
    coberturas = {frozenset(y[grupos == g]) for g in g_unicos}
    minimo_sesiones = next(k for k in range(1, 4)
                           if any(set().union(*c) == {0, 1, 2}
                                  for c in combinations(coberturas, k)))
    test_count = max(minimo_sesiones, round(0.15 * n))
    val_count = max(minimo_sesiones, round(0.15 * n))
    if test_count + val_count >= n:
        raise ValueError('Faltan sesiones para cubrir las tres clases en cada particion.')
    mejor = None
    for semilla in range(500):
        gss1 = GroupShuffleSplit(n_splits=1, test_size=test_count, random_state=semilla)
        base_idx, test_idx = next(gss1.split(y, y, groups=grupos))
        gss2 = GroupShuffleSplit(n_splits=1, test_size=val_count, random_state=semilla + 1000)
        train_rel, val_rel = next(gss2.split(y[base_idx], y[base_idx], groups=grupos[base_idx]))
        train_idx, val_idx = base_idx[train_rel], base_idx[val_rel]
        if any(set(y[idx]) != {0, 1, 2} for idx in (train_idx, val_idx, test_idx)):
            continue
        fracs = [len(train_idx)/len(y),len(val_idx)/len(y),len(test_idx)/len(y)]
        error = sum(abs(a-b) for a,b in zip(fracs,(.70,.15,.15)))
        if mejor is None or error < mejor[0]:
            mejor = (error, train_idx, val_idx, test_idx)
    if mejor is None:
        raise ValueError("No existe division por sesion con las 3 clases en train/validacion/test. Recolecta mas sesiones representativas por clase.")
    return mejor[1:]


def score_modelo(m, X, y):
    pred = m.predict(X)
    return {
        "recall_peligro": float(recall_score(y, pred, labels=[2], average="macro", zero_division=0)),
        "f1_macro": float(f1_score(y, pred, labels=[0,1,2], average="macro",zero_division=0)),
        "accuracy": float(accuracy_score(y,pred)),
        "precision_macro": float(precision_score(y,pred,labels=[0,1,2],average="macro",zero_division=0)),
    }


def main():
    p=argparse.ArgumentParser(description="Entrena y compara 3 clasificadores con sesiones independientes")
    p.add_argument("--datos",type=Path,nargs="+",required=True,help="Uno o varios CSV con sesion y etiqueta verificadas")
    p.add_argument("--salida",type=Path,default=RESULTADOS)
    p.add_argument("--minimo-clase",type=int,default=300,help="Por defecto, minimo del proyecto: 300 muestras reales por clase")
    args=p.parse_args()
    if args.minimo_clase < 1:
        p.error('--minimo-clase debe ser positivo')
    filas=leer_dataset(args.datos)
    X,y,grupos=construir_features(filas)
    conteos=Counter(int(v) for v in y)
    for i,nombre in enumerate(CLASES):
        print(f"{nombre}: {conteos.get(i,0)} lecturas")
    faltantes=[f"{CLASES[i]} ({conteos.get(i,0)}/{args.minimo_clase})" for i in range(3) if conteos.get(i,0)<args.minimo_clase]
    if faltantes:
        raise SystemExit("ENTRENAMIENTO DETENIDO: faltan ejemplos REALES: " + ", ".join(faltantes) +
                         ". No se fabricaran registros ni se calculara una precision ficticia.")
    try:
        train_idx, val_idx, test_idx=obtener_particiones(y,grupos)
    except ValueError as exc:
        raise SystemExit(str(exc))
    Xtr,ytr=X[train_idx],y[train_idx]
    Xval,yval=X[val_idx],y[val_idx]
    Xte,yte=X[test_idx],y[test_idx]
    modelos={
        "arbol": DecisionTreeClassifier(max_depth=6,min_samples_leaf=6,class_weight="balanced",random_state=42),
        "random_forest": RandomForestClassifier(n_estimators=45,max_depth=8,min_samples_leaf=4,class_weight="balanced_subsample",random_state=42,n_jobs=-1),
        "knn": make_pipeline(StandardScaler(),KNeighborsClassifier(n_neighbors=min(7,len(ytr)),weights="distance")),
    }
    val={}
    for nombre, modelo in modelos.items():
        modelo.fit(Xtr,ytr)
        val[nombre]=score_modelo(modelo,Xval,yval)
    # Seguridad: recall de peligro prioritario, luego F1. Si hay empate practico, modelo mas simple.
    mejor_recall=max(v["recall_peligro"] for v in val.values())
    elegibles=[n for n in modelos if val[n]["recall_peligro"]>=mejor_recall-.02]
    mejor_f1=max(val[n]["f1_macro"] for n in elegibles)
    elegibles=[n for n in elegibles if val[n]["f1_macro"]>=mejor_f1-.02]
    orden_simple={"arbol":0,"knn":1,"random_forest":2}
    ganador=min(elegibles,key=lambda n:orden_simple[n])

    # Informe en test INDEPENDIENTE; no se decide el modelo utilizando estos resultados.
    elegido=modelos[ganador]
    pred=elegido.predict(Xte)
    matriz=confusion_matrix(yte,pred,labels=[0,1,2])
    resumen={
        "conteo_etiquetas":dict((CLASES[i],conteos.get(i,0)) for i in range(3)),
        "columnas_utilizadas":FEATURES,
        "modelo_elegido":ganador,
        "criterio":"recall de peligro prioritario; F1 macro; simplicidad",
        "validacion_modelos":val,
        "particion_muestras":{
            "train":len(train_idx),"validacion":len(val_idx),"prueba":len(test_idx),
            "proporcion_real":[round(len(idx)/len(y),4) for idx in (train_idx,val_idx,test_idx)],
        },
        "particion_sesiones":{
            "train":sorted(set(grupos[train_idx])),
            "validacion":sorted(set(grupos[val_idx])),
            "prueba":sorted(set(grupos[test_idx])),
        },
        "metricas_test":score_modelo(elegido,Xte,yte),
        "reporte_por_clase_test":classification_report(yte,pred,labels=[0,1,2],target_names=CLASES,zero_division=0,output_dict=True),
        "matriz_confusion_test":matriz.tolist(),
        "advertencia":"Clasificador academico experimental; NO es un detector certificado de gases o fuego."
    }
    args.salida.mkdir(parents=True,exist_ok=True)
    (args.salida/'metricas_3clases.json').write_text(json.dumps(resumen,ensure_ascii=False,indent=2),encoding='utf-8')
    fig,ax=plt.subplots(figsize=(6.2,5.2))
    ConfusionMatrixDisplay(confusion_matrix=matriz,display_labels=CLASES).plot(ax=ax,values_format='d',colorbar=False)
    ax.set_title('Matriz de confusión · pruebas por sesión')
    fig.tight_layout()
    fig.savefig(args.salida/'matriz_confusion.png',dpi=170)
    plt.close(fig)
    joblib.dump({"modelo":elegido,"nombre":ganador,"features":FEATURES,"clases":CLASES},args.salida/'modelo_3clases.joblib')
    joblib.dump({"modelos":modelos,"features":FEATURES,"clases":CLASES},args.salida/'modelos_comparados.joblib')
    print(f"MODELO ELEGIDO: {ganador}")
    print('Particion real:',resumen['particion_muestras'])
    print('Validacion:',json.dumps(val,indent=2))
    print('PRUEBA INDEPENDIENTE:',json.dumps(resumen['metricas_test'],indent=2))
    print(f"Modelo y reporte guardados en {args.salida}")

if __name__=='__main__':
    main()

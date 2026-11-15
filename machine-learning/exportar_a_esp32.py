"""Exporta un modelo SUPERVISADO validado (arbol o Random Forest) a cabecera C++.

No se puede ejecutar hasta disponer de un modelo entrenado de tres clases.
No exporta el detector de anomalias exploratorio como sistema de seguridad.
"""
from __future__ import annotations
import argparse
from pathlib import Path
import joblib
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from rutas import RESULTADOS
from funciones_ml import FEATURES


def literal_float(value: float) -> str:
    # Los literales 0f/1f no son validos en C++; deben ser 0.0f/1.0f.
    s = format(float(value), '.9g')
    if not any(x in s for x in ('.', 'e', 'E')):
        s += '.0'
    return s + 'f'


def generar_funcion(arbol, num: int, n_clases: int) -> str:
    t=arbol.tree_
    lines=[f'static inline void evaluar_arbol_{num:03d}(const float x[12], float p[3]) {{']
    def recorrer(nodo, nivel):
        tab='    '*nivel
        if t.children_left[nodo] < 0:
            valores=np.asarray(t.value[nodo][0],dtype=float)
            probabilidades=valores/valores.sum()
            for k in range(n_clases):
                lines.append(f'{tab}p[{k}] += {literal_float(probabilidades[k])};')
        else:
            feat=int(t.feature[nodo]); umbral=float(t.threshold[nodo])
            lines.append(f'{tab}if (x[{feat}] <= {umbral:.17g}) {{')
            recorrer(int(t.children_left[nodo]),nivel+1)
            lines.append(f'{tab}}} else {{')
            recorrer(int(t.children_right[nodo]),nivel+1)
            lines.append(f'{tab}}}')
    recorrer(0,1)
    lines.append('}')
    return '\n'.join(lines)


def generar_cabecera(modelo):
    if modelo.n_features_in_ != 12:
        raise ValueError('El modelo debe recibir las 12 variables del proyecto.')
    if isinstance(modelo,DecisionTreeClassifier):
        arboles=[modelo]
    elif isinstance(modelo,RandomForestClassifier):
        arboles=list(modelo.estimators_)
    else:
        raise ValueError('Solo se exportan DecisionTree o RandomForest. Si gano kNN, no se inventara un modelo embebido.')
    if list(map(int,modelo.classes_)) != [0,1,2]:
        raise ValueError('El modelo debe contener las tres clases numeradas 0, 1, 2.')
    parts=['// GENERADO AUTOMATICAMENTE. SOLO PROTOTIPO ACADEMICO; NO DETECTOR CERTIFICADO.',
           '// Entradas (en este orden): MQ2, MQ6, MQ135, LLAMA; delta de cada uno; media movil de 5 de cada uno.',
           '// Resultado: 0=seguro, 1=precaucion, 2=peligro (CLASIFICACION EXPERIMENTAL).',
           '#ifndef MODELO_RIESGO_ESP32_H', '#define MODELO_RIESGO_ESP32_H', '']
    for i,arbol in enumerate(arboles):
        parts.append(generar_funcion(arbol,i,3))
        parts.append('')
    parts += ['static inline int clasificarRiesgoML(const float x[12]) {',
              '    float p[3] = {0.0f, 0.0f, 0.0f};']
    for i in range(len(arboles)):
        parts.append(f'    evaluar_arbol_{i:03d}(x, p);')
    parts += ['    int clase = 0;',
              '    if (p[1] > p[clase]) clase = 1;',
              '    if (p[2] > p[clase]) clase = 2;',
              '    return clase;',
              '}', '', '#endif // MODELO_RIESGO_ESP32_H','']
    return '\n'.join(parts)


def main():
    p=argparse.ArgumentParser()
    p.add_argument('--modelo',type=Path,default=RESULTADOS / 'modelo_3clases.joblib')
    p.add_argument('--salida',type=Path,default=RESULTADOS / 'modelo_riesgo_esp32.h')
    a=p.parse_args()
    if not a.modelo.is_file():
        raise SystemExit('Aun NO existe modelo de tres clases. Recoge muestras reales y ejecuta entrenar_3_clases.py.')
    obj=joblib.load(a.modelo)  # Archivo local confiable; joblib no admite archivos no confiables.
    if obj.get('features') != FEATURES:
        raise SystemExit('Orden de variables inesperado; no se exporta.')
    if obj.get('clases') != ('seguro','precaucion','peligro'):
        raise SystemExit('Clases del modelo inesperadas; no se exporta.')
    try:
        cabecera=generar_cabecera(obj['modelo'])
    except ValueError as e:
        raise SystemExit(str(e))
    a.salida.parent.mkdir(parents=True,exist_ok=True)
    a.salida.write_text(cabecera,encoding='utf-8')
    print(f'Cabecera creada: {a.salida}')
    print('ANTES de integrarla: comprobar equivalencia en 20 muestras conocidas y verificar RAM/flash en ESP32.')

if __name__=='__main__':
    main()

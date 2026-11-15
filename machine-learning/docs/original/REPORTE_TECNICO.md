# Informe técnico de avance — Machine Learning del carrito ESP32

## Objetivo

Preparar un conjunto de datos reproducible para estudiar el comportamiento de sensores MQ-2, MQ-6, MQ-135 y sensor óptico de llama, y dejar lista la tubería de aprendizaje automático para comparar clasificadores de **seguro, precaución y peligro** cuando haya datos reales de las tres clases.

## Procedencia y delimitación de los datos

Se utilizaron dos registros reales entregados por el equipo, ambos descritos como **aire limpio**. Cada registro serial tiene la forma `DATA,ms,mq2,mq6,mq135,llama,riesgo,llama_detectada`. Para no mezclar una etiqueta experimental con las reglas del firmware, `riesgo` y `llama_detectada` se conservaron como **provisionales**; no se usaron como verdad terreno.

| Sesión | Registro original | Filas | Primer `ms` | Último `ms` | Duración observada |
|---|---|---:|---:|---:|---:|
| 01 | `Texto pegado(5).txt` | 212 | 124544 | 350525 | 225.981 s |
| 02 | `Texto pegado(6).txt` | 171 | 90994 | 273064 | 182.070 s |
| **Total** | Dos sesiones independientes | **383** | — | — | — |

Los archivos `Texto pegado(4).txt` y otros fragmentos corresponden a versiones previas o condiciones no verificadas. **No se sumaron como sesiones adicionales** ni se etiquetaron como gases o llama; de este modo no se duplicaron registros o inventaron clases.

Se comprobaron formato, lecturas ADC dentro de 0–4095, orden temporal y unicidad por `(sesion, ms)`. No se rellenaron valores ni se generaron filas sintéticas.

## Variables

Se toman las cuatro lecturas ADC de la ESP32 y se construyen ocho características derivadas: la diferencia con la lectura anterior y el promedio de hasta cinco lecturas recientes de cada canal. Los historiales se reinician en cada sesión. La fecha/hora civil de sesiones antiguas no se infirió a partir de `millis()`.

## Estadística descriptiva (383 registros)

| Canal | Mínimo | Máximo | Media | Desviación estándar (muestral) |
|---|---:|---:|---:|---:|
| MQ-2 | 951 | 990 | 974.15 | 12.50 |
| MQ-6 | 848 | 888 | 872.17 | 12.51 |
| MQ-135 | 1043 | 1079 | 1060.21 | 12.18 |
| Llama | 4061 | 4095 | 4094.91 | 1.74 |

Las diferencias de nivel entre sesiones reflejan condiciones/calibraciones distintas y no prueban exposición a un gas. En la segunda sesión hubo una caída óptica puntual a ADC 4061, **sin detección de llama**. Esas lecturas son indicios de funcionamiento electrónico, no una prueba de sensibilidad o especificidad química.

### Gráfica

`resultados/grafica_sesiones_aire_limpio.png` presenta cada señal con su propio eje temporal desde el primer registro de la sesión. La primera sesión (212 muestras) abarca cerca de 3.77 min; la segunda (171 muestras), cerca de 3.03 min.

## Demostración exploratoria de ML

Se ajustó un **Isolation Forest** (`n_estimators=120`, `contamination=0.03`, `random_state=42`) con normalización previa (`StandardScaler`) sobre los primeros 70 % de cada sesión. Se revisaron los últimos 30 % de cada una: **267 registros para ajuste** y **116 para revisión**. En estos 116 registros, el detector marcó como atípicos **7 (6.03 %)**.

Esa fracción **no es la precisión del sistema** y no corresponde a una tasa de detección de peligro. La revisión comparte condiciones ambientales con el ajuste y no demuestra generalización a otras sesiones, gases, humos, obstáculos, fuentes de luz ni equipamientos. No se atribuye la etiqueta de peligro a las observaciones atípicas.

## Algoritmos supervisados listos para ejecutar

Quedan implementados: **árbol de decisión**, **Random Forest** y **k-NN**. El programa de entrenamiento requiere muestras representativas y etiquetadas de las tres clases; separa sesiones completas para entrenamiento, validación y prueba (aproximadamente 70/15/15 según el número y tamaño de sesiones disponibles). La elección del modelo prioriza el recuerdo (`recall`) de la clase peligro en validación y, como criterio secundario, F1 macro y sencillez. La matriz de confusión y las métricas del test se calculan solo después de la selección.

**Estado actual del dataset**: seguro = 383; precaución = 0; peligro = 0. La rutina no entrena un clasificador de tres clases mientras no haya ejemplos reales suficientes y distribución de sesiones que permita evaluar honestamente.

## Limitaciones y trabajo pendiente

1. Recopilar sesiones nuevas, supervisadas, en condiciones verificadas y documentar etiquetas; **no generar exposiciones peligrosas improvisadas**.
2. Conseguir suficientes sesiones independientes y al menos 300 muestras reales por clase según el plan del proyecto.
3. Entrenar, comparar y evaluar con muestras de sesiones no usadas para ajustar el modelo; reportar los valores reales de matriz de confusión, precisión, recall y F1.
4. Exportar el clasificador embebible, verificar equivalencia de predicciones con muestras conocidas y probar funcionamiento real con Wi-Fi/MQTT y el carrito.

## Conclusión provisional

Se completó una **base reproducible de adquisición, limpieza, caracterización y detección exploratoria de anomalías**. Todavía no existe evidencia empírica para afirmar que el sistema distingue tres niveles de riesgo ni que detecta de manera confiable gases o fuego; el prototipo no sustituye un dispositivo certificado.

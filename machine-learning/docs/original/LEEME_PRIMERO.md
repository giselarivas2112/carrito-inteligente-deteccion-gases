# Machine Learning - Carrito inteligente ESP32

Paquete de trabajo **reproducible** preparado con lecturas verdaderas enviadas por Fran. No se fabricaron ejemplos de riesgo, ni se atribuyó a la linterna del celular la condición de llama real.

## 1. Qué está hecho y qué no

**Hecho y comprobado:**
- 383 lecturas reales de dos sesiones de aire limpio (212 + 171) en `datos/`, con los TXT originales preservados en `fuentes/`.
- Importador de registros `DATA,...` del monitor serial (`preparar_datos.py`).
- Captura automática USB a CSV (`capturar_serial.py`, requiere un ESP32 conectado).
- Extracción de **12 características**: 4 lecturas, sus 4 diferencias con la lectura anterior y 4 medias móviles de hasta 5 lecturas.
- Estadísticas y gráfica de ambas sesiones (`analizar_aire_limpio.py`).
- Un **Isolation Forest experimental** entrenado solo con aire limpio. Se guarda en `resultados/modelo_anomalias_DEMO.joblib`.
- Puntuación exploratoria por registro (`predecir_anomalias.py`).
- Código listo para comparar árbol de decisión, Random Forest y k-NN **cuando** haya etiquetas verificadas de las tres clases (`entrenar_3_clases.py`).
- Exportador de árbol o Random Forest validados a `.h` para Arduino/ESP32 (`exportar_a_esp32.py`).

**No realizado aún porque faltan datos:**
- Entrenamiento válido y selección de un modelo **seguro / precaución / peligro**.
- Métricas de las tres clases, matriz de confusión del riesgo y un `.h` final de ese clasificador.
- Validación con el carrito real, integración MQTT y verificación de equivalencia del modelo en ESP32.

El proyecto requiere al menos **300 muestras reales por clase** y varias sesiones independientes. Para evaluación honesta por sesión, el programa pide que **cada clase** esté representada en al menos tres sesiones y que existan sesiones para entrenamiento, validación y prueba. La cantidad mínima no implica automáticamente que las muestras sean suficientes ni seguras.

## 2. Archivos principales

| Archivo | Para qué sirve |
|---|---|
| `datos/dataset_real_383_seguro.csv` | Dataset real de 383 registros, sin duplicación de sesiones |
| `fuentes/Texto pegado(5).txt` y `(6).txt` | Registros seriales de origen sin modificar |
| `datos/sesion_01_aire_limpio.csv` | Registro original de la sesión 1 (212) |
| `datos/sesion_02_aire_limpio.csv` | Registro original de la sesión 2 (171) |
| `resumen_datos.xlsx` | Resumen y datos para inspección en Excel |
| `resultados/grafica_sesiones_aire_limpio.png` | Gráfica de los cuatro canales por sesión |
| `resultados/reporte_aire_limpio.json` | Estadísticas y revisión exploratoria del detector |
| `resultados/modelo_anomalias_DEMO.joblib` | Modelo de anomalías de **demostración**, no de seguridad |
| `resultados/puntuaciones_anomalias_DEMO.csv` | Puntuaciones de prueba; incluye datos usados en entrenamiento |
| `REPORTE_TECNICO.md` | Resultados e interpretación para redactar el artículo |

## 3. Ejecutar en Windows

Abrí la terminal en la carpeta `ML_Carrito_Fran` y ejecutá:

```powershell
python -m pip install -r requirements.txt
python analizar_aire_limpio.py
python predecir_anomalias.py
```

Los resultados quedan dentro de `resultados/`. Si la PC tiene `py` en lugar de `python`, podés usar `py` en esos comandos.

### Capturar nuevos datos reales del ESP32

1. Conectá la ESP32 por USB y averiguá su puerto, por ejemplo `COM5`.
2. **Cerrá el Monitor Serial del Arduino IDE**, porque dos programas no pueden usar el mismo puerto a la vez.
3. Si acaba de encenderse la placa, dejá que finalice precalentamiento/calibración. El programa ignora textos que no empiezan por `DATA,`.
4. Para una nueva sesión **realmente realizada en aire limpio**, ejecutá:

```powershell
python capturar_serial.py --puerto COM5 --baudios 115200 --sesion sesion_03 --etiqueta seguro --escenario aire_limpio --duracion 180 --salida datos/sesion_03_aire_limpio.csv
```

Al terminar, ese CSV contiene timestamp de la PC (`fecha_pc`), tiempo del ESP32 (`ms`), lecturas y etiqueta declarada por quien hizo la prueba. Anotá también fecha, lugar, condiciones, duración y fuente de la etiqueta en tu bitácora. **No etiquetés registros como precaución/peligro basándote solo en `riesgo_provisional`**: ese campo proviene de umbrales fijados a mano.

### Si ya tenés un TXT con líneas `DATA,...`

```powershell
python preparar_datos.py --archivo "registro_nuevo.txt" --sesion sesion_03 --etiqueta seguro --escenario aire_limpio --salida datos/sesion_03_aire_limpio.csv
```

Las etiquetas deben describir condiciones realmente observadas. **No improvisen fugas peligrosas ni llamas** para obtener registros. Planifiquen pruebas con docente/supervisor y procedimientos adecuados.

## 4. Entrenar tres clases cuando estén los datos

Cuando tengas CSV adicionales reales y correctamente etiquetados de `precaucion` y `peligro`, ejecutá (cambia los nombres según tus archivos):

```powershell
python entrenar_3_clases.py --datos datos/dataset_real_383_seguro.csv datos/sesion_03.csv datos/sesion_04.csv datos/sesion_05.csv datos/sesion_06.csv datos/sesion_07.csv datos/sesion_08.csv
```

Se exige cobertura suficiente **por clase y por sesión**. Si falta, el programa termina con un mensaje y no inventa métricas. Si la información es suficiente, compara **árbol de decisión, Random Forest y k-NN**, da prioridad al `recall` de peligro en validación, elige un modelo, mide una sola vez el conjunto de prueba independiente y guarda las métricas, matriz de confusión y modelo `.joblib`.

Para exportar un árbol o un bosque aleatorio elegidos al ESP32:

```powershell
python exportar_a_esp32.py
```

Genera `resultados/modelo_riesgo_esp32.h`. **Si gana k-NN, el exportador se niega a sustituirlo silenciosamente** por otro: habría que elegir/validar una implementación embebida por separado. Un `.joblib` de scikit-learn no se carga directamente en Arduino.

El encabezado `.h` espera un arreglo `float x[12]` en el orden exacto de `funciones_ml.FEATURES`. El código del ESP32 debe calcular las diferencias respecto a la lectura anterior y medias móviles de 5, **reiniciadas al comienzo de cada sesión/arranque**. Antes de instalarlo en el carrito: comprobar que predice las mismas 20 muestras en PC y ESP32 y probar el consumo de memoria.

## 5. Cómo interpretar los resultados actuales

- `modelo_anomalias_DEMO.joblib` detecta observaciones **atípicas respecto al aire limpio**. No puede distinguir gas de calor, vibración, iluminación, cambios de alimentación o cambios ambientales.
- `fraccion_marcada_atipica_en_revision` **no es exactitud**, no es sensibilidad de gas y no es tasa de falsos positivos de un detector de peligro.
- `riesgo_provisional` y `llama_detectada_provisional` son salidas del código del ESP32, **no verdad terreno** para entrenar.
- Los sensores MQ son poco selectivos; el sensor óptico responde a luz intensa. No debe usarse el prototipo como dispositivo certificado de seguridad.

### Orden de las 12 variables

`mq2, mq6, mq135, llama, delta_mq2, delta_mq6, delta_mq135, delta_llama, media5_mq2, media5_mq6, media5_mq135, media5_llama`

**Conexiones existentes:** MQ-2 AO→GPIO35; MQ-6 AO→GPIO34; MQ-135 AO→GPIO32; llama AO→GPIO33, todos ADC1. Con los sensores MQ a 5 V, las entradas analógicas de la ESP32 deben estar protegidas con los divisores de voltaje ya utilizados.

## 6. Nota para el artículo

La metodología y los valores reales de ambas sesiones figuran en `REPORTE_TECNICO.md`. Las gráficas y modelos de este paquete son reproducibles a partir del dataset CSV. Las métricas de clasificación de riesgo aún **no existen**. Documentá quién supervisó la captura, la definición real de las etiquetas y el protocolo de evaluación del modelo antes de declarar conclusiones.

# Machine learning del carrito ESP32

Integración de `Proyecto_Machine_Learning_ESP32.zip` en el repositorio. Esta carpeta
contiene adquisición de datos, análisis, entrenamiento y exportación del modelo.

## Estado actual

- 383 lecturas de aire limpio, en dos sesiones de 212 y 171 registros.
- Detector experimental de anomalías (Isolation Forest) reproducible con esos datos.
- Captura compatible con el firmware actual del repo y con los registros `DATA` del ZIP.
- Preparación de 12 variables y comparación de árbol, Random Forest y k-NN para
  cuando existan datos etiquetados de las tres clases.

Una lectura atípica **no equivale a gas, llama ni peligro**. Actualmente faltan
ejemplos de `precaucion` y `peligro`; no existe un clasificador validado de riesgo.

## Ejecutar ahora, sin ESP32

Desde la raíz del repositorio, en PowerShell:

```powershell
python -m pip install -r .\machine-learning\requirements.txt
python .\machine-learning\analizar_aire_limpio.py
python .\machine-learning\predecir_anomalias.py
```

Los archivos se generan en `machine-learning/resultados/`:

| Archivo | Contenido |
|---|---|
| `reporte_aire_limpio.json` | Estadísticas y revisión temporal de la demo |
| `grafica_sesiones_aire_limpio.png` | Gráfica de las cuatro señales |
| `modelo_anomalias_DEMO.joblib` | Modelo entrenado localmente |
| `puntuaciones_anomalias_DEMO.csv` | Puntuaciones de las 383 filas, incluidas filas de entrenamiento |

Las rutas **por defecto** se resuelven respecto a esta carpeta, incluso al ejecutar
desde la raíz. Las rutas explícitas de `--datos`, `--salida` y `--archivo` son
relativas a la terminal. También funciona entrar a `machine-learning` y ejecutar
`python analizar_aire_limpio.py`. `python machine-learning/entrenar` es un acceso
corto a esa demo; no entrena las tres clases.

Dependencias: Python 3.10 o posterior y `requirements.txt`. El entorno `.venv`
que ya estaba en el repositorio es de Linux/Python 3.12; no se usa para estos
comandos de Windows. Si prefieres un entorno aislado, desde la raíz:

```powershell
python -m venv .\machine-learning\.venv-win
.\machine-learning\.venv-win\Scripts\python.exe -m pip install -r .\machine-learning\requirements.txt
.\machine-learning\.venv-win\Scripts\python.exe .\machine-learning\analizar_aire_limpio.py
.\machine-learning\.venv-win\Scripts\python.exe .\machine-learning\predecir_anomalias.py
```

## Capturar nuevas sesiones

El firmware `Firmware/Firmware.ino` usa estos pines y envía el CSV en este orden:

| Posición serial | Canal | GPIO |
|---|---|---|
| 1 | MQ-6 | 34 |
| 2 | MQ-2 | 35 |
| 3 | MQ-135 | 32 |
| 4 | Llama | 33 |

El lector normaliza ese orden al del dataset: **mq2, mq6, mq135, llama**.
No es necesario cambiar el firmware para capturar. El firmware actual no hace
calentamiento ni calibración: comienza la captura una vez estabilizados los sensores.
Cierra el Monitor Serial antes de abrir el puerto desde Python.

Desde la raíz, sustituye COM5 por el puerto real:

```powershell
python -m serial.tools.list_ports
python .\machine-learning\capturar_serial.py --puerto COM5 --sesion sesion_03_aire_limpio --etiqueta seguro --escenario aire_limpio --duracion 180 --salida .\machine-learning\datos\sesion_03_aire_limpio.csv
```

`recolectar` acepta los mismos argumentos. `--formato auto` reconoce:

- `mq6,mq2,mq135,llama`: firmware del repo. `ms` es tiempo transcurrido en la PC
  desde la apertura de la captura. `origen` indica `reloj=pc`; los campos de riesgo
  provisional quedan vacíos, porque el firmware no los envía.
- `DATA,ms,mq2,mq6,mq135,llama,riesgo,llama_detectada`: formato del ZIP.
  Conserva el tiempo de la placa y registra `reloj=esp32`.

Puedes fijar `--formato firmware-repo` o `--formato data`. La duración comienza
con la primera muestra válida; `--duracion 0` permite detener con Ctrl+C.
`--espera 120` limita a 120 segundos los periodos sin datos válidos, y se puede
aumentar si el otro firmware tarda más en iniciar. Un reinicio detectable del
reloj `DATA` detiene la captura; el formato de cuatro columnas no permite detectar
reinicios de la placa. Usa un ID de sesión y archivo nuevos por captura/arranque.
El programa conserva las filas recibidas y no sobrescribe capturas existentes.

`etiqueta` describe la condición observada por el experimentador; no se deduce de
umbrales del firmware. Documenta condiciones, fecha, alimentación y criterio de
etiquetado. La captura real por USB queda pendiente de probar con la placa.

## Importar registros anteriores

Los TXT originales se conservan intactos en `fuentes/`. Para un nuevo TXT con
registros `DATA` (desde la raíz):

```powershell
python .\machine-learning\preparar_datos.py --archivo registro.txt --sesion sesion_04 --etiqueta seguro --escenario aire_limpio --salida .\machine-learning\datos\sesion_04.csv
```

El importador no inventa tiempos para TXT de cuatro columnas. Los duplicados
idénticos se omiten; tiempos repetidos con datos contradictorios se rechazan.
`--anexar` combina con un CSV existente sin repetir `(sesion, ms)`.
No debes sumar los CSV individuales y el consolidado como observaciones distintas:
el cargador los deduplica por esa misma clave.

## Entrenar tres clases más adelante

```powershell
python .\machine-learning\entrenar_3_clases.py --datos .\machine-learning\datos\dataset_real_383_seguro.csv
```

Con los datos actuales, este comando **se detiene e informa las clases faltantes**.
Cuando tengas nuevos CSV, agrégalos separados por espacios después de `--datos`.
El mínimo configurable heredado del paquete es 300 muestras por clase
(`--minimo-clase`); no garantiza validez por sí solo. Cada clase debe aparecer en
al menos tres sesiones y las tres particiones deben contener las tres clases.

Se dividen sesiones completas entre entrenamiento, validación y prueba, intentando
aproximar 70/15/15 sin separar una sesión. Con pocas sesiones las proporciones
pueden ser distintas. El modelo se elige usando validación; el test se reserva
para el informe final. Deltas y medias móviles se calculan por sesión, sin mezclar
historiales. Las columnas de riesgo provisional no se usan como variables ni etiquetas.

Para exportar posteriormente un árbol o Random Forest entrenado localmente:

```powershell
python .\machine-learning\exportar_a_esp32.py
```

Genera `resultados/modelo_riesgo_esp32.h`. k-NN no se exporta. El firmware deberá
calcular las mismas 12 variables; la equivalencia y compilación en ESP32 quedan
pendientes. Carga únicamente archivos joblib de confianza.

## Procedencia y resultados

Los CSV, TXT y `resumen_datos.xlsx` se copiaron del ZIP sin alterar su contenido.
Los documentos originales están en `docs/original/`; su manifiesto describe los
archivos **del ZIP**, no los scripts adaptados. El informe original es histórico;
el reporte JSON regenerado es la referencia de cada ejecución. Los binarios
del modelo del ZIP se sustituyen por entrenamiento local reproducible.

La demo utiliza 267 muestras para ajuste y 116 para revisión temporal. Ambas
partes comparten sesiones: la fracción marcada atípica no es precisión ni prueba
de generalización. Las ventanas de revisión pueden usar lecturas pasadas del
tramo de ajuste, como en un flujo continuo; nunca lecturas futuras.
El periodo de muestreo del firmware del repo difiere del de los TXT originales;
registra y mantén una cadencia comparable al reunir nuevos datos, porque deltas y
medias se definen por número de lecturas, no por segundos.

`resultados/`, cachés y entornos nuevos se ignoran en Git. El `.venv` que ya estaba
versionado no se elimina de Git mediante esta integración.

## Verificación

```powershell
python -m unittest discover -s .\machine-learning\tests -v
```

Las pruebas cubren procedencia de las 383 filas, deduplicación, conflictos,
orden serial, captura con puerto simulado y límites de espera, reinicio de
características y separación de sesiones. No representan una validación física
de detección de gas o llama.

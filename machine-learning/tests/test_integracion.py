"""Pruebas de integridad y compatibilidad; no validan deteccion fisica de gases."""
import csv
import io
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

import numpy as np

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from capturar_serial import capturar
from entrenar_3_clases import obtener_particiones
from funciones_ml import construir_features, leer_dataset
from preparar_datos import escribir_csv, extraer_data
from protocolo_serial import interpretar_linea
from rutas import BASE_DIR, DATOS


class IntegracionTests(unittest.TestCase):
    def test_fuentes_reproducen_dataset(self):
        filas = []
        for numero, txt in ((1, 'Texto pegado(5).txt'), (2, 'Texto pegado(6).txt')):
            filas.extend(extraer_data(BASE_DIR / 'fuentes' / txt,
                                     f'sesion_0{numero}_aire_limpio', 'seguro', 'aire_limpio'))
        with (DATOS / 'dataset_real_383_seguro.csv').open(encoding='utf-8-sig', newline='') as f:
            originales = list(csv.DictReader(f))
        self.assertEqual(len(filas), 383)
        self.assertEqual([{k: str(v) for k, v in r.items()} for r in filas], originales)

    def test_no_duplicar_al_combinar_sesiones_y_consolidado(self):
        filas = leer_dataset(sorted(DATOS.glob('*.csv')))
        self.assertEqual(len(filas), 383)

    def test_features_reiniciadas_por_sesion(self):
        filas = leer_dataset([DATOS / 'dataset_real_383_seguro.csv'])
        x, _, grupos = construir_features(filas)
        self.assertEqual(x.shape, (383, 12))
        for grupo in set(grupos):
            primero = np.flatnonzero(grupos == grupo)[0]
            np.testing.assert_array_equal(x[primero, 4:8], [0, 0, 0, 0])
            np.testing.assert_array_equal(x[primero, 8:], x[primero, :4])

    def test_orden_de_canales_en_ambos_firmwares(self):
        repo = interpretar_linea('850,975,1060,4095', ms_pc=123)
        data = interpretar_linea('DATA,123,975,850,1060,4095,0,0')
        for campo in ('ms', 'mq2', 'mq6', 'mq135', 'llama'):
            self.assertEqual(repo[campo], data[campo])
        self.assertEqual(repo['riesgo_provisional'], '')
        self.assertEqual(repo['reloj'], 'pc')
        self.assertEqual(data['reloj'], 'esp32')

    def test_rechaza_ruido_y_adc_invalido(self):
        for linea in ('Calentando...', 'DATA,ms,mq2,mq6,mq135,llama,riesgo,llama',
                      'DATA,1,4096,0,0,0,0,0', 'DATA,-1,0,0,0,0,0,0', '0,-1,0,0'):
            self.assertIsNone(interpretar_linea(linea, ms_pc=0))

    def test_captura_serial_mapeada_a_csv(self):
        class Puerto:
            def readline(self):
                return b'850,975,1060,4095\n'
        salida = io.StringIO()
        with patch('capturar_serial.time.monotonic', side_effect=[0, 0, .01, .01, 2]):
            total = capturar(Puerto(), salida, sesion='prueba', etiqueta='seguro',
                             escenario='fixture', puerto='simulado', duracion=1)
        fila = list(csv.DictReader(io.StringIO(salida.getvalue())))[0]
        self.assertEqual(total, 1)
        self.assertEqual((fila['mq2'], fila['mq6']), ('975', '850'))
        self.assertIn('reloj=pc', fila['origen'])

    def test_captura_termina_aunque_desaparezcan_datos(self):
        class Puerto:
            lineas = iter([b'DATA,100,975,850,1060,4095,0,0\n', b''])
            def readline(self):
                return next(self.lineas)
        with patch('capturar_serial.time.monotonic', side_effect=[0, 0, .01, .01, .5, .6, 2]):
            self.assertEqual(capturar(Puerto(), io.StringIO(), sesion='prueba',
                                     etiqueta='seguro', escenario='fixture', puerto='simulado', duracion=1), 1)

    def test_reinicio_de_esp32_no_mezcla_sesiones(self):
        class Puerto:
            lineas = iter([b'DATA,100,975,850,1060,4095,0,0\n',
                           b'DATA,10,975,850,1060,4095,0,0\n'])
            def readline(self):
                return next(self.lineas)
        with patch('capturar_serial.time.monotonic', side_effect=[0, 0, .01, .01, .5, .6]):
            with self.assertRaisesRegex(ValueError, 'posible reinicio'):
                capturar(Puerto(), io.StringIO(), sesion='prueba', etiqueta='seguro',
                         escenario='fixture', puerto='simulado')

    def test_captura_sin_datos_tiene_limite(self):
        class Puerto:
            def readline(self):
                return b'ruido\n'
        with patch('capturar_serial.time.monotonic', side_effect=[0, 0, .1, 3]):
            with self.assertRaisesRegex(ValueError, 'Sin lecturas validas'):
                capturar(Puerto(), io.StringIO(), sesion='prueba', etiqueta='seguro',
                         escenario='fixture', puerto='simulado', espera=2)

    def test_conflictos_no_se_descartan_silenciosamente(self):
        fila = dict(sesion='s1', ms=1, mq2=100, mq6=200, mq135=300, llama=4095,
                    etiqueta='seguro', escenario='fixture')
        with tempfile.TemporaryDirectory() as tmp:
            archivo = Path(tmp) / 'datos.csv'
            escribir_csv(archivo, [fila, {**fila, 'mq2': 101}])
            with self.assertRaisesRegex(ValueError, 'contradictorios'):
                leer_dataset([archivo])

    def test_particiones_con_una_clase_por_sesion(self):
        # Solo etiquetas/IDs de prueba: no se agregan muestras al dataset real.
        y = np.repeat([0, 1, 2], 30)
        grupos = np.repeat([f'fixture_{i}' for i in range(9)], 10)
        particiones = obtener_particiones(y, grupos)
        conjuntos = [set(grupos[idx]) for idx in particiones]
        for idx in particiones:
            self.assertEqual(set(y[idx]), {0, 1, 2})
        self.assertFalse(conjuntos[0] & conjuntos[1] | conjuntos[0] & conjuntos[2] | conjuntos[1] & conjuntos[2])


if __name__ == '__main__':
    unittest.main()

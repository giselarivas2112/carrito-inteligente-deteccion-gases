"""Rutas por defecto estables al ejecutar desde el repo o desde esta carpeta."""
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
DATOS = BASE_DIR / "datos"
RESULTADOS = BASE_DIR / "resultados"

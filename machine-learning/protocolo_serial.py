"""Normaliza el firmware del repo y los registros DATA del paquete original."""


def interpretar_linea(linea, formato="auto", ms_pc=None):
    partes = [p.strip() for p in linea.strip().split(",")]
    try:
        if formato in ("auto", "data") and len(partes) == 8 and partes[0] == "DATA":
            ms, mq2, mq6, mq135, llama, riesgo, deteccion = map(int, partes[1:])
            reloj = "esp32"
        elif formato in ("auto", "firmware-repo") and len(partes) == 4:
            if ms_pc is None:
                return None
            # Firmware/Firmware.ino imprime MQ6 ANTES de MQ2.
            mq6, mq2, mq135, llama = map(int, partes)
            ms, riesgo, deteccion, reloj = ms_pc, "", "", "pc"
        else:
            return None
    except ValueError:
        return None
    if ms < 0 or not all(0 <= v <= 4095 for v in (mq2, mq6, mq135, llama)):
        return None
    return dict(ms=ms, mq2=mq2, mq6=mq6, mq135=mq135, llama=llama,
                riesgo_provisional=riesgo, llama_detectada_provisional=deteccion,
                reloj=reloj)

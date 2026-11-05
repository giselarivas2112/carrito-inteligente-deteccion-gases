const int PIN_MQ6 = 34, PIN_MQ2 = 35, PIN_MQ135 = 32, PIN_FLAMA = 33;

int leer(int pin) {           // promedio de 10 muestras para reducir ruido
  long s = 0;
  for (int i = 0; i < 10; i++) { s += analogRead(pin); delay(5); }
  return s / 10;
}

void setup() { Serial.begin(115200); }

void loop() {
  Serial.printf("%d,%d,%d,%d\n", leer(PIN_MQ6), leer(PIN_MQ2), leer(PIN_MQ135), leer(PIN_FLAMA));
  delay(500);
}


#include <SoftwareSerial.h>
#define RX 5
#define TX 6
float otmp = 0;
SoftwareSerial HM10(RX, TX);
int tin = 0;

#include <Wire.h>
#include "MAX30100_PulseOximeter.h"

#define REPORTING_PERIOD_MS 1000

PulseOximeter pox;
uint32_t tsLastReport = 0;

void onBeatDetected()
{
  Serial.println("Beat!");
}


void setup() {
  HM10.begin(9600);
  Serial.begin(115200);
  Serial.print("Initializing pulse oximeter..");
  if (!pox.begin()) {
    Serial.println("FAILED");
    for (;;);
  } else {
    Serial.println("SUCCESS");
  }
  pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);

  // Register a callback for the beat detection
  pox.setOnBeatDetectedCallback(onBeatDetected);
}

void loop() {
  int hr = 0;
  int spo2 = 0;
  pox.update();
    if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
        Serial.print("Heart rate:");
        Serial.print(pox.getHeartRate());
        Serial.print("bpm / SpO2:");
        Serial.print(pox.getSpO2());
        Serial.println("%");
        float x = 0;
        x += (float)(analogRead(tin) / 1024.0 * 330);
        HM10.print("{" + String(x) + "," + hr + "," + spo2 + "}");

        tsLastReport = millis();
        
    }
 }

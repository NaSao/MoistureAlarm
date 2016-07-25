#include "UbidotsWizFi250.h"
#define TOKEN "78vJiasUytrRniEt1BOrfd4vbZiNlv"  // Replace it with your Ubidots token
#define ID "5793725176254220f1167436" // Replace it with your Ubidots' variable ID

#define WLAN_SSID       "Triger"  // Your WiFi SSID, cannot be longer than 32 characters!
#define WLAN_PASS       "nayuqi0406"  // Replace it with your WiFi pass
// Security can be OPEN, WEP, WPA, WPAAES, WPA2AES, WPA2TKIP, WPA2
#define WLAN_SECURITY   "WPA2"

Ubidots client(TOKEN);

void setup() {
  Serial.begin(9600);
  
  while(!client.wifiConnection(WLAN_SSID, WLAN_PASS, WLAN_SECURITY));

}

void loop() {
  int value = analogRead(A4);
  client.add(ID,value);
  client.sendAll();
  Serial.println("--------------------");
  delay(1000);
 
}

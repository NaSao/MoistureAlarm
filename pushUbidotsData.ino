#include <Arduino.h>
#include <SoftwareSerial.h>

#define SERVER "50.23.124.68"
#define  REMOTE_PORT "80"

#define TX 2
#define RX 3
#define BAUDRATE 115200

#define _token "YNbIJdlwtH8s9p4xYNlZTuqhB6bfYY"  // Replace it with your Ubidots token
#define ID "5795e2f876254249c8ce246a" // Replace it with your Ubidots' variable ID

#define WLAN_SSID       "Nasao"  // Your WiFi SSID, cannot be longer than 32 characters!
#define WLAN_PASS       "12345678"  // Replace it with your WiFi pass
// Security can be OPEN, WEP, WPA, WPAAES, WPA2AES, WPA2TKIP, WPA2
#define WLAN_SECURITY   "WPA2"
#define DEBUG_UBIDOTS


typedef struct Value {
  char  *id;
  float value_id;
} Value;

//Ubidots client(TOKEN);
SoftwareSerial _client = SoftwareSerial(TX, RX);
int maxValues = 5;
int currentValue = 0;
Value * val = (Value *)malloc(maxValues*sizeof(Value));

char* readData(uint16_t timeout){
  char replybuffer[500];
  uint16_t replyidx = 0;
  while (timeout--) {
    if (replyidx >= 500) {
      break;
    }
    while(_client.available()) {
      int c =  _client.read();
      if ((c>31 && c<126) || c=='\n') {
        replybuffer[replyidx] = char(c);
        replyidx++;
      }
      
    }

    if (timeout == 0) {
      break;
    }
    delay(1);
  }
  replybuffer[replyidx] = '\0';  // null term
#ifdef DEBUG_UBIDOTS
  Serial.println(F("Response of GPRS:"));
  Serial.println(replybuffer);
#endif
  while(_client.available()){
    _client.read();
  }
  return replybuffer;
}
/** 
 * This function is to read the data from GPRS pins. This function is from Adafruit_FONA library
 * @arg timeout, time to delay until the data is transmited
 * @return replybuffer the data of the GPRS
 */
bool wifiConnection(const char *ssid, const char *phrase, const char *auth){
    _client.write("AT\r\n");
    
    _client.write("AT+WSET=0,");
    _client.write(ssid);
    _client.write("\r\n");
    
    _client.write("AT+WSEC=0,");
    _client.write(auth);
    _client.write(",");
    _client.write(phrase);
    _client.write("\r\n");
   
    _client.write("AT+WJOIN\r\n");
    if(strstr(readData(8000),"Successfully joined")==NULL){
#ifdef DEBUG_UBIDOTS
        Serial.println(F("Error with AT+CSQ with all"));
#endif
        return false;
    }
    delay(2000);
    return true;
    
}
/**
 * Add a value of variable to save
 * @arg variable_id variable id to save in a struct
 * @arg value variable value to save in a struct
 */
void add(char *variable_id, double value){
  (val+currentValue)->id = variable_id;
  (val+currentValue)->value_id = value;
  currentValue++;
  if(currentValue>maxValues){
    Serial.println(F("You are sending more than 5 consecutives variables, you just could send 5 variables. Then other variables will be deleted!"));
    currentValue = maxValues;
  }
}
/**
 * Send all data of all variables that you saved
 * @reutrn true upon success, false upon error.
 */
bool sendAll(){
    int i;
    char vals[5];
    char data[350];    
    sprintf(data,"[");
    for(i=0; i<currentValue;){
      dtostrf((val + i)->value_id, 5, 3, vals);
      sprintf(data, "%s{\"variable\": \"%s\", \"value\":\"%s\"}", data, (val + i)->id, vals);
      i++;
      if(i<currentValue){
        sprintf(data,"%s, ", data);
      }
    }
    sprintf(data, "%s]", data);
    Serial.println(data);
    _client.write("AT+SCON=O,TCN,");
    _client.write(SERVER);
    _client.write(",");
    _client.write(REMOTE_PORT);
    _client.write(",");
    _client.write(",1\r\n");

      sprintf(vals,"%d",strlen(data));
      
    _client.write("POST /api/v1.6/collections/values/?force=true HTTP/1.1");
    _client.write("\r\n");
    _client.write("Host: things.ubidots.com");
    _client.write("\r\n");
    _client.write("User-Agent: User-Agent: Arduino-WizFi250/1.0");
    _client.write("\r\n");
    _client.write("X-Auth-Token: ");
    _client.write(_token);
    _client.write("\r\n");
    _client.write("Content-Type: application/json");
    _client.write("\r\n");
    _client.write("Connection: close");
    _client.write("\r\n");
    _client.write("Content-Length: ");
    _client.write(vals);    
    _client.write("\r\n");
    _client.write("\r\n");
    Serial.println(data);
    _client.write(data);
    //_client.write(data);
    _client.write("\r\n");
    _client.write("\r\n");

    while(strstr(readData(5000),"[OK]")!=NULL){
#ifdef DEBUG_UBIDOTS
        Serial.println(F("Error with AT+CSQ in Sending"));
#endif
        delay(2000);
        currentValue = 0;
        return false;
        
    }
    //_client.write("AT+SMGMT=ALL\r\n");
    currentValue = 0;
    delay(4000);
    return true;    
}

void setup() {
  Serial.begin(115200);
  _client.begin(BAUDRATE); 
  while(!wifiConnection(WLAN_SSID, WLAN_PASS, WLAN_SECURITY));

  
    Serial.println("Connected with WIFI");


}

void loop() {
  int value = analogRead(A4);
  add(ID,value);
  if(sendAll()){
    Serial.println("Send data success!");
    }else{
      Serial.println("Send data failure!");
      };
  Serial.println("--------------------");
  delay(6000);
 
}


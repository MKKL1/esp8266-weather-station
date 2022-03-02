
#include <Arduino.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BME280.h>
#include <ESP8266WiFi.h>
#include <WifiClient.h>

extern const char* ssid;
extern const char* password;
extern const char* hostIp;
//extern const uint16_t port;
const uint16_t port = 6969;


WiFiClient client;


struct serverProtocol {
  uint8_t protocol;
  uint8_t size;
  float temp;
  float pressure;
  float humidity;
} packet;

Adafruit_BME280 bme; // use I2C interface
Adafruit_Sensor *bme_temp = bme.getTemperatureSensor();
Adafruit_Sensor *bme_pressure = bme.getPressureSensor();
Adafruit_Sensor *bme_humidity = bme.getHumiditySensor();

int sendData();
int handshake();
void floatToBinary(float, byte*);
void ltobEndian(byte* from, byte* to);

void setup() {
  Serial.begin(9600);
  Serial.println(F("BME280 Sensor event test"));

  if (!bme.begin(0x76)) {
    Serial.println(F("Could not find a valid BME280 sensor, check wiring!"));
    while (1) delay(10);
  }
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);


  
  Serial.println(ssid);
  Serial.println(F("Connecting to WiFi..."));

  while(WiFi.status() != WL_CONNECTED ) { //TODO timeout i retry
    delay(500);
    Serial.print(".");
  }

  Serial.println("Wifi connected!");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());

  Serial.println("Connecting to host...");
  if(client.connect(hostIp, port)) {
    Serial.println("Connected to host!");
  } else {
    Serial.println("Connection to host failed"); //TODO tu powinien byc loop zeby laczy sie do skutku I za kazdym razem sprawdzal czy dalej jest polaczony do wifi
  }

  packet.protocol = 0;
  
  packet.size = 12;

  handshake();
  //bme_temp->printSensorDetails();
  //bme_pressure->printSensorDetails();
  //bme_humidity->printSensorDetails();
}

void loop() {
  sensors_event_t temp_event, pressure_event, humidity_event;
  bme_temp->getEvent(&temp_event);
  bme_pressure->getEvent(&pressure_event);
  bme_humidity->getEvent(&humidity_event);
  
  packet.temp = temp_event.temperature;
  packet.pressure = pressure_event.pressure;
  packet.humidity = humidity_event.relative_humidity;

  //Serial.print(F("Temperature = "));
  //Serial.print(temp_event.temperature);
  //Serial.println(" *C");


  //Serial.print(F("Pressure = "));
  //Serial.print(pressure_event.pressure);
  //Serial.println(" hPa");


  //Serial.print(F("Humidity = "));
  //Serial.print(humidity_event.relative_humidity);
  //Serial.println(" %");

  //Serial.println();

  sendData();


  delay(1000);
}

//
// jezeli sie nie powiedzie, to zwraca 0
int sendData()
{
  //jakis szajs pakiety

  unsigned char idProtokolu = packet.protocol & 0xFF;
  client.write(idProtokolu);

  unsigned char rozmPakietu = packet.size & 0xFF; //rozmiar
  client.write(rozmPakietu);

  byte data[4];
  byte swappedData[4];

  size_t len = 4;
  floatToBinary(packet.temp, data);  
  ltobEndian(data, swappedData);
  client.write(swappedData, len);


  floatToBinary(packet.pressure, data);
  ltobEndian(data, swappedData);
  client.write(swappedData, len);


  floatToBinary(packet.humidity, data);
  ltobEndian(data, swappedData);
  client.write(swappedData, len);

  return 1;
}

int handshake()//TODO handshake raczej powinien korzystać z oddzielnego structa
{

  unsigned char data;
  data = packet.protocol & 0xFF; //TODO zamiast tego ma byc funkcja ktora zamienia zmienne na bajty i zwraca byte array (tak sie nie da dla tego trzeba bedzie to jakos na wskaznikach)
  client.write(data);

  data = 1 & 0xFF; //rozmiar handshake
  client.write(data);


  data = 0 & 0xFF; //id klienta
  client.write(data);

  packet.protocol = 1;
  return 1;
}

void floatToBinary(float f, byte* val)
{
 byte* f_byte = reinterpret_cast<byte*>(&f);
 memcpy(val, f_byte, 4);
/*
  bytes[0] = (n >> 24) & 0xFF;
  bytes[1] = (n >> 16) & 0xFF;
  bytes[2] = (n >> 8) & 0xFF;
  bytes[3] = n & 0xFF;
*/
}

void ltobEndian(byte* arrFrom, byte* arrTo)
{
  arrTo[3] = arrFrom[0];
  arrTo[2] = arrFrom[1];  
  arrTo[1] = arrFrom[2];
  arrTo[0] = arrFrom[3];
}
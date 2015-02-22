
#include <Adafruit_CC3000.h>

#include <ccspi.h>
#include <SPI.h>
#include <string.h>
#include <avr/wdt.h>
#include "utility/debug.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins
// On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
SPI_CLOCK_DIVIDER); // you can change this clock speed

#define WLAN_SSID       "SSID"           // cannot be longer than 32 characters!
#define WLAN_PASS       "you know my password"
// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define WLAN_SECURITY   WLAN_SEC_WPA2

#define IDLE_TIMEOUT_MS  2200      // Amount of time to wait (in milliseconds) with no data 
// received before closing the connection.  If you know the server
// you're accessing is quick to respond, you can reduce this value.

// What page to grab!
#define WEBSITE      "www.xeamphiil.co.nf"
String WEBPAGE   =   "/index.php";
int Device_Id = 3;
uint32_t ip;

void setup(void)
{
  WEBPAGE +="?id="+String(Device_Id);
  randomSeed(42);
  Serial.begin(115200);
  //pinMode(13, OUTPUT);
  //Serial.println(WEBPAGE);

  Serial.print("Free RAM: "); 
  Serial.println(getFreeRam(), DEC);

  /* Initialise the module */
  Serial.println(F("\nInitializing..."));
  if (!cc3000.begin())
  {
    Serial.println(F("Couldn't begin()! Check your wiring?"));
    while(1);
  }

  // Optional SSID scan
  // listSSIDResults();

  Serial.print(F("\nAttempting to connect to ")); 
  Serial.println(WLAN_SSID);
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial.println(F("Failed!"));
    while(1);
  }

  Serial.println(F("Connected!"));

  /* Wait for DHCP to complete */
  Serial.println(F("Request DHCP"));
  while (!cc3000.checkDHCP())
  {
    delay(100); // ToDo: Insert a DHCP timeout!
  }  
   while (! displayConnectionDetails()) {
    delay(1000);
  }

  /* Display the IP address DNS, Gateway, etc. */
  ip = 0;
  // Try looking up the website's IP address
  Serial.print(WEBSITE); Serial.print(F(" -> "));
  while (ip == 0) {
    if (! cc3000.getHostByName(WEBSITE, &ip)) {
      Serial.println(F("Couldn't resolve!"));
    }
    delay(500);
  }

  cc3000.printIPdotsRev(ip);
  
  wdt_enable(WDTO_8S);
}
  char resp;
  boolean check;


  int counter = 0;

  Adafruit_CC3000_Client www;
  
void loop(void)
{
  
  
  wdt_reset();
  //cc3000.printIPdotsRev(ip);

  //Serial.println("Debug 1\n"); 
  
  www = cc3000.connectTCP(ip, 80);
  
  wdt_reset();
  //Serial.println("Debug 2\n"); 
  
  char WEBPAGEARRAY[200];
  (WEBPAGE+"&label="+String(random(1,5))+
  "&current="+String(random(1,50))+
  "&voltage="+String(random(200,300))+
  "&pf="+String(random(1,90))).toCharArray(WEBPAGEARRAY, 200);
  if (www.connected()) {
    www.fastrprint(F("GET "));
    www.fastrprint(WEBPAGEARRAY);
    www.fastrprint(F(" HTTP/1.1\r\n"));
    www.fastrprint(F("Host: ")); www.fastrprint(WEBSITE); www.fastrprint(F("\r\n"));
    www.fastrprint(F("\r\n"));
    www.println();
  } 
  else {
    Serial.println(F("Connection failed"));    
    return;
  }
  wdt_reset();
  //Serial.println("Debug 3\n"); 

  /* Read data until either the connection is closed, or the idle timeout is reached. */

  check = true;
  //Serial.println("Debug 4\n"); 

  unsigned long lastRead = millis();
  counter = 0;
  while (www.connected()) {

    // Serial.println("Debug 6\n"); 
    // Serial.println("Debug 6.1\n");
    while (www.available()) {
      check = false; 
      //Serial.println("Debug 6.2\n");

      //Serial.println(counter);

      lastRead = millis();

      if(counter == 116)
      {

        resp =www.read();
        break;      
      }
      else{
        www.read();
      }      
      counter++;
      // Serial.println("Debug 6.3\n");
    }
    if(counter == 116)
    {
      break;
    }

    if (!(check&& (millis() - lastRead < IDLE_TIMEOUT_MS)))
    {
      break;
    } 
    // Serial.println("Debug 6.5\n");
  }
  wdt_reset();
  //Serial.println(counter);
  Serial.println(resp);
  
  //Serial.println("Debug 7\n"); 

 
  //Serial.println(counter);
  //counter++; 
  www.close();
  //delay(1000);
  Serial.flush();
  // Serial.println("Debug 8\n"); 
  wdt_reset();
}


void listSSIDResults(void)
{
  uint32_t index;
  uint8_t valid, rssi, sec;
  char ssidname[33]; 

  if (!cc3000.startSSIDscan(&index)) {
    Serial.println(F("SSID scan failed!"));
    return;
  }

  Serial.print(F("Networks found: ")); 
  Serial.println(index);
  Serial.println(F("================================================"));

  while (index) {
    index--;

    valid = cc3000.getNextSSID(&rssi, &sec, ssidname);

    Serial.print(F("SSID Name    : ")); 
    Serial.print(ssidname);
    Serial.println();
    Serial.print(F("RSSI         : "));
    Serial.println(rssi);
    Serial.print(F("Security Mode: "));
    Serial.println(sec);
    Serial.println();
  }
  Serial.println(F("================================================"));

  cc3000.stopSSIDscan();
}

bool displayConnectionDetails(void)
{
  uint32_t ipAddress, netmask, gateway, dhcpserv, dnsserv;

  if(!cc3000.getIPAddress(&ipAddress, &netmask, &gateway, &dhcpserv, &dnsserv))
  {
    Serial.println(F("Unable to retrieve the IP Address!\r\n"));
    return false;
  }
  else
  {
    Serial.print(F("\nIP Addr: ")); 
    cc3000.printIPdotsRev(ipAddress);
    Serial.print(F("\nNetmask: ")); 
    cc3000.printIPdotsRev(netmask);
    Serial.print(F("\nGateway: ")); 
    cc3000.printIPdotsRev(gateway);
    Serial.print(F("\nDHCPsrv: ")); 
    cc3000.printIPdotsRev(dhcpserv);
    Serial.print(F("\nDNSserv: ")); 
    cc3000.printIPdotsRev(dnsserv);
    Serial.println();
    return true;
  }
}



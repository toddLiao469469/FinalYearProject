#include <SoftwareSerial.h>  
#define USE_ARDUINO_INTERRUPTS true    // Set-up low-level interrupts for most acurate BPM math.
#include <PulseSensorPlayground.h>     // Includes the PulseSensorPlayground Library. 

#define blueTooth_RX 8
#define blueTooth_TX 7
#define PulseWire A0
#define LED13 13

int Threshold = 550;
SoftwareSerial BT(blueTooth_RX,blueTooth_TX);   // 接收腳(RX), 傳送腳(TX)；接HC-06之TXD、RXD   
  
char val;  
String recieveData = "";   
bool startRecieve = false;  
void setup()  
{  
  Serial.begin(9600);   
  BT.begin(9600); //HC-06 預設 baud  
  Serial.println("12312321312312");

  pulseSensor.analogInput(PulseWire);   
  pulseSensor.blinkOnPulse(LED13);       //auto-magically blink Arduino's LED with heartbeat.
  pulseSensor.setThreshold(Threshold);   

  // Double-check the "pulseSensor" object was created and "began" seeing a signal. 
   if (pulseSensor.begin()) {
    Serial.println("We created a pulseSensor Object !");  //This prints one time at Arduino power-up,  or on Arduino reset.  
  }
}  
  
void loop()  
{  
  int myBPM = pulseSensor.getBeatsPerMinute();  

  if (pulseSensor.sawStartOfBeat()) {            // Constantly test to see if "a beat happened". 
  print a message "a heartbeat happened".
    Serial.print("BPM: ");                        // Print phrase "BPM: " 
    Serial.println(myBPM);                        // Print the value inside of myBPM. 
  }

  delay(20);                    // considered best practice in a simple sketch.

  while(BT.available()) //如果有收到資料  
  {  
    startRecieve = true;  
    val=BT.read(); //每次接收一個字元  
    recieveData += val; //字元組成字串  
    BT.write(byte(val)); //把每次收到的字元轉成byte封包傳至手機端  
    delay(200);  //每次傳輸間隔，如果太短會造成資料遺失或亂碼  
  }  
  
  if(startRecieve)  
  {  
  startRecieve = false;  
  Serial.println(recieveData); //呈現收到字串  
  recieveData = "";  
  }  
    delay(300);  

    
}  

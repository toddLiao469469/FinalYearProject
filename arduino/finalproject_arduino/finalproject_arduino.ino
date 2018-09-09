#include <SoftwareSerial.h>  
#define USE_ARDUINO_INTERRUPTS true    // Set-up low-level interrupts for most acurate BPM math.
#include <PulseSensorPlayground.h>     // Includes the PulseSensorPlayground Library. 

#define blueTooth_RX 8
#define blueTooth_TX 7
#define PulseWire A0
#define LED13 13
#define BPMCount 100

int Threshold = 550;
SoftwareSerial BT(blueTooth_RX,blueTooth_TX);   // 接收腳(RX), 傳送腳(TX)；接HC-06之TXD、RXD   
  
char val;  
String recieveData = "";   
bool startRecieve = false;  
int myBPMAverage = 0;
int myBPMCount = 0;

void setup()  
{  
  Serial.begin(9600);   
  BT.begin(9600);       //HC-06 預設 baud  
  Serial.println("12312321312312");

  pulseSensor.analogInput(PulseWire);   
  pulseSensor.blinkOnPulse(LED13);       
  pulseSensor.setThreshold(Threshold);   

  // Double-check the "pulseSensor" object was created and "began" seeing a signal. 
   if (pulseSensor.begin()) {
    Serial.println("We created a pulseSensor Object !");  //This prints one time at Arduino power-up,  or on Arduino reset.  
  }
}  
  
void loop()  
{  
  int myBPM = pulseSensor.getBeatsPerMinute();  
  myBPMAverage += myBPM;

  if (pulseSensor.sawStartOfBeat()) {             
    Serial.print("BPM: ");                        
    Serial.println(myBPM);                         
  }

  if(myBPMCount < BPMCount){
    myBPMCount++;
  }else if(myBPMCount >= BPMCount){
    myBPMAverage /= 100;
  }
  delay(20);                    

  while(myBPMCount >= BPMCount){ //如果有收到資料  
    char pushBPM[3];
    pushBPM[0] =char((myBPMAverage % 1000)- (myBPMAverage % 100) /100)
    pushBPM[1] =char((myBPMAverage % 100)- (myBPMAverage % 10) /10)
    pushBPM[2] =char(myBPMAverage % 10)
    
    for(int i = 0 ; i < 3 ; i++){
      BT.write(byte(pushBPM[i])); 
      delay(200);
    }
  
  }  
  
}  

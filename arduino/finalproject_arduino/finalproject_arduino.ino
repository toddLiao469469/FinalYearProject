#include <SoftwareSerial.h>  
#define USE_ARDUINO_INTERRUPTS true    // Set-up low-level interrupts for most acurate BPM math.
#include <PulseSensorPlayground.h>     // Includes the PulseSensorPlayground Library. 

#define blueTooth_RX 8
#define blueTooth_TX 7
#define PulseWire A1
#define LED13 13
#define BPMCount 20

int Threshold = 550;
SoftwareSerial BT(blueTooth_RX,blueTooth_TX);   // 接收腳(RX), 傳送腳(TX)；接HC-06之TXD、RXD   
PulseSensorPlayground pulseSensor;
char val;  
String recieveData = "";   
bool startRecieve = false;  
int myBPMAverage = 0;
int myBPMCount = 0;

void setup()  
{  
  Serial.begin(9600);   
  BT.begin(9600);       //HC-06 預設 baud  
  Serial.println("branchtest");

  pulseSensor.analogInput(PulseWire);   
  pulseSensor.blinkOnPulse(LED13);       
  pulseSensor.setThreshold(Threshold);   

  // Double-check the "pulseSensor" object was created and "began" seeing a signal. 
   if (pulseSensor.begin()) {
    Serial.println("We created a pulseSensor Object !");  //This prints one time at Arduino power-up,  or on Arduino reset.  
  }
  
  Serial.print("21312321");
  BT.write("A");
}  
  
void loop()  
{  
  int myBPM = pulseSensor.getBeatsPerMinute();  
  if(myBPM <=255 && myBPM >=0){
    myBPMAverage += myBPM;
  }
  if (pulseSensor.sawStartOfBeat()) {             
    Serial.print("BPM: ");                        
    Serial.println(myBPM);                         
  }

  if(myBPMCount < BPMCount){
    myBPMCount++;
  }
  if(myBPMCount >= BPMCount){
    myBPMAverage /= myBPMCount;
  }
  Serial.print("test aver:");
   Serial.println(myBPMAverage);

  delay(800);                    

  if(myBPMCount >= BPMCount){ //如果有收到資料  
    char pushBPM[3];
    pushBPM[0] =((myBPMAverage % 1000)- (myBPMAverage % 100))/100 + '0';
    pushBPM[1] =((myBPMAverage % 100)- (myBPMAverage % 10))/10 + '0';
    pushBPM[2] =(myBPMAverage % 10) + '0';
     Serial.println("");
     Serial.print("aver:");
     Serial.println(myBPMAverage);
     myBPMCount=0;
     myBPMAverage=0;
    for(int i = 0 ; i < 3 ; i++){
      Serial.println(pushBPM[i]);
      delay(500);
      BT.write(byte(pushBPM[i])); 
    }
  
  }  
  
}  



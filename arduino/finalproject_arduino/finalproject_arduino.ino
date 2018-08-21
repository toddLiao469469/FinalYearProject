#include <SoftwareSerial.h>  
SoftwareSerial BT(8,7);   // 接收腳(RX), 傳送腳(TX)；接HC-06之TXD、RXD   
  
char val;  
String recieveData = "";   
bool startRecieve = false;  
void setup()  
{  
  Serial.begin(9600);   
  BT.begin(9600); //HC-06 預設 baud  
  Serial.println("12312321312312");
}  
  
void loop()  
{  
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

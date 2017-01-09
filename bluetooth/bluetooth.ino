#include <SoftwareSerial.h>

SoftwareSerial android(2,3);
int inByte=0;
int incomingByte;
int dataLength = 0;
int data[128]={};
void setup(){
  android.begin(115200);

  Serial.begin(9600);

  Serial.write("init");

  pinMode(LED_BUILTIN, OUTPUT);

  
}

void loop(){

  if(android.available()){
    inByte=android.read();
    Serial.print(inByte);
   if(inByte==49){
   digitalWrite(LED_BUILTIN, HIGH);   
   }else
  digitalWrite(LED_BUILTIN, LOW);    
       
  }

if(android.available()){
  Serial.write(android.read());
}

int count = Serial.available();
  if (count > 0) {
    Serial.print(count);
    dataLength = count;
    for (int i=0; i < count; i++) {
      // 一旦バッファに入れ繰り返し表示できるようにする
      incomingByte = Serial.read();
      data[i] = incomingByte;
    }
  }



}


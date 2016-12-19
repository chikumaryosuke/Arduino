#include <SoftwareSerial.h>

SoftwareSerial android(2,3);
int inByte=0;
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
  android.write(Serial.read());
}



}


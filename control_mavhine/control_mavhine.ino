#include <SoftwareSerial.h>
const int motorA = 5;
const int motorB = 6;
//const int PWM_motAB=6;
const int motorC = 10;
const int motorD = 11;
//const int PWM_motCD=11;

void forward(){
  digitalWrite(motorA,HIGH);
  digitalWrite(motorB,LOW);
  digitalWrite(motorC,HIGH);
  digitalWrite(motorD,LOW);
//  analogWrite(PWM_motAB,200);
//  analogWrite(PWM_motCD,200);
  }

 void backward(){
  digitalWrite(motorA,LOW);
  digitalWrite(motorB,HIGH);
  digitalWrite(motorC,LOW);
  digitalWrite(motorD,HIGH);
//  analogWrite(PWM_motAB,200);
//  analogWrite(PWM_motCD,200);
  }
SoftwareSerial android(2,3);
int inByte=0;
int incomingByte;
int dataLength = 0;
int data[128]={};
void setup(){
  android.begin(115200);

  Serial.begin(9600);

  Serial.write("init");
  
  pinMode(motorA,OUTPUT); //信号用ピン
  pinMode(motorB,OUTPUT); //信号用ピン
  pinMode(motorC,OUTPUT); //信号用ピン
  pinMode(motorD,OUTPUT); //信号用ピン

  
}

void loop(){

  if(android.available()){
    inByte=android.read();
    Serial.print(inByte);
   if(inByte==49){
        //前進
  forward();
  delay(20000);
   }else
   
   backward();
  delay(5000);    
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


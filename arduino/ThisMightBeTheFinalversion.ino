//#############################################################
const int echopin=4; 
const int trigpin=3; 
int led = 10;
int count = 0;
//#############################################################
float lastrot[3];
float newrot[3];


#include <SoftwareSerial.h>
SoftwareSerial BTserial(6, 5); // RX | TX
// Connect the HC-06 TX to the Arduino RX on pin 3. 
// Connect the HC-06 RX to the Arduino TX on pin 4 through a voltage divider.
// 
//**********************************************************************************8
// I2Cdev and MPU6050 must be installed as libraries, or else the .cpp/.h files
// for both classes must be in the include path of your project
#include "I2Cdev.h"

#include "MPU6050_6Axis_MotionApps20.h"
//#include "MPU6050.h" // not necessary if using MotionApps include file

// Arduino Wire library is required if I2Cdev I2CDEV_ARDUINO_WIRE implementation
// is used in I2Cdev.h
#if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
    #include "Wire.h"
#endif

// class default I2C address is 0x68
// specific I2C addresses may be passed as a parameter here
// AD0 low = 0x68 (default for SparkFun breakout and InvenSense evaluation board)
// AD0 high = 0x69
MPU6050 mpu;
//MPU6050 mpu(0x69); // <-- use for AD0 high

// uncomment "OUTPUT_READABLE_QUATERNION" if you want to see the actual
// quaternion components in a [w, x, y, z] format (not best for parsing
// on a remote host such as Processing or something though)
//#define OUTPUT_READABLE_QUATERNION

// uncomment "OUTPUT_READABLE_EULER" if you want to see Euler angles
// (in degrees) calculated from the quaternions coming from the FIFO.
// Note that Euler angles suffer from gimbal lock (for more info, see
// http://en.wikipedia.org/wiki/Gimbal_lock)
//#define OUTPUT_READABLE_EULER

// uncomment "OUTPUT_READABLE_YAWPITCHROLL" if you want to see the yaw/
// pitch/roll angles (in radians) calculated from the quaternions coming
// from the FIFO. Note this also requires gravity vector calculations.
// Also note that yaw/pitch/roll angles suffer from gimbal lock (for
// more info, see: http://en.wikipedia.org/wiki/Gimbal_lock)
#define OUTPUT_READABLE_YAWPITCHROLL

// uncomment "OUTPUT_READABLE_REALACCEL" if you want to see acceleration
// components with gravity removed. This acceleration reference frame is
// not compensated for orientation, so +X is always +X according to the
// sensor, just without the effects of gravity. If you want acceleration
// compensated for orientation, us OUTPUT_READABLE_WORLDACCEL instead.
#define OUTPUT_READABLE_REALACCEL

// uncomment "OUTPUT_READABLE_WORLDACCEL" if you want to see acceleration
// components with gravity removed and adjusted for the world frame of
// reference (yaw is relative to initial orientation, since no magnetometer
// is present in this case). Could be quite handy in some cases.
#define OUTPUT_READABLE_WORLDACCEL

// uncomment "OUTPUT_TEAPOT" if you want output that matches the
// format used for the InvenSense teapot demo
//#define OUTPUT_TEAPOT

#define LED_PIN 13 // (Arduino is 13, Teensy is 11, Teensy++ is 6)

bool blinkState = false;

// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer
const float pi = 3.1415;

// orientation/motion vars
Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector
//double balance[30];

// packet structure for InvenSense teapot demo
uint8_t teapotPacket[14] = { '$', 0x02, 0,0, 0,0, 0,0, 0,0, 0x00, 0x00, '\r', '\n' };


// ================================================================
// ===               INTERRUPT DETECTION ROUTINE                ===
// ================================================================

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}

//*********************************************************************************************8
void setup() 
{
  //************************************************************************************
// join I2C bus (I2Cdev library doesn't do this automatically)
    #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
        Wire.begin();
        TWBR = 24; // 400kHz I2C clock (200kHz if CPU is 8MHz)
    #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
        Fastwire::setup(400, true);
    #endif

    // initialize serial communication
    // (115200 chosen because it is required for Teapot Demo output, but it's
    // really up to you depending on your project)

    //==========================
    Serial.begin(9600);
    //###############################################################
    pinMode(echopin,INPUT); 
    pinMode(trigpin,OUTPUT);
    pinMode(led,OUTPUT);
    //###############################################################


    
    while (!Serial); // wait for Leonardo enumeration, others continue immediately

    // NOTE: 8MHz or slower host processors, like the Teensy @ 3.3v or Ardunio
    // Pro Mini running at 3.3v, cannot handle this baud rate reliably due to
    // the baud timing being too misaligned with processor ticks. You must use
    // 38400 or slower in these cases, or use some kind of external separate
    // crystal solution for the UART timer.

    // initialize device
    Serial.println(F("Initializing I2C devices..."));
    mpu.initialize();

    // verify connection
    Serial.println(F("Testing device connections..."));
    Serial.println(mpu.testConnection() ? F("MPU6050 connection successful") : F("MPU6050 connection failed"));

    // wait for ready
//    Serial.println(F("\nSend any character to begin DMP programming and demo: "));
//    while (Serial.available() && Serial.read()); // empty buffer
//    while (!Serial.available());                 // wait for data
//    while (Serial.available() && Serial.read()); // empty buffer again

    // load and configure the DMP
    Serial.println(F("Initializing DMP..."));
    devStatus = mpu.dmpInitialize();

    // supply your own gyro offsets here, scaled for min sensitivity
    mpu.setXGyroOffset(220);
    mpu.setYGyroOffset(76);
    mpu.setZGyroOffset(-85);
    mpu.setZAccelOffset(1788); // 1688 factory default for my test chip

    // make sure it worked (returns 0 if so)
    if (devStatus == 0) {
        // turn on the DMP, now that it's ready
        Serial.println(F("Enabling DMP..."));
        mpu.setDMPEnabled(true);

        // enable Arduino interrupt detection
        Serial.println(F("Enabling interrupt detection (Arduino external interrupt 0)..."));
        attachInterrupt(0, dmpDataReady, RISING);
        mpuIntStatus = mpu.getIntStatus();

        // set our DMP Ready flag so the main loop() function knows it's okay to use it
        Serial.println(F("DMP ready! Waiting for first interrupt..."));
        dmpReady = true;

        // get expected DMP packet size for later comparison
        packetSize = mpu.dmpGetFIFOPacketSize();
    } else {
        // ERROR!
        // 1 = initial memory load failed
        // 2 = DMP configuration updates failed
        // (if it's going to break, usually the code will be 1)
        Serial.print(F("DMP Initialization failed (code "));
        Serial.print(devStatus);
        Serial.println(F(")"));
    }

    // configure LED for output
    pinMode(LED_PIN, OUTPUT);
  //************************************************************************************
 

// HC-06 default serial speed is 9600
    BTserial.begin(9600); 
  
//    Serial.begin(9600);
//    Serial.println("Enter AT commands:");
// 
//    // HC-06 default serial speed is 9600
//    BTserial.begin(9600);  
}

//===================================================
//looping part

//



void loop()
{
 
//****For print out the velocity*****************************
//              Serial.print("-.-.-.-.-.-.-.-.\n");
//              digitalWrite(trigpin,LOW);
//              delayMicroseconds(2);
//              digitalWrite(trigpin,HIGH);
//              delayMicroseconds(10);
//              digitalWrite(trigpin,LOW);
//              int distance = pulseIn(echopin,HIGH);
//              distance = distance/58.0;
//              int velocity = ((distance*distance)-(49)); //since we are using m/s as the unit of the velocity, 0.01* 1000 = 1
//              Serial.print("{");
//              Serial.print("vel:");
//              Serial.print(velocity);  
//              Serial.print("}\n");
//                            
//              BTserial.print("{");
//              BTserial.print("vel:");
//              BTserial.print(velocity);
//              BTserial.print("}\n");
//                    if(distance<10)
//                    {digitalWrite(led,HIGH);}
//                    else{digitalWrite(led,LOW);}
//                    delay(10); 
//                if(distance<10)
//                {digitalWrite(led,HIGH);}
//                else{digitalWrite(led,LOW);}
//
//
//                Serial.print("-.-.-.-.-.-.-.-.\n");

            
              //************************************************************

 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // if programming failed, don't try to do anything
    if (!dmpReady) return;

    // wait for MPU interrupt or extra packet(s) available
    while (!mpuInterrupt && fifoCount < packetSize) {
        // other program behavior stuff here
        // .
        // .
        // .
        // if you are really paranoid you can frequently test in between other
        // stuff to see if mpuInterrupt is true, and if so, "break;" from the
        // while() loop to immediately process the MPU data
        // .
        // .
        // .
    }

    // reset interrupt flag and get INT_STATUS byte
    mpuInterrupt = false;
    mpuIntStatus = mpu.getIntStatus();

    // get current FIFO count
    fifoCount = mpu.getFIFOCount();

    // check for overflow (this should never happen unless our code is too inefficient)
    if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
        // reset so we can continue cleanly
        mpu.resetFIFO();
        //Serial.println(F("FIFO overflow!"));

    // otherwise, check for DMP data ready interrupt (this should happen frequently)
    } else if (mpuIntStatus & 0x02) {
        // wait for correct available data length, should be a VERY short wait
        while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

        // read a packet from FIFO
        mpu.getFIFOBytes(fifoBuffer, packetSize);
        
        // track FIFO count here in case there is > 1 packet available
        // (this lets us immediately read more without waiting for an interrupt)
        fifoCount -= packetSize;

        #ifdef OUTPUT_READABLE_QUATERNION
            // display quaternion values in easy matrix form: w x y z
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            Serial.print("quat\t");
            Serial.print(q.w);
            Serial.print("\t");
            Serial.print(q.x);
            Serial.print("\t");
            Serial.print(q.y);
            Serial.print("\t");
            Serial.println(q.z);
            //+++++++++++++++++++++++++++++++++
             BTserial.print("quat\t");
             BTserial.print(q.w);
             BTserial.print("\t");
             BTserial.print(q.x);
             BTserial.print("\t");
             BTserial.print(q.y);
             BTserial.print("\t");
             BTserial.println(q.z);
            //+++++++++++++++++++++++++++++++++
        #endif

        #ifdef OUTPUT_READABLE_EULER
            // display Euler angles in degrees
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            mpu.dmpGetEuler(euler, &q);
            Serial.print("euler\t");
            Serial.print(euler[0] * 180/M_PI);
            Serial.print("\t");
            Serial.print(euler[1] * 180/M_PI);
            Serial.print("\t");
            Serial.println(euler[2] * 180/M_PI);
            //+++++++++++++++++++++++++++++++++
             BTserial.print("euler\t");
             BTserial.print(euler[0] * 180/M_PI);
             BTserial.print("\t");
             BTserial.print(euler[1] * 180/M_PI);
             BTserial.print("\t");
             BTserial.println(euler[2] * 180/M_PI);
            //+++++++++++++++++++++++++++++++++
        #endif

        #ifdef OUTPUT_READABLE_YAWPITCHROLL
            // display Euler angles in radians
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            mpu.dmpGetGravity(&gravity, &q);
            mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);
            Serial.print("{rot\t");
            Serial.print(ypr[2] );
            Serial.print(",");
            Serial.print(ypr[1] );
            Serial.print(",");
            Serial.println(ypr[0] );
             //+++++++++++++++++++++++++++++++++
             if(count==6){
              newrot[2]=fabs((fabs(ypr[2])-fabs(lastrot[2]))/.51);
              newrot[1]=fabs((fabs(ypr[1])-fabs(lastrot[1]))/.51);
              newrot[0]=fabs(fabs((ypr[0])-fabs(lastrot[0]))/.51);
              lastrot[2]=ypr[2];
              lastrot[1]=ypr[1];
              lastrot[0]=ypr[0];
              
              Serial.print("{newrot\t");
              Serial.print(newrot[2]);
              Serial.print(",");
              Serial.print(newrot[1] );
              Serial.print(",");
              Serial.println(newrot[0]);
              BTserial.print("{newrot:");
              BTserial.print(newrot[2] );
              BTserial.print(",");
              BTserial.print(newrot[1] );
              BTserial.print(",");
              BTserial.print(newrot[0] );
              BTserial.print("}\n");
              count=0;
             }
             count++;
             BTserial.print(count);
             BTserial.print("{rot:");
             BTserial.print(ypr[2] );
             BTserial.print(",");
             BTserial.print(ypr[1] );
             BTserial.print(",");
             BTserial.print(ypr[0] );
             BTserial.print("}\n");
            //+++++++++++++++++++++++++++++++++
        #endif

        #ifdef OUTPUT_READABLE_REALACCEL
            // display real acceleration, adjusted to remove gravity
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            mpu.dmpGetAccel(&aa, fifoBuffer);
            mpu.dmpGetGravity(&gravity, &q);
            mpu.dmpGetLinearAccel(&aaReal, &aa, &gravity);
            //+++++++++++++++++++++++++++++++++
             BTserial.print("{acc:");
             BTserial.print(aaReal.x);
             BTserial.print(",");
             BTserial.print(aaReal.y);
             BTserial.print(",");
             BTserial.print(-aaReal.z);
             BTserial.print("}\n");
            //+++++++++++++++++++++++++++++++++
            Serial.print("{acc:");
            Serial.print(aaReal.x);
            Serial.print(",");
            Serial.print(-aaReal.y);
            Serial.print(",");
            Serial.print(-aaReal.z);
            Serial.print("}\n");

            
//            //****For print out the velocity*****************************
              digitalWrite(trigpin,LOW);
              delayMicroseconds(2);
              digitalWrite(trigpin,HIGH);
              delayMicroseconds(10);
              digitalWrite(trigpin,LOW);
              int distance = pulseIn(echopin,HIGH);
              distance = distance/58.0;
              int velocity = ((distance*distance)-(49)); //since we are using m/s as the unit of the velocity, 0.01* 1000 = 1
              Serial.print("{");
              Serial.print("vel:");
              Serial.print(velocity);  
              Serial.print("}\n");
                            
              BTserial.print("{");
              BTserial.print("vel:");
              BTserial.print(velocity);
              BTserial.print("}\n");
                    if(distance<10)
                    {digitalWrite(led,HIGH);}
                    else{digitalWrite(led,LOW);}
                    delay(10); 
                if(distance<10)
                {digitalWrite(led,HIGH);}
                else{digitalWrite(led,LOW);}
//              //************************************************************
            
        #endif

        #ifdef OUTPUT_READABLE_WORLDACCEL
            // display initial world-frame acceleration, adjusted to remove gravity
            // and rotated based on known orientation from quaternion
            mpu.dmpGetQuaternion(&q, fifoBuffer);
            mpu.dmpGetAccel(&aa, fifoBuffer);
            mpu.dmpGetGravity(&gravity, &q);
            mpu.dmpGetLinearAccel(&aaReal, &aa, &gravity);
            mpu.dmpGetLinearAccelInWorld(&aaWorld, &aaReal, &q);
            Serial.print("{wac:");
            Serial.print(aaWorld.x);
            Serial.print(",");
            Serial.print(aaWorld.y);
            Serial.print(",");
            Serial.print(-aaWorld.z);
            Serial.print("}\n");
            //+++++++++++++++++++++++++++++++++
             BTserial.print("{wac:");
             BTserial.print(aaWorld.x);
             BTserial.print(",");
             BTserial.print(aaWorld.y);
             BTserial.print(",");
             BTserial.print(-aaWorld.z);
             BTserial.print("}\n");
            //+++++++++++++++++++++++++++++++++
            
        #endif
    
        #ifdef OUTPUT_TEAPOT
            // display quaternion values in InvenSense Teapot demo format:
            teapotPacket[2] = fifoBuffer[0];
            teapotPacket[3] = fifoBuffer[1];
            teapotPacket[4] = fifoBuffer[4];
            teapotPacket[5] = fifoBuffer[5];
            teapotPacket[6] = fifoBuffer[8];
            teapotPacket[7] = fifoBuffer[9];
            teapotPacket[8] = fifoBuffer[12];
            teapotPacket[9] = fifoBuffer[13];
            Serial.write(teapotPacket, 14);

            //++++++++++++
             BTserial.println(teapotPacket, 14);
            //+++++++++++
            teapotPacket[11]++; // packetCount, loops at 0xFF on purpose
        #endif

        // blink LED to indicate activity
        blinkState = !blinkState;
        digitalWrite(LED_PIN, blinkState);
        //delay(5);
    }


 //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Keep reading from HC-06 and send to Arduino Serial Monitor
    if (BTserial.available())
    {  
        Serial.write(BTserial.read());
    }

    // Keep reading from Arduino Serial Monitor and send to HC-06
    if (Serial.available())
    {
        BTserial.write(Serial.read());
    }
 
}

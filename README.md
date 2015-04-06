IoController java class
-----------------------
This class provide very simple methods to controle the I/O-controller in Java on Windows, Linux and Mac OS X.
The Autonomous I/O-controller was published in 1988 and 1989 by the Elektuur. Currently named [Elektor](https://www.elektor.nl/).
The controller is connected with a serial RS232 interface to a host computer and can control 32 digital outputs, 32 digital inputs, 4 analog outputs and 8 analog inputs.
To control the controller on modern computers without an serial RS232 interface a USB to Serial converter can be used. This class was tested on a Mac and Windows computer with a converter using the FTDI/FT232RL chipset.

Next to the class a simple Tester class is provided. 
The IoController class uses the jSSC java serial port communication library for communication to the serial port.
More information can be found [here](https://code.google.com/p/java-simple-serial-connector/).

####Technical specification

All technical specifications of the controller can be found in the Elektuur of December 1988 and Januari 1989.

## Get it, Build it and Run it
```
git clone https://github.com/etenbrinke/iocontroller.git
cd iocontroller
mvn clean package
java -jar target/io-controller-1.0.0-SNAPSHOT.jar
```

## License
```
Copyright 2015 Ernst-Paul ten Brinke

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

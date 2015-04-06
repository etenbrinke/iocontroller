package etenbrinke.iocontroller;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etenbrinke on 05/12/15.
 */

public class IOControllerTester {

    /**
     delay.
     Input : time in millisecond
     */
    private static void delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms); //sleep
        }
        catch(InterruptedException ie){
            //clean up stateSwitch
            Thread.currentThread().interrupt();
        }
    }

    /**
     Tester class
     */
    public static void main(String[] args) throws SerialPortException, SerialPortTimeoutException {

        //Logger.getGlobal().setLevel(Level.INFO);
        Logger.getGlobal().setLevel(Level.OFF);

        IOController io = new IOControllerImpl("/dev/cu.usbserial-FTAJP1YY",144);
        io.openSerialPort();
        io.resetController();

        io.setAllDigitalOutputs(0);

        for (int loop = 0; loop <= 100; loop++) {

            System.out.println("------------------------------------------------");
            System.out.println("If Logical level on digital input is high then let the white LED burn");
            for (int l = 0; l <= 2 ; l++) {
                for (int k = 250; k <= 300; k=k+10) {
                    if (io.getLogicalLevelDigitalInput(0) == 1) {
                        io.setVoltageAnalogOutput(0,k);
                        System.out.printf("sent %.2f V to the LED%n",(float)k/100);
                    }
                }
                for (int k = 300; k >= 250; k=k-10) {
                    if (io.getLogicalLevelDigitalInput(0) == 1) {
                        io.setVoltageAnalogOutput(0,k);
                        System.out.printf("sent %.2f V to the LED%n",(float)k/100);
                    }
                }
            }
            io.setVoltageAnalogOutput(0,0);
            io.setAllDigitalOutputs(0);

            System.out.println("------------------------------------------------");
            System.out.println("Set Connection on block 0 to 1");
            io.setBlockConnection(0,1);
            System.out.println("Read Block connection on block 0 is "+io.getBlockConnection(0));
            System.out.println("Set Connection on block 0 to 0");
            io.setBlockConnection(0,0);
            System.out.println("Read Block connection on block 0 is "+io.getBlockConnection(0));

            System.out.println("------------------------------------------------");
            System.out.println("Write Logical level 1 on output 16");
            io.setLogicalLevelDigitalOutput(16,1);
            System.out.println("Read Logical level on digital output 16 is "+io.getLogicalLevelDigitalOutput(16));
            System.out.println("Write Logical level 0 on output 16");
            io.setLogicalLevelDigitalOutput(16,0);
            System.out.println("Read Logical level on digital output 16 is "+io.getLogicalLevelDigitalOutput(16));

            System.out.println("-----------------------------------------------");
            System.out.println("Write Byte 32 to output block 0");
            io.setByteDigitalOutputBlock(0,32);
            System.out.println("Read Byte on digital output block 0 is "+io.getByteDigitalOutputBlock(0));
            System.out.println("Write Byte 123 to output block 2");
            io.setByteDigitalOutputBlock(2,123);
            System.out.println("Read Byte on digital output block 2 is "+io.getByteDigitalOutputBlock(2));

            System.out.println("------------------------------------------------");
            System.out.println("Read Byte on digital input block 1 is "+io.getByteDigitalInputBlock(1));

            System.out.println("\n");	;
            System.out.println("Read logical level on digital input 14 is "+io.getLogicalLevelDigitalInput(14));

            System.out.println("------------------------------------------------");
            for (int i = 0; i <= 1000; i=i+100)
            {
                io.setVoltageAnalogOutput(1,i);
                System.out.printf("Write %.2f V to analog output 1. ",(float)i/100);
                System.out.println("Read voltage on analog output 1 is "+ io.getVoltageAnalogOutput(1)+ "V");
            }

            System.out.println("------------------------------------------------");
            System.out.println("Read voltage on analog input 1 is "+ io.getVoltageAnalogInput(1)+ "V");

            System.out.println("-------------Now have some LED fun--------------");
            for (int i = 0; i <= 10; i++)
            {	io.setAllDigitalOutputs(1);
                delay(10);
                io.setAllDigitalOutputs(0);
                delay(10);
            }
            for (int i = 0; i <= 23; i++)
            {
                io.setLogicalLevelDigitalOutput(i,1);
                io.setLogicalLevelDigitalOutput(i+8,1);
                io.setLogicalLevelDigitalOutput(i+16,1);
            }

            io.setAllDigitalOutputs(0);

            for (int i = 0; i <= 10; i++)
            {
                io.setByteDigitalOutputBlock(0,15);
                io.setByteDigitalOutputBlock(0,0);
                io.setByteDigitalOutputBlock(1,15);
                io.setByteDigitalOutputBlock(1,0);
                io.setByteDigitalOutputBlock(2,15);
                io.setByteDigitalOutputBlock(2,0);
                io.setByteDigitalOutputBlock(2,240);
                io.setByteDigitalOutputBlock(2,0);
                io.setByteDigitalOutputBlock(1,240);
                io.setByteDigitalOutputBlock(1,0);
                io.setByteDigitalOutputBlock(0,240);
                delay(10);
                io.setByteDigitalOutputBlock(0,0);
            }

            io.setAllDigitalOutputs(0);

            Random rand = new Random();
            for (int i = 0; i <= 20; i++)
            {
                io.setByteDigitalOutputBlock(0,rand.nextInt(255) + 1);
                io.setByteDigitalOutputBlock(1,rand.nextInt(255) + 1);
                io.setByteDigitalOutputBlock(2,rand.nextInt(255) + 1);
            }
            io.setAllDigitalOutputs(0);

        }
        io.closeSerialPort();
    }

}

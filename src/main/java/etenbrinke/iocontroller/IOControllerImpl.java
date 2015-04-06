package etenbrinke.iocontroller;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import java.util.logging.Logger;

/**
 * Created by etenbrinke on 05/12/15.
 */

public class IOControllerImpl implements IOController {

    private static final int WAIT_FOR_CONTROLLER_MS = 20;
    private static final int READ_TIMEOUT_MS = 30000;
    private static final String READY_STATUS = "16";
    private static final String PARAMETER_OUT_OF_RANGE = "Parameter(s) out for range";
    private String serialDevice;
    private int ioAddress;
    private static SerialPort serialPort;
    private static final Logger LOG = Logger.getGlobal();

    public IOControllerImpl() {
        serialDevice = "/dev/ttyS0";
        ioAddress = 144;
    }

    public IOControllerImpl(String serialDevice, int ioAddress) {
        this.serialDevice = serialDevice;
        this.ioAddress = ioAddress;
    }

    @Override
    public String getSerialDevice() {
        return serialDevice;
    }

    /**
     Set serial device name and open serial port
     Example device name : /dev/cu.usbserial-FTA (Unix) or COM1 (Windows)
     @param serialDevice serial device name
     */
    @Override
    public void setSerialDevice(String serialDevice) {
        this.serialDevice = serialDevice;
    }

    @Override
    public int getIoAddress() {
        return ioAddress;
    }

    /**
     Set device address to enable serial communication
     The device address+1 will be used to disable serial communication
     @param ioAddress device address 144,146,148,150
     */
    @Override
    public void setIOAddress(int ioAddress) {
        if (ioAddress == 144 || ioAddress == 146 || ioAddress == 148 || ioAddress == 150) {
            this.ioAddress = ioAddress;
            LOG.info("Device address set to "+ioAddress);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Open serial port
     */
    @Override
    public void openSerialPort() {
        serialPort = new SerialPort(serialDevice);
        try {
            serialPort.openPort(); //Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE); //Set parameters
            LOG.info("Serial port opened on "+serialDevice);
        }
        catch (SerialPortException e) {
            LOG.info(e.getMessage());
        }
    }

    /**
     Close serial port
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void closeSerialPort() throws SerialPortException {
        serialPort.closePort();
        LOG.info("Serial port closed");
    }

    /**
     Reset Controller
     Controller command R
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void resetController() throws SerialPortException {
        serialPort.writeInt((char) ioAddress);
        serialPort.writeByte((byte)0x18);
        serialPort.writeString("R");
        serialPort.writeByte((byte)0x0D);
        delay(WAIT_FOR_CONTROLLER_MS);
        LOG.info("Controller reset");
    }

    /**
     Set Local Mode
     Controller commands L,N
     @param localSwitch 1 to enable, 0 to disable
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setLocalMode(int localSwitch) throws SerialPortException {
        if (localSwitch == 0 || localSwitch == 1) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            if (localSwitch == 1) {
                serialPort.writeString("L");
            } else {
                serialPort.writeString("N");
            }
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Local mode set to "+localSwitch);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Set Echo Mode
     Controller commands X,Y
     @param echoSwitch 1 to enable, 0 to disable
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setEchoMode(int echoSwitch) throws SerialPortException {
        if (echoSwitch == 0 || echoSwitch == 1) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            if (echoSwitch == 1) {
                serialPort.writeString("X");
            } else {
                serialPort.writeString("Y");
            }
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Echo mode set to "+echoSwitch);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Set all digital outputs to high or low
     Controller commands C,D
     @param stateSwitch 1 set outputs to high, 0 to low
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setAllDigitalOutputs(int stateSwitch) throws SerialPortException {
        if (stateSwitch == 0 || stateSwitch == 1) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            if (stateSwitch == 1) {
                serialPort.writeString("D");
            }
            else {
                serialPort.writeString("C");
            }
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("All digital outputs set to "+stateSwitch);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Set Block Connection
     Controller commands G,H
     @param block number 0-3
     @param connectionSwitch 1 to make connection, 0 do not make connection
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setBlockConnection(int block, int connectionSwitch) throws SerialPortException {
        if ((block >= 0 && block <= 3) && (connectionSwitch == 0 || connectionSwitch == 1)) {
         	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            if (connectionSwitch == 1) {
                serialPort.writeString("G"+block);
            }
            else { serialPort.writeString("H"+block);
            }
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Block connection on block "+block+" set to "+connectionSwitch);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Get if block is connected
     Controller command g.
     @param block number 0-3
     @return 1 if connection is made, 0 if not, -1 parameter out of range
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public int getBlockConnection(int block) throws SerialPortException, SerialPortTimeoutException {
        LOG.info("Get block Connection on block "+block);
        if (block >= 0 && block <= 3) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("g"+block);
            serialPort.writeByte((byte)0x0D);
            int connected = Integer.parseInt(serialPort.readString(2,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Block connection on block "+block+" is set to "+connected);
            return connected;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Set logical level on digital output
     Controller command A
     @param digitalOutput number 0-31
     @param logicalLevel logical level 0 or 1
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setLogicalLevelDigitalOutput(int digitalOutput, int logicalLevel) throws SerialPortException {
        if ((digitalOutput >= 0 && digitalOutput <= 31) && (logicalLevel == 0 || logicalLevel == 1)) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("A"+digitalOutput+","+logicalLevel);
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Logical level "+logicalLevel+" set on digital output "+digitalOutput);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Get logical level on digital output
     Controller command a
     @param digitalOutput number 0-31
     @return logical level 0 or 1, -1 parameter out of range
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public int getLogicalLevelDigitalOutput(int digitalOutput) throws SerialPortException, SerialPortTimeoutException {
        if (digitalOutput >= 0 && digitalOutput <= 31) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("a"+ digitalOutput);
            serialPort.writeByte((byte)0x0D);
            int logicalLevel = Integer.parseInt(serialPort.readString(2,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Logical level on digital output "+digitalOutput+" is "+logicalLevel);
            return logicalLevel;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Set byte on digital output block
     Controller command B
     @param digitalOutputBlock number 0-3
     @param byteValue byte value 0-255
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setByteDigitalOutputBlock(int digitalOutputBlock, int byteValue) throws SerialPortException {
        if ((digitalOutputBlock >= 0 && digitalOutputBlock <= 3) && (byteValue >=0 && byteValue <= 255)) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("B"+digitalOutputBlock+","+byteValue);
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Byte value "+byteValue+" set on digital output block "+digitalOutputBlock);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Get byte on digital output block (8 outputs)
     Controller command b
     @param digitalOutputBlock number 0-3
     @return byte value 0-255, -1 parameter out of range
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public int getByteDigitalOutputBlock(int digitalOutputBlock) throws SerialPortException, SerialPortTimeoutException {
        if (digitalOutputBlock >= 0 && digitalOutputBlock <= 3) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("b"+digitalOutputBlock);
            serialPort.writeByte((byte)0x0D);
            int byteValue = Integer.parseInt(serialPort.readString(5,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Byte on digital block "+digitalOutputBlock+" is "+byteValue);
            return byteValue;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Get byte on digital input block (8 inputs)
     Controller command f
     @param digitalInputBlock number 0-3
     @return byte value 0-255
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public int getByteDigitalInputBlock(int digitalInputBlock) throws SerialPortException, SerialPortTimeoutException {
        if (digitalInputBlock >= 0 && digitalInputBlock <= 3) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("f"+ digitalInputBlock);
            serialPort.writeByte((byte)0x0D);
            int byteValue = Integer.parseInt(serialPort.readString(5,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Byte value on digital input block "+digitalInputBlock+" is "+byteValue);
            return byteValue;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Get logical level on digital input
     Controller command e
     @param digitalInput number 0-31
     @return logical level 0 or 1, -1 parameter out of range
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public int getLogicalLevelDigitalInput(int digitalInput) throws SerialPortException, SerialPortTimeoutException {
        if (digitalInput >= 0 && digitalInput <= 31) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("e"+ digitalInput);
            serialPort.writeByte((byte)0x0D);
            int logicalLevel = Integer.parseInt(serialPort.readString(2,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Logical level on digital input "+digitalInput+" is "+logicalLevel);
            return logicalLevel;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Set voltage on analog output
     Controller command U
     @param analogOutput number 0-3
     @param voltage in V 0-1023. 1023 equals 10.23 V
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public void setVoltageAnalogOutput(int analogOutput, int voltage) throws SerialPortException {
        if ((analogOutput >= 0 || analogOutput <= 3) && (voltage >= 0 && voltage <= 1023)) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("U"+analogOutput+","+voltage);
            serialPort.writeByte((byte)0x0D);
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Set "+String.format("%.2f",(double)voltage/100)+" V on analog output "+analogOutput);
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
        }
    }

    /**
     Get voltage on analog output
     Controller command u
     @param analogOutput number 0-3
     @return voltage in V 0-10.23, -1 parameter out of range
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public double getVoltageAnalogOutput(int analogOutput) throws SerialPortException, SerialPortTimeoutException {
        if (analogOutput >= 0 || analogOutput <= 3) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("u"+ analogOutput);
            serialPort.writeByte((byte)0x0D);
            double voltage = Double.parseDouble(serialPort.readString(6,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Voltage on analog output "+analogOutput+" is "+voltage+" V");
            return voltage;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return -1;
        }
    }

    /**
     Get voltage on analog input
     Controller command v
     @param analogInput number 0-3
     @return voltage in V 0-10.23
     @throws SerialPortException exception that might occur in Serial interface
     */
    @Override
    public double getVoltageAnalogInput(int analogInput) throws SerialPortException, SerialPortTimeoutException {
        if (analogInput >= 0 || analogInput <= 3) {
        	serialPort.writeInt((char) ioAddress);
            waitForControllerToBeReady();
            serialPort.writeString("v"+ analogInput);
            serialPort.writeByte((byte)0x0D);
            double voltage = Double.parseDouble(serialPort.readString(6,READ_TIMEOUT_MS).trim());
            delay(WAIT_FOR_CONTROLLER_MS);
            serialPort.writeInt((char)(ioAddress+1));
            LOG.info("Voltage on analog input "+analogInput+" is "+voltage+" V");
            return voltage;
        } else {
            LOG.info(PARAMETER_OUT_OF_RANGE);
            return 9999;
        }
    }

    /**
     delay.
     Input : time in millisecond
     */
    private static void delay(int ms) {
        try {
            LOG.info("waiting "+ms+" ms");
            Thread.currentThread();
            Thread.sleep(ms); //sleep
        }
        catch(InterruptedException ie){
            LOG.info(ie.getMessage());
            //clean up stateSwitch
            Thread.currentThread().interrupt();
        }
    }

    /**
     Check if controller is ready for a new command to receive
     When controller sent a 16H status-byte it is ready
     @throws SerialPortException exception that might occur in Serial interface
     */
    private void waitForControllerToBeReady() throws SerialPortException {
        delay(WAIT_FOR_CONTROLLER_MS);
        String status = "";
        while (!READY_STATUS.equals(status))
        {   serialPort.writeInt((char)0);
            byte[] buffer = serialPort.readBytes(1);
            status = Integer.toHexString(buffer[0]);
            LOG.info("Status received from controller : " +status+ "H");
        }
    }

    @Override
    public String toString() {
        return "IOController{"+
                "serialDevice='"+serialDevice+'\''+
                ", ioAddress="+ioAddress+
                '}';
    }
}
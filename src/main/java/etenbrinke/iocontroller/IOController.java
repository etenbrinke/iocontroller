package etenbrinke.iocontroller;

import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 * Created by etenbrinke on 05/12/15.
 */

public interface IOController {
    String getSerialDevice();

    void setSerialDevice(String serialDevice);

    int getIoAddress();

    void setIOAddress(int ioAddress);

    void openSerialPort();

    void closeSerialPort() throws SerialPortException;

    void resetController() throws SerialPortException;

    void setLocalMode(int localSwitch) throws SerialPortException;

    void setEchoMode(int echoSwitch) throws SerialPortException;

    void setAllDigitalOutputs(int stateSwitch) throws SerialPortException;

    void setBlockConnection(int block, int connectionSwitch) throws SerialPortException;

    int getBlockConnection(int block) throws SerialPortException, SerialPortTimeoutException;

    void setLogicalLevelDigitalOutput(int digitalOutput, int logicalLevel) throws SerialPortException;

    int getLogicalLevelDigitalOutput(int digitalOutput) throws SerialPortException, SerialPortTimeoutException;

    void setByteDigitalOutputBlock(int digitalOutputBlock, int byteValue) throws SerialPortException;

    int getByteDigitalOutputBlock(int digitalOutputBlock) throws SerialPortException, SerialPortTimeoutException;

    int getByteDigitalInputBlock(int digitalInputBlock) throws SerialPortException, SerialPortTimeoutException;

    int getLogicalLevelDigitalInput(int digitalInput) throws SerialPortException, SerialPortTimeoutException;

    void setVoltageAnalogOutput(int analogOutput, int voltage) throws SerialPortException;

    double getVoltageAnalogOutput(int analogOutput) throws SerialPortException, SerialPortTimeoutException;

    double getVoltageAnalogInput(int analogInput) throws SerialPortException, SerialPortTimeoutException;
}

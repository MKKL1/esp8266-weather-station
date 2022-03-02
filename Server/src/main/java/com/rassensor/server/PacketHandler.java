package com.rassensor.server;

import com.rassensor.logger.LoggerManager;
import com.rassensor.client.SensorClient;
import com.rassensor.client.SensorData;

import java.io.DataInputStream;
import java.io.IOException;

public class PacketHandler {

    SensorClient sensorClient;
    DataInputStream dataInputStream;

    public PacketHandler(SensorClient sensorClient, DataInputStream dataInputStream) {
        this.sensorClient = sensorClient;
        this.dataInputStream = dataInputStream;
    }

    public void handlePacket() throws IOException {
        byte protocolnumber = dataInputStream.readByte();
        int messagelength = dataInputStream.readByte() & 0xFF;

        switch (protocolnumber) {
            case 0x00 -> {
                readHandShake();
            }
            case 0x01 -> {
                readSensorData();
            }
        }
    }

    private void readHandShake() throws IOException {
        int clientid = dataInputStream.readByte() & 0xFF;
        LoggerManager.LOGGER.info("Client id: " + clientid);
        sensorClient = new SensorClient();
        sensorClient.createOrLoadClient(clientid);

    }

    private void readSensorData() throws IOException {
        SensorData sensorData = new SensorData();
        sensorData.temperature = dataInputStream.readFloat();
        sensorData.pressure = dataInputStream.readFloat();
        sensorData.humidity = dataInputStream.readFloat();

        sensorClient.logToFile(sensorData);

        //TODO for debugging
        System.out.println(sensorData.toString());
    }
}

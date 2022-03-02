package com.rassensor.client;

import java.io.File;
import java.io.IOException;

public class SensorClient{
    private int id = -1;
    //Path to sensor's data folder
    private String path;
    private final ClientIO clientIO;

    public SensorClient() {
        clientIO = new ClientIO();
    }

    public void createOrLoadClient(int clientid) throws IOException {
        id = clientid;
        clientIO.createOrLoadClient(this, clientid);
    }

    public void logToFile(SensorData sensorData) throws IOException {
        clientIO.logToFile(sensorData, path);
    }

    public int getId() {
        return id;
    }

    public String getDirectory() {
        return path;
    }

    public void setDirectory(String path) {this.path = path;}
    public void setDirectory(File path) {this.path = path.getAbsolutePath();}
}

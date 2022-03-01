package com.rassensor;

import com.rassensor.config.ConfigGlobal;
import com.rassensor.config.ConfigManager;
import com.rassensor.server.SensorServer;

import java.io.IOException;

public class Main {


    public static void main(String[] args) {

        try {
            //Loads configs from config.properties to ConfigGlobal
            new ConfigManager().load();

            SensorServer sensorServer = new SensorServer(ConfigGlobal.port);
            sensorServer.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

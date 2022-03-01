package com.rassensor.server;

import com.rassensor.logger.LoggerManager;
import com.rassensor.client.SensorClient;

import java.io.*;
import java.net.Socket;

public class SensorClientThread extends Thread{

    SensorClient sensorClient = null;

    protected Socket socket;
    public SensorClientThread(Socket clientsocket) {
        this.socket = clientsocket;
    }

    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            //Client disconnected or lost connection

            //e.printStackTrace();
            String tmpid = "unknown";
            if (sensorClient != null)
                tmpid = String.valueOf(sensorClient.getId());
            //TODO sensorClient is always null
            LoggerManager.LOGGER.info("Client(" + tmpid + ") disconnected. Reason: " + e.getMessage());

        }
    }

    private void handleClient() throws IOException {
        LoggerManager.LOGGER.info("Client connected with ip: " + socket.getInetAddress().toString());
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            PacketHandler packetHandler = new PacketHandler(sensorClient,dataInputStream);

            while (true) {
                //handlePacket waits for input
                packetHandler.handlePacket();
                //System.out.println(dataInputStream.readByte());
            }
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }

            socket.close();
        }


    }
}

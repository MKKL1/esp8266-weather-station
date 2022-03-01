package com.rassensor.server;

import com.rassensor.bgworker.BackgroundWorker;
import com.rassensor.bgworker.CTaskListManager;
import com.rassensor.logger.LoggerManager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class  SensorServer {
    int port;

    public static BackgroundWorker backgroundWorker;

    public SensorServer(int port) {
        this.port = port;
    }

    public void open() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        LoggerManager.LOGGER.info("Server started on port " + port);
        backgroundWorker = new BackgroundWorker();

        backgroundWorker.addTaskToQueue(CTaskListManager.executeFromTextFileRunnable());

        Socket socket = null;
        while(true) {
            try {
                socket = serverSocket.accept();
                SensorClientThread sensorClientThread = new SensorClientThread(socket);
                sensorClientThread.start();
            } catch (IOException e) {
                if (socket != null) {
                    InetAddress address = socket.getInetAddress();
                    if (address == null)
                        LoggerManager.LOGGER.warning("Error occurred while trying to accept client with unknown ip");
                    else
                        LoggerManager.LOGGER.warning("Error occurred while trying to accept client with ip: " + address.toString());
                }
                e.printStackTrace();
            }
        }
    }
}

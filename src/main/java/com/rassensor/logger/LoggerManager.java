package com.rassensor.logger;

import com.rassensor.server.SensorServer;

import java.util.logging.Logger;

public class LoggerManager {
    public static Logger LOGGER = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(SensorServer.class.getName());
    }
}

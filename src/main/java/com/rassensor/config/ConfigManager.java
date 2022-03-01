package com.rassensor.config;

import com.rassensor.logger.LoggerManager;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    Properties properties;

    public ConfigManager(){
        properties = new Properties();
    }

    /***
     * Loads properties defined in {@link ConfigGlobal}, rest should be handled by {@link #getProperty(String)}
     * @throws IOException If config file was not found in resources
     */
    public void load() throws IOException {
        InputStream inputStream = findConfigFile();
        properties.load(inputStream);

        ConfigGlobal.datapath = properties.getProperty("sensordatapath");
        ConfigGlobal.tasklistpath = properties.getProperty("tasklistpath");
        ConfigGlobal.port = Short.parseShort(properties.getProperty("port"));
        inputStream.close();
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * {@link Properties#getProperty(String)}
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Searches for config file in deafult path. If not found, creates file and copies content from config.properties
     * in resources
     * @return {@link InputStream} of config.properties
     * @throws IOException If config.properties was not found in resources
     */
    private InputStream findConfigFile() throws IOException {
        File configFile = new File(".", "config.properties");
        if (!configFile.exists()) {
            System.out.println("Config file wasn't found. Creating new config file");
            InputStream configResource = getClass().getClassLoader().getResourceAsStream("config.properties");
            if (configResource == null) throw new IOException("config.properties wasn't found (it may have been deleted from jar file)");
            FileOutputStream fileOutputStream = new FileOutputStream(configFile);
            fileOutputStream.write(configResource.readAllBytes());
            fileOutputStream.close();
            configResource = getClass().getClassLoader().getResourceAsStream("config.properties");
            return configResource;
        }
        return new FileInputStream(configFile);
    }
}

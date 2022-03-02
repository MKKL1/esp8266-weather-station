package com.rassensor.client;

public class SensorData {
    public float temperature;
    public float pressure;
    public float humidity;

    //TODO bad performance
    @Override
    public String toString() {
        return "temperature=" + temperature +
                ", pressure=" + pressure +
                ", humidity=" + humidity;
    }
}

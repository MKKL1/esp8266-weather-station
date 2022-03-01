package com.rassensor.client;

import com.rassensor.config.ConfigGlobal;
import com.rassensor.bgworker.CompressListFile;
import com.rassensor.bgworker.CTaskListManager;
import com.rassensor.server.SensorServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;

import static com.rassensor.server.SensorServer.backgroundWorker;

public class ClientIO {

    public void createOrLoadClient(SensorClient sensorClient, int id) throws IOException {
        File directory = new File(Paths.get(ConfigGlobal.datapath, "sensor-" + id).normalize().toString());
        //File infofile = new File(directory, "info.json");
        sensorClient.setDirectory(directory);
        directory.mkdirs();
    }

    private LocalDateTime currentdate;
    private File logfile;
    private FileWriter fw = null;
    private int lastday = 0;
    private final StringBuilder stringBuilder = new StringBuilder(29 + System.lineSeparator().length());

    public void logToFile(SensorData sensorData, String path) throws IOException {
        currentdate = LocalDateTime.now();
        int currentday = currentdate.getDayOfMonth();

        //Next day or when program starts
        if (currentday != lastday) {
            if (fw != null) { //Next day
                fw.close();
                //TODO find better way to handle recurring tasks
                backgroundWorker.addTaskToQueue(CTaskListManager.executeFromTextFileRunnable());
            }

            logfile = new File(String.valueOf(Paths.get(path, currentdate.getYear() + "-" + currentdate.getMonthValue())), currentday + ".csv");
            if (!logfile.exists()) {
                logfile.getParentFile().mkdirs();
                logfile.createNewFile();
            }

            fw = new FileWriter(logfile, true);
            backgroundWorker.addTaskToQueue(CTaskListManager.addCompressTask(new CompressListFile(
                            currentdate.getYear(),
                            currentdate.getMonthValue(),
                            currentdate.getDayOfMonth(),
                            logfile.getAbsolutePath(),
                            new File(logfile.getParent(), currentday + ".zip").getAbsolutePath())));

        }

        /*fw.append(String.format(Locale.ROOT, "%d:%d:%d,%.2f,%.2f,%.2f\n",
                currentdate.getHour(),
                currentdate.getMinute(),
                currentdate.getSecond(),
                sensorData.temperature,
                sensorData.pressure,
                sensorData.humidity));*/

        stringBuilder.append(currentdate.getHour()).append(":");
        stringBuilder.append(currentdate.getMinute()).append(":");
        stringBuilder.append(currentdate.getSecond()).append(",");
        stringBuilder.append(cutfloat(sensorData.temperature)).append(",");
        stringBuilder.append(cutfloat(sensorData.pressure)).append(",");
        stringBuilder.append(cutfloat(sensorData.humidity)).append(System.lineSeparator());
        fw.write(stringBuilder.toString());
        stringBuilder.setLength(0);

        fw.flush();
        lastday = currentday;
    }

    private float cutfloat(float value) {
        return (float)((int)( value *100f ))/100f;
    }
}

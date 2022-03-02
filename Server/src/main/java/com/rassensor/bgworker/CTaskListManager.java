package com.rassensor.bgworker;

import com.rassensor.config.ConfigGlobal;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//TODO Fix this mess
public class CTaskListManager {

    /**
     * Reads and compresses all files that are not to be written to again (older than 1 day)
     * @throws IOException TODO
     */
    public static void executeFromTextFile() throws IOException {
        File file = new File(ConfigGlobal.tasklistpath);
        if (!file.exists())
            if(!file.createNewFile()) {
                throw new IOException("Couldn't create file (" + file.getAbsolutePath() + ")");
            }
        int toProcess = 0;
        List<CompressListFile> toWrite = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        try {
            String line;
            LocalDate localDate = LocalDate.now();
            while ((line = bufferedReader.readLine()) != null && !line.equals("")) {
                CompressListFile item = new CompressListFile(line);
                if (item.year != localDate.getYear() ||
                        item.month != localDate.getMonthValue() ||
                        item.day != localDate.getDayOfMonth()) {
                    new FileCompressorTask(new File(item.compresspath), new File(item.outputpath)).run();
                    toProcess++;
                } else {
                    toWrite.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferedReader.close();

        if (toProcess==0) return;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (int j = 0; j < toWrite.size()-1; j++) {
            CompressListFile item = toWrite.get(j);
            bufferedWriter.write(item.toString() + System.lineSeparator());
        }
        if (toWrite.size() > 0) {
            CompressListFile item = toWrite.get(toWrite.size() - 1);
            bufferedWriter.write(item.toString());
        }

        bufferedWriter.close();
    }

    public static Runnable executeFromTextFileRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    CTaskListManager.executeFromTextFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Adds sensor data log file to task list file to later be compressed with {@link #executeFromTextFile()}
     * Should be used with {@link BackgroundWorker#addTaskToQueue(Runnable)}
     * @param compressListFile File to be compressed
     * @return Runnable task for {@link BackgroundWorker#addTaskToQueue(Runnable)}
     */

    public static Runnable addCompressTask(CompressListFile compressListFile) {
        File file = new File(ConfigGlobal.tasklistpath);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(compressListFile.toString() + System.lineSeparator());
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

    }

}


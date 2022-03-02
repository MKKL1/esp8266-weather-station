package com.rassensor.bgworker;

import com.rassensor.logger.LoggerManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompressorTask implements Runnable{

    private static Logger LOGGER = null;

    public File filetocompress;
    public File finalproduct;
    private boolean delete;

    /**
     * Adds file to zip archive so it could be decompressed with simple tools
     * @param filetocompress File to be compressed
     * @param finalproduct Compressed output file
     * @param delete Whether {{@link #filetocompress}} should be deleted
     */
    public FileCompressorTask(File filetocompress, File finalproduct, boolean delete) {
        this.filetocompress = filetocompress;
        this.finalproduct = finalproduct;
        this.delete = delete;
        LOGGER = LoggerManager.LOGGER;
    }

    public FileCompressorTask(File filetocompress, File finalproduct) {
        new FileCompressorTask(filetocompress, finalproduct, true);
    }



    public void run() {
        ZipOutputStream zipOutputStream = null;

        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(finalproduct));

            ZipEntry entry = new ZipEntry(filetocompress.getName());
            entry.setMethod(ZipEntry.DEFLATED);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(Files.readAllBytes(Paths.get(filetocompress.getAbsolutePath())));

            LOGGER.info("Compressed " + filetocompress.getName());

            if (!filetocompress.delete()) LOGGER.warning("Failed to delete file " + filetocompress.getName());
        } catch (FileNotFoundException e) {
            LOGGER.warning("File was not found (" + filetocompress.getAbsolutePath() + ")");
        } catch (IOException e) {
            LOGGER.warning("Error ocured while compressing " + filetocompress.getName());
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

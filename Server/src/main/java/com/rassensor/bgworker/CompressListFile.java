package com.rassensor.bgworker;

public class CompressListFile {
    public int year;
    public int month;
    public int day;
    public String compresspath;
    public String outputpath;
    public boolean write = true;

    public CompressListFile(String line) {
        String[] items = line.split(",");
        year = Integer.parseInt(items[0]);
        month = Integer.parseInt(items[1]);
        day = Integer.parseInt(items[2]);
        compresspath = items[3];
        outputpath = items[4];
    }

    public CompressListFile(int year, int month, int day, String compresspath, String outputpath) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.compresspath = compresspath;
        this.outputpath = outputpath;
    }

    @Override
    public String toString() {
        return year + "," + month + "," + day + "," + compresspath + "," + outputpath;
    }
}

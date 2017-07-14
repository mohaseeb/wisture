package com.nebula.haseeb.winiff.utils;

import java.io.Serializable;

/**
 * Created by haseeb on 4/4/16.
 */
public class MeasurementsParameters implements Serializable {
    private String measurementsFileNamePrefix = Constants.DEFAULT_MEASUREMENTS_FILE_RPEFIX;
    private int samplesPerSecond = Constants.DEFAULT_WIFI_RSS_SAMPLES_SECOND;
    private int graphWidthSeconds = Constants.DEFAULT_VISIBLE_WINDOW_WIDTH_SECONDS;

    public String getMeasurementsFileNamePrefix() {
        return measurementsFileNamePrefix;
    }

    public void setMeasurementsFileNamePrefix(String measurementsFileNamePrefix) {
        this.measurementsFileNamePrefix = measurementsFileNamePrefix;
    }

    public int getSamplesPerSecond() {
        return samplesPerSecond;
    }

    public void setSamplesPerSecond(int samplesPerSecond) {
        this.samplesPerSecond = samplesPerSecond;
    }

    public int getGraphWidthSeconds() {
        return graphWidthSeconds;
    }

    public void setGraphWidthSeconds(int graphWidthSeconds) {
        this.graphWidthSeconds = graphWidthSeconds;
    }
}

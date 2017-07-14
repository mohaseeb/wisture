package com.nebula.haseeb.winiff.measurements;

/**
 * Created by haseeb on 3/24/16.
 */
public class WifiRSSMeasurement implements ISeriesMeasurement {

    private long stepValue;
    private float measurementValue;

    /***
     * Wi-Fi Radio signal strength measurement
     *
     * @param stepValue        millisecond value; the first measurement will have a millisecond value of 0
     * @param measurementValue the RSS in mdb
     */
    public WifiRSSMeasurement(long stepValue, float measurementValue) {
        this.stepValue = stepValue;
        this.measurementValue = measurementValue;
    }

    @Override
    public long getStepValue() {
        return stepValue;
    }

    @Override
    public float getMeasurementValue() {
        return measurementValue;
    }

    @Override
    public float[] getMeasurements() {
        return new float[0];
    }
}

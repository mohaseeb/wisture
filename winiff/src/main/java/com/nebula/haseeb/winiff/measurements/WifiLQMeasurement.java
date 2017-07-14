package com.nebula.haseeb.winiff.measurements;

/**
 * Created by haseeb on 3/31/16.
 */
public class WifiLQMeasurement implements ISeriesMeasurement {

    private long stepValue;
    private float measurementValue;

    /***
     * Wi-Fi Link quality measurement
     *
     * @param stepValue        millisecond value; the first measurement will have a millisecond value of 0
     * @param measurementValue the LQ value
     */
    public WifiLQMeasurement(long stepValue, float measurementValue) {
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

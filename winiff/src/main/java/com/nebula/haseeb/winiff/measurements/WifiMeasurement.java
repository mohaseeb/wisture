package com.nebula.haseeb.winiff.measurements;

/**
 * Created by haseeb on 3/31/16.
 */
public class WifiMeasurement implements ISeriesMeasurement {

    private long stepValue;
    private float[] measurements;

    public WifiMeasurement(long stepValue, float[] measurements) {
        this.stepValue = stepValue;
        this.measurements = measurements;
    }

    @Override
    public long getStepValue() {
        return stepValue;
    }

    @Override
    public float getMeasurementValue() {
        return 0;
    }

    @Override
    public float[] getMeasurements() {
        return measurements;
    }

    @Override
    public String toString() {
        String measurementsString = String.valueOf(measurements[0]);
        for (int i = 1; i < measurements.length; i++) {
            measurementsString += "\t" + measurements[i];
        }
        return stepValue + "\t" + measurementsString;
    }
}

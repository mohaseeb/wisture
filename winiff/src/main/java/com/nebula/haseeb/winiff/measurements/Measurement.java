package com.nebula.haseeb.winiff.measurements;

import java.io.Serializable;

/**
 * Created by haseeb on 3/24/16.
 */
public interface Measurement extends Serializable {
    public float getMeasurementValue();

    public float[] getMeasurements();
}
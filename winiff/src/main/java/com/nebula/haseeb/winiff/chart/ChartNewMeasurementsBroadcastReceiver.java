package com.nebula.haseeb.winiff.chart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nebula.haseeb.winiff.measurements.ISeriesMeasurement;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;

import java.util.LinkedList;

/**
 * Created by haseeb on 4/4/16.
 */
public class ChartNewMeasurementsBroadcastReceiver extends BroadcastReceiver {

    private ChartManager chartManager;

    private ChartNewMeasurementsBroadcastReceiver(Context context, MeasurementsParameters measurementsParameters) {
        chartManager = new ChartManager(context, measurementsParameters);
    }

    public static ChartNewMeasurementsBroadcastReceiver getInstance(Context context, MeasurementsParameters measurementsParameters) {
        return new ChartNewMeasurementsBroadcastReceiver(context, measurementsParameters);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        chartManager.updateChart((LinkedList<ISeriesMeasurement>) intent.getSerializableExtra(Constants.EXTENDED_DATA_ALL_WIFI_MEASUREMENTS));
    }
}

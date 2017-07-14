package com.nebula.haseeb.winiff.persistence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nebula.haseeb.winiff.measurements.ISeriesMeasurement;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;

import java.util.LinkedList;

/**
 * Created by haseeb on 4/3/16.
 */
public class PersistenceNewMeasurementsBroadcastReceiver extends BroadcastReceiver {

    private MeasurementsPersistenceManager persistenceManager;

    private PersistenceNewMeasurementsBroadcastReceiver(Context context, MeasurementsParameters measurementsParameter) {
        persistenceManager = new MeasurementsPersistenceManager(context, measurementsParameter);
    }

    /**
     * returns the only @PersistenceNewMeasurementsBroadcastReceiver instance
     *
     * @param context
     * @return
     */
    public static PersistenceNewMeasurementsBroadcastReceiver getInstance(Context context, MeasurementsParameters measurementsParameter) {
        return new PersistenceNewMeasurementsBroadcastReceiver(context, measurementsParameter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.NEW_MEASUREMENTS_BROADCAST_ACTION:
                persistenceManager.saveMeasurements((LinkedList<ISeriesMeasurement>) intent.getSerializableExtra(Constants.EXTENDED_DATA_ALL_WIFI_MEASUREMENTS));
                break;
            case Constants.MEASUREMENT_END_BROADCAST_ACTION:
                persistenceManager.closeCurrentMeasurementFile();
                break;
            default:
                Log.e(this.getClass().getName(), "Unexpected broadcast");
        }
    }
}

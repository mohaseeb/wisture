package com.nebula.haseeb.winiff.measurements;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.nebula.haseeb.winiff.MeasurementsActivity;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;
import com.nebula.haseeb.winiff.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * .
 * Created by haseeb on 3/23/16.
 */
public class WifiPROCMeasurementsService extends IntentService {

    private MeasurementsParameters measurementsParameters;

    public WifiPROCMeasurementsService() {
        super(WifiPROCMeasurementsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        measurementsParameters = (MeasurementsParameters) intent.getSerializableExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID);
        try {
            Process shell = Runtime.getRuntime().exec("sh");
            DataOutputStream shOutputStream = new DataOutputStream(shell.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));

            LinkedList<ISeriesMeasurement> wifiMeasurementsBuffer = new LinkedList<>();

            shOutputStream.writeBytes("while :; do cat /proc/net/wireless; done;\n");
            shOutputStream.flush();
//                Inter-| sta-|   Quality        |   Discarded packets               | Missed | WE
//                face | tus | link level noise |  nwid  crypt   frag  retry   misc | beacon | 22
//                p2p0: 0000    0     0     0        0      0      0      0      0        0
//                wlan0: 0000   49.  -61.  -256        0      0      0      7    751        0

            String line;
            int rssi;
            int linkQuality;
            while (!MeasurementsActivity.measurementStopped) {
                if (!MeasurementsActivity.measurementOngoing) {
                    Utils.sleep(100);
                    continue;
                }
                line = reader.readLine();
                if (line.contains("wlan0")) {
                    String[] splitLine = line.split("\\s+");
                    rssi = Integer.valueOf(splitLine[4].replace(".", ""));
//                    linkQuality = Integer.valueOf(splitLine[3].replace(".", ""));
//                    wifiMeasurementsBuffer.add(new WifiMeasurement(System.nanoTime(), new float[]{rssi, linkQuality}));
                    wifiMeasurementsBuffer.add(new WifiMeasurement(System.nanoTime(), new float[]{rssi}));
                    if (wifiMeasurementsBuffer.size() > 150) {
                        reportMeasurements(wifiMeasurementsBuffer);
                        wifiMeasurementsBuffer = new LinkedList<>();
                    }
                    Utils.sleep(3);
                }
            }

            reportMeasurementEnd();
            shOutputStream.writeBytes("exit\n");
            shOutputStream.flush();
            shell.waitFor();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void reportMeasurementEnd() {
        Intent measurementsEndIntent = new Intent(Constants.MEASUREMENT_END_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(measurementsEndIntent);
    }

    private void reportMeasurements(LinkedList<ISeriesMeasurement> wifiMeasurements) {
           /*
     * Creates a new Intent containing a Uri object
     * NEW_MEASUREMENTS_BROADCAST_ACTION is a custom Intent action
     */
        Intent newMeasurementsReportIntent =
                new Intent(Constants.NEW_MEASUREMENTS_BROADCAST_ACTION)
                        .putExtra(Constants.EXTENDED_DATA_ALL_WIFI_MEASUREMENTS, wifiMeasurements);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(newMeasurementsReportIntent);
    }
}


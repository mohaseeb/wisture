package com.nebula.haseeb.winiff.measurements;

import android.app.IntentService;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;

import com.nebula.haseeb.winiff.MeasurementsActivity;
import com.nebula.haseeb.winiff.utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .
 * Created by haseeb on 3/23/16.
 */
public class WifiService extends IntentService {

    private final static String RSSI_PATTERN = "wlan0.*(-[0-9]+)\\..*";
    private static long NANOS_IN_SECOND = 1000000000;
    private int BUFFER_SIZE = 2000;
    private static Pattern pattern = Pattern.compile(RSSI_PATTERN);
    private RandomAccessFile wirelessFile;

    public WifiService() {
        super(WifiService.class.getName());
        try {
            wirelessFile = new RandomAccessFile("/proc/net/wireless", "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LinkedList<ISeriesMeasurement> wifiMeasurementsBuffer = new LinkedList<>();

        long windowSpacingNanos = NANOS_IN_SECOND / 4;

//                Inter-| sta-|   Quality        |   Discarded packets               | Missed | WE
//                face | tus | link level noise |  nwid  crypt   frag  retry   misc | beacon | 22
//                p2p0: 0000    0     0     0        0      0      0      0      0        0
//                wlan0: 0000   49.  -61.  -256        0      0      0      7    751        0

//        long start = System.nanoTime();
//        long count = 0;
        long previousWindowStart = System.nanoTime();
        // while the app is still running
        while (!MeasurementsActivity.measurementStopped) {
            if (!MeasurementsActivity.measurementOngoing) {
                sleep(100);
                previousWindowStart = System.nanoTime();
                continue;
            }

            // take a measurement
            try {
                String line = wirelessFile.readLine();
                if (line != null) {
                    Float rssi = parseRssi(line);
                    if (rssi != null) {
                        System.out.println(line);
                        System.out.println(rssi);
                        wifiMeasurementsBuffer.add(
                                new WifiMeasurement(
                                        System.nanoTime(),
                                        new float[]{rssi}
                                )
                        );
                        wirelessFile.seek(0); // Go to start
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

//            count++;
            // Todo check for index out of bound
            // See if we have a complete window to send
            if (System.nanoTime() - previousWindowStart > windowSpacingNanos) {
                reportMeasurements(wifiMeasurementsBuffer);
                wifiMeasurementsBuffer = new LinkedList<>();
                previousWindowStart = System.nanoTime();
            }

            // sleep a bit
            sleep(1);
        }

//        System.out.println(" sampling avg " + (count * 1000000000) / (System.nanoTime() - start));
        // report last measurement
        reportMeasurementEnd();
    }

    private static Float parseRssi(String str){
        if (str.contains("wlan")) {
            String[] split = str.split("\\s+");
            String rssiStr = split[4];
            if (rssiStr.contains("."))
                return Float.valueOf(rssiStr);
        }
        return null;
    }


    public static void main(String[] args){
        String str = "";
        Float rssi = WifiService.parseRssi(str);
        System.out.println(rssi);
        if (rssi != null) throw new AssertionError();

        str = "wlan0: 0000   49.  -61.  -256        0      0      0      7    751        0";
        rssi = WifiService.parseRssi(str);
        System.out.println(rssi);
        if (rssi != -61.0) throw new AssertionError();

        str = "wlan0: 0000   49.  61.  -256        0      0      0      7    751        0";
        rssi = WifiService.parseRssi(str);
        System.out.println(rssi);
        if (rssi != 61.0) throw new AssertionError();

        str = "wlan0: 0000   49.  61  -256        0      0      0      7    751        0";
        rssi = WifiService.parseRssi(str);
        System.out.println(rssi);
        if (rssi != null) throw new AssertionError();

        str = "wwan0: 0000   49.  61.  -256        0      0      0      7    751        0";
        rssi = WifiService.parseRssi(str);
        System.out.println(rssi);
        if (rssi != null) throw new AssertionError();
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


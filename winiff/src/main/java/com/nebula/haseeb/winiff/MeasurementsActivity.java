package com.nebula.haseeb.winiff;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nebula.haseeb.winiff.chart.ChartNewMeasurementsBroadcastReceiver;
import com.nebula.haseeb.winiff.measurements.WifiService;
import com.nebula.haseeb.winiff.persistence.PersistenceNewMeasurementsBroadcastReceiver;
import com.nebula.haseeb.winiff.traffic.TrafficInductionService;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;
import com.nebula.haseeb.winiff.utils.Utils;

public class MeasurementsActivity extends AppCompatActivity {

    public static boolean measurementOngoing = false;
    public static boolean measurementStopped = true;
    PersistenceNewMeasurementsBroadcastReceiver persistenceNewMeasurementsBroadcastReceiver;
    ChartNewMeasurementsBroadcastReceiver chartNewMeasurementsBroadcastReceiver;
    Button stopButton;
    private MeasurementsParameters measurementsParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        initActivityView();
        measurementsParameters = (MeasurementsParameters) getIntent().getSerializableExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID);
        startMeasurements();
    }

    @Override
    protected void onStop() {
        super.onStop();
        endMeasurements();
    }

    private void initActivityView() {
        setContentView(R.layout.measurements_main);
        Button fab = (Button) findViewById(R.id.pause);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!measurementOngoing) {
                    measurementOngoing = true;
                    Button parent = (Button) findViewById(R.id.pause);
                    parent.setText(R.string.button_text_pause);
                } else {
                    measurementOngoing = false;
                    Button parent = (Button) findViewById(R.id.pause);
                    parent.setText(R.string.button_text_resume);
                }
            }
        });

        stopButton = (Button) findViewById(R.id.end);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endMeasurements();
                Toast.makeText(getApplicationContext(), "Go to start page to start a new measurement! ", Toast.LENGTH_LONG).show();
                Button pauseButton = (Button) findViewById(R.id.pause);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });
    }

    private void startMeasurements() {
        registerChartMeasurementsReceiver();
        registerPersistenceMeasurementsReceiver();
        measurementOngoing = true;
        measurementStopped = false;
        startWifiMeasurementService();
        startInduction();
    }

    private void endMeasurements() {
        measurementOngoing = false;
        measurementStopped = true;
        // TODO: sound v bad
        Utils.sleep(500);
        unregisterChartMeasurementsReceiver();
        unregisterPersistenceMeasurementsReceiver();
        stopWifiMeasurementService();
        stopInduction();
    }

    private void registerPersistenceMeasurementsReceiver() {
        persistenceNewMeasurementsBroadcastReceiver = PersistenceNewMeasurementsBroadcastReceiver.getInstance(this, measurementsParameters);
        LocalBroadcastManager.getInstance(this).registerReceiver(persistenceNewMeasurementsBroadcastReceiver, new IntentFilter(Constants.NEW_MEASUREMENTS_BROADCAST_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(persistenceNewMeasurementsBroadcastReceiver, new IntentFilter(Constants.MEASUREMENT_END_BROADCAST_ACTION));
    }

    public void unregisterPersistenceMeasurementsReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(persistenceNewMeasurementsBroadcastReceiver);
    }

    private void registerChartMeasurementsReceiver() {
        chartNewMeasurementsBroadcastReceiver = ChartNewMeasurementsBroadcastReceiver.getInstance(this, measurementsParameters);
        LocalBroadcastManager.getInstance(this).registerReceiver(chartNewMeasurementsBroadcastReceiver, new IntentFilter(Constants.NEW_MEASUREMENTS_BROADCAST_ACTION));
    }

    private void unregisterChartMeasurementsReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chartNewMeasurementsBroadcastReceiver);
    }

    /***
     * WIFI SERvice
     */
    private void startWifiMeasurementService() {
        Intent startWifiMeasurementService = new Intent(this, WifiService.class);
        startWifiMeasurementService.putExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID, measurementsParameters);
        this.startService(startWifiMeasurementService);
    }

    private void stopWifiMeasurementService() {
        Intent stopWifiMeasurementService = new Intent(this, WifiService.class);
        this.stopService(stopWifiMeasurementService);
    }

    /***
     * induction service
     */
    private void startInduction(){
        String gatewayAddress = getGatewayAddress();
        Intent induction = new Intent(this, TrafficInductionService.class);
        induction.putExtra("gatewayAddress", gatewayAddress);
        this.startService(induction);
    }

    private String getGatewayAddress(){
        WifiManager manager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        DhcpInfo info = manager.getDhcpInfo();
        return Formatter.formatIpAddress(info.gateway);
    }

    private void stopInduction(){
        Intent induction = new Intent(this, TrafficInductionService.class);
        this.stopService(induction);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.nebula.haseeb.winiff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nebula.haseeb.winiff.persistence.MeasurementsFileManagementActivity;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;

public class MeasurementPreparationActivity extends AppCompatActivity {

    private MeasurementsParameters measurementsParameters = new MeasurementsParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_prep);
        setSupportActionBar(toolbar);

        setDefaultParametersValues();

        final Button startMeasurementButton = (Button) findViewById(R.id.start);
        startMeasurementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserSetValues();
                startMeasurementsActivity();
            }
        });

        final Button manageMeasurementsFileButton = (Button) findViewById(R.id.file_mgmt);
        manageMeasurementsFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserSetValues();
                startMeasurementsFilesManagementActivity();
            }
        });
    }

    private void startMeasurementsFilesManagementActivity() {
        Intent startMeasurementFileMagmtActivity = new Intent(MeasurementPreparationActivity.this, MeasurementsFileManagementActivity.class);
        startMeasurementFileMagmtActivity.putExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID, measurementsParameters);
        startActivity(startMeasurementFileMagmtActivity);
    }

    private void startMeasurementsActivity() {
        Intent startMeasurementActivity = new Intent(MeasurementPreparationActivity.this, MeasurementsActivity.class);
        startMeasurementActivity.putExtra(Constants.MEASUREMENTS_PARAMETERS_EXTRA_ID, measurementsParameters);
        startActivity(startMeasurementActivity);
    }

    private void setDefaultParametersValues() {
        // Measurement file name
        EditText measurementsFilePrefix = (EditText) findViewById(R.id.measurements_file_prefix);
        measurementsFilePrefix.setText(measurementsParameters.getMeasurementsFileNamePrefix());

        // graph width
        EditText graphWidthSeconds = (EditText) findViewById(R.id.graph_width_seconds);
        graphWidthSeconds.setText(String.valueOf(measurementsParameters.getGraphWidthSeconds()));
    }

    private void getUserSetValues() {
        // Measurement file name
        EditText measurementsFilePrefix = (EditText) findViewById(R.id.measurements_file_prefix);
        measurementsParameters.setMeasurementsFileNamePrefix(measurementsFilePrefix.getText().toString());

        // graph width
        EditText graphWidthSeconds = (EditText) findViewById(R.id.graph_width_seconds);
        measurementsParameters.setGraphWidthSeconds(Integer.valueOf(graphWidthSeconds.getText().toString()));

        // traffic induction
        CheckBox induceTrafficCheck = (CheckBox) findViewById(R.id.induceTraffic);
        measurementsParameters.setInduceTraffic(induceTrafficCheck.isChecked());
    }
}

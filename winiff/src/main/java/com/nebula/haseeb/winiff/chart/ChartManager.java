package com.nebula.haseeb.winiff.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nebula.haseeb.winiff.R;
import com.nebula.haseeb.winiff.measurements.ISeriesMeasurement;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by haseeb on 4/4/16.
 */
public class ChartManager {
    LinkedList<ISeriesMeasurement> wifiMeasurementsChartBuffer;
    private Context context;
    private MeasurementsParameters measurementsParameters;
    private LineChart chart;
    private int chartWidthSamples;

    public ChartManager(Context context, MeasurementsParameters measurementsParameters) {
        this.context = context;
        this.measurementsParameters = measurementsParameters;
        initializeLineChart();
    }

    private void initializeLineChart() {
        chart = (LineChart) ((Activity) context).findViewById(R.id.chart);
        chart.setBackgroundColor(Color.LTGRAY);
        // chart.getAxisRight().setEnabled(false);
        chart.setDescription("");
        //chart.setVisibleXRangeMaximum(Constants.DEFAULT_VISIBLE_WINDOW_WIDTH_SECONDS * Constants.DEFAULT_WIFI_RSS_SAMPLES_SECOND);
//        YAxis yAxis = chart.getAxisLeft();
//        yAxis.setAxisMinValue(Constants.MINIMUM_POWER_DBM);
//        yAxis.setAxisMaxValue(Constants.MAXIMUM_POWER_DBM);
//
//        yAxis = chart.getAxisRight();
//        yAxis.setAxisMinValue(Constants.MINIMUM_LQ);
//        yAxis.setAxisMaxValue(Constants.MAXIMUM_LQ);

        wifiMeasurementsChartBuffer = new LinkedList<>();
        chartWidthSamples = measurementsParameters.getGraphWidthSeconds() * measurementsParameters.getSamplesPerSecond();
    }

    public void updateChart(LinkedList<ISeriesMeasurement> measurements) {
        setData(measurements);
        chart.notifyDataSetChanged();
        chart.invalidate();
        chart.animateX(0);
    }

    private void addToBufferedMeasurement(LinkedList<ISeriesMeasurement> measurements) {
        wifiMeasurementsChartBuffer.addAll(measurements);
        while (wifiMeasurementsChartBuffer.size() > chartWidthSamples)
            wifiMeasurementsChartBuffer.removeFirst();
    }

    private void setData(LinkedList<ISeriesMeasurement> measurements) {

        addToBufferedMeasurement(measurements);

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> rssiVals = new ArrayList<>();
//        ArrayList<Entry> lqVals = new ArrayList<>();

        Iterator<ISeriesMeasurement> measurementIterator = wifiMeasurementsChartBuffer.iterator();
        int i = 0;
        while (measurementIterator.hasNext()) {
            ISeriesMeasurement measurement = measurementIterator.next();
            xVals.add((float) (measurement.getStepValue() / 1000) + "");
            rssiVals.add(new Entry(measurement.getMeasurements()[0], i));
            //lqVals.add(new Entry(measurement.getMeasurements()[1], i));
            i++;
            if (i >= chartWidthSamples)
                i = 0;
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(initRssiDataSet(rssiVals));
//        dataSets.add(initLqDataSet(lqVals));

        // create a data object with the datasets
        LineData lineData = new LineData(xVals, dataSets);
        lineData.notifyDataChanged();

        // set data
        chart.setData(lineData);
    }

    private LineDataSet initRssiDataSet(ArrayList<Entry> rssiVals) {
        LineDataSet rssiDataSet = new LineDataSet(rssiVals, "Active Wi-Fi RSS(dBm) Vs Time(sec)");
        rssiDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        rssiDataSet.setColor(ColorTemplate.getHoloBlue());
        rssiDataSet.setLineWidth(4f);
        rssiDataSet.setDrawCircles(true);
        rssiDataSet.setCircleColor(Color.WHITE);
        rssiDataSet.setCircleRadius(1f);
        rssiDataSet.setFillAlpha(65);
        rssiDataSet.setFillColor(ColorTemplate.getHoloBlue());
        rssiDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        //set1.setDrawCircleHole(false);
        rssiDataSet.setDrawValues(false);
        return rssiDataSet;
    }

    private LineDataSet initLqDataSet(ArrayList<Entry> laVals) {
        LineDataSet rssiDataSet = new LineDataSet(laVals, "Active Wi-Fi LQ Vs Time(sec)");
        rssiDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        rssiDataSet.setLineWidth(4f);
        rssiDataSet.setDrawCircles(true);
        rssiDataSet.setCircleColor(Color.RED);
        rssiDataSet.setCircleRadius(1f);
        rssiDataSet.setFillAlpha(65);
        rssiDataSet.setFillColor(Color.RED);
        rssiDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        //set1.setDrawCircleHole(false);
        rssiDataSet.setDrawValues(false);
        return rssiDataSet;
    }
}

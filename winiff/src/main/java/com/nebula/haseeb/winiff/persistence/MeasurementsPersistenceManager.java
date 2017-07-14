package com.nebula.haseeb.winiff.persistence;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nebula.haseeb.winiff.measurements.ISeriesMeasurement;
import com.nebula.haseeb.winiff.utils.Constants;
import com.nebula.haseeb.winiff.utils.MeasurementsParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by haseeb on 4/3/16.
 */
public class MeasurementsPersistenceManager {

    private MeasurementsParameters measurementsParameters;

    private Context context;
    private File measurementsFilesDir;
    private File currentMeasurementFile;
    private SimpleDateFormat measurementsFileDateFormat = new SimpleDateFormat(Constants.MEASUREMENTS_FILE_DATE_FORMAT);

    public MeasurementsPersistenceManager(Context context, MeasurementsParameters measurementsParameters) {
        this.context = context;
        this.measurementsParameters = measurementsParameters;
        this.measurementsFilesDir = createMeasurementsFilesDir();
    }

    public void saveMeasurements(LinkedList<ISeriesMeasurement> measurements) {
        if (currentMeasurementFile == null)
            currentMeasurementFile = getMeasurementStorageFile();

        StringBuilder measurementsString = new StringBuilder();
        Iterator<ISeriesMeasurement> measurementIterator = measurements.iterator();
        while (measurementIterator.hasNext()) {
            measurementsString.append(measurementIterator.next().toString() + "\n");
        }
        if (isExternalStorageWritable()) {
            try {
                PrintStream printStream = new PrintStream(new FileOutputStream(currentMeasurementFile, true));
                printStream.print(measurementsString.toString());
                printStream.flush();
                printStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(this.getClass().getName(), "External storage not available");
        }
    }

    public void closeCurrentMeasurementFile() {
        // rename the file
        File newName = new File(measurementsFilesDir, currentMeasurementFile.getName() + "_" + measurementsFileDateFormat.format(new Date()));
        currentMeasurementFile.renameTo(newName);
        // unset the @currentMeasurementFile field
        currentMeasurementFile = null;
        // inform the user
        Toast.makeText(context, "measurements successfully saved to " + newName.getName(), Toast.LENGTH_SHORT).show();
    }

    public File[] getAllMeasurementFiles() {
        return measurementsFilesDir.listFiles();
    }

    public boolean deleteMeasurementFiles(List<File> files) {
        boolean success = true;
        for (File file : files)
            success = success && file.delete();
        return success;
    }

    private File getMeasurementStorageFile() {
        return new File(measurementsFilesDir, getMeasurementFileName());
    }

    private File createMeasurementsFilesDir() {
        File measurementFilesDir = null;
        if (isExternalStorageWritable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                measurementFilesDir = new File(context.getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS), Constants.MEASUREMENTS_FILES_DIR);
            } else {
                measurementFilesDir = new File(Environment.getExternalStorageDirectory() + "/Documents/winiff/" + Constants.MEASUREMENTS_FILES_DIR);
            }
            boolean isPresent = true;
            if (!measurementFilesDir.exists()) {
                isPresent = measurementFilesDir.mkdirs();
            }
            if (!isPresent)
                throw new RuntimeException("Could not get access to the document folder " + measurementFilesDir.toString());
        }
        return measurementFilesDir;
    }

    private String getMeasurementFileName() {
        return measurementsParameters.getMeasurementsFileNamePrefix() + "_" + measurementsFileDateFormat.format(new Date());
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}

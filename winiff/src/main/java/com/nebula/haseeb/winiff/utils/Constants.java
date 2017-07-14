package com.nebula.haseeb.winiff.utils;

/**
 * Created by haseeb on 3/23/16.
 */
public final class Constants {
    // Defines a NEW MEASUREMENT REPORT Intent action
    public static final String NEW_MEASUREMENTS_BROADCAST_ACTION = "NEW_MEASUREMENTS";

    // Defines a Measurement end report intent action
    public static final String MEASUREMENT_END_BROADCAST_ACTION = "MEASUREMENT_END";

    // measurements parameters Extra id
    public static final String MEASUREMENTS_PARAMETERS_EXTRA_ID = "MEASUREMENTS_PARAMETERS";

    // Defines the key for all the available WIFI measurements; current RSSI and LQI
    public static final String EXTENDED_DATA_ALL_WIFI_MEASUREMENTS = "ALL WIFI MEASUREMENTS";

    // WIFI RSS samples per second
    public static final int DEFAULT_WIFI_RSS_SAMPLES_SECOND = 200;

    // Measurements reports per second
    public static final int MEASUREMENT_REPORTS_SECOND = 2;

    // Visible window width in seconds
    public static final int DEFAULT_VISIBLE_WINDOW_WIDTH_SECONDS = 20;

    // minimum visible power in mdb
    public static final int MINIMUM_POWER_DBM = -80;

    // maximum visible power in mdb
    public static final int MAXIMUM_POWER_DBM = -20;

    // minimum visible link quality value
    public static final int MINIMUM_LQ = 0;

    // maximum visible link quality value
    public static final int MAXIMUM_LQ = 100;

    // measurements dir
    public static final String MEASUREMENTS_FILES_DIR = "measurements";

    // measurements file prefix
    public static final String DEFAULT_MEASUREMENTS_FILE_RPEFIX = "measurements";

    // measurements file data format
    public static final String MEASUREMENTS_FILE_DATE_FORMAT = "yyyyMMddHHmmss";
}

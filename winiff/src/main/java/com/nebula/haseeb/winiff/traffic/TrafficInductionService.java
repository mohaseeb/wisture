package com.nebula.haseeb.winiff.traffic;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.nebula.haseeb.winiff.MeasurementsActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import android.support.annotation.Nullable;
import android.text.format.Formatter;

/**
 * Created by haseeb on 11/23/16.
 */

public class TrafficInductionService extends IntentService {
    private final static Logger logger = Logger.getLogger(TrafficInductionService.class.getName());

    private InetAddress router;
    private int checkTimeout = 20; // milliseconds

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TrafficInductionService() {
        super(TrafficInductionService.class.getName());
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
        try {
            router = InetAddress.getByName(intent.getStringExtra("gatewayAddress"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info("Started inducing traffic towards " + router.getHostAddress());
        while (!MeasurementsActivity.measurementStopped) {
            if (!MeasurementsActivity.measurementOngoing) {
                sleep(100);
                continue;
            }
            try {
                boolean res = router.isReachable(checkTimeout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Stopped inducing traffic towards " + router.getHostAddress());
    }
}

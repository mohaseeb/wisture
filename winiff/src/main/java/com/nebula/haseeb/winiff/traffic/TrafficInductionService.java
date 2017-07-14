package com.nebula.haseeb.winiff.traffic;

import android.app.IntentService;
import android.content.Intent;

import com.nebula.haseeb.winiff.MeasurementsActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by haseeb on 11/23/16.
 */

public class TrafficInductionService extends IntentService {
    private final static Logger logger = Logger.getLogger(TrafficInductionService.class.getName());

    private InetAddress router;
    private int checkTimeout = 1; // milliseconds

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TrafficInductionService() {
        super(TrafficInductionService.class.getName());
        try {
//            router = InetAddress.getByName("192.168.1.1");
            router = InetAddress.getByName("192.168.2.1");
        } catch (UnknownHostException e) {
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
        try {
//            router = InetAddress.getByName("192.168.1.1");
            router = InetAddress.getByName("192.168.2.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info("Started inducing traffic towards " + router.getHostAddress());
//        long count = 0;
//        long start = System.nanoTime();
        while (!MeasurementsActivity.measurementStopped) {
            if (!MeasurementsActivity.measurementOngoing) {
                sleep(100);
                continue;
            }
            try {
                router.isReachable(checkTimeout);
//                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        logger.info("avg " + (count * 1000000000) / (System.nanoTime() - start));
        logger.info("Stopped inducing traffic towards " + router.getHostAddress());
    }
}

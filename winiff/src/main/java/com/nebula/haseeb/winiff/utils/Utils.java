package com.nebula.haseeb.winiff.utils;

/**
 * Created by haseeb on 4/8/16.
 */
public class Utils {
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

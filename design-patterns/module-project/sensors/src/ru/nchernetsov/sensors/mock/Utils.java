package ru.nchernetsov.sensors.mock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Utils {
    static double sleepAndGenerateRandomValue(int millisecondsToSleep, double min, double max) {
        try {
            TimeUnit.MILLISECONDS.sleep(millisecondsToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return randomDoubleInRange(min, max);
    }

    private static double randomDoubleInRange(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}

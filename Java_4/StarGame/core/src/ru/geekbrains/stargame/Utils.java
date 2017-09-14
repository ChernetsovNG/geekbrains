package ru.geekbrains.stargame;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static Random random;

    static {
        random = new Random(System.currentTimeMillis());
    }

    public static float getRandomFloatInRange(float min, float max) {
        return min + random.nextFloat()*(max - min);
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }
}

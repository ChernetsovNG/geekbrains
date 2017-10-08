package com.geek.spaceshooter.game;

/**
 * Created by FlameXander on 04.10.2017.
 */

public class LevelInfo {
    private int level;
    private float asteroidGenerationTime;
    private int asteroidHpMin;
    private int asteroidHpMax;
    private float asteroidSpeedMin;
    private float asteroidSpeedMax;

    public int getLevel() {
        return level;
    }

    public float getAsteroidGenerationTime() {
        return asteroidGenerationTime;
    }

    public int getAsteroidHpMin() {
        return asteroidHpMin;
    }

    public int getAsteroidHpMax() {
        return asteroidHpMax;
    }

    public float getAsteroidSpeedMin() {
        return asteroidSpeedMin;
    }

    public float getAsteroidSpeedMax() {
        return asteroidSpeedMax;
    }

    public LevelInfo(int level, float asteroidGenerationTime, int asteroidHpMin, int asteroidHpMax, float asteroidSpeedMin, float asteroidSpeedMax) {
        this.level = level;
        this.asteroidGenerationTime = asteroidGenerationTime;
        this.asteroidHpMin = asteroidHpMin;
        this.asteroidHpMax = asteroidHpMax;
        this.asteroidSpeedMin = asteroidSpeedMin;
        this.asteroidSpeedMax = asteroidSpeedMax;
    }
}

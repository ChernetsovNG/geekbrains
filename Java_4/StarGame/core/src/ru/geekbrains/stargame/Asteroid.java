package ru.geekbrains.stargame;

import java.util.ArrayList;
import java.util.List;

import static ru.geekbrains.stargame.Utils.getRandomBoolean;
import static ru.geekbrains.stargame.Utils.getRandomFloatInRange;

public class Asteroid {
    public float x, y;    // координаты нижнего левого угла астероида
    public float vx, vy;  // скорость астероида, пикс/сек
    public float angle;   // угол поворота астероида
    public float omega;   // скорость вращения астероида, град/сек
    public boolean rotateDir;  // направление вращения астероида, true - против часовой стрелки, false - по часовой стрелке
    public float scale;   // масштаб астероида (относительно базового размера)

    public Asteroid(float x, float y, float vx, float vy, float angle, float omega, boolean rotateDir, float scale) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.omega = omega;
        this.rotateDir = rotateDir;
        this.scale = scale;
    }

    public static List<Asteroid> getRandomAsteroids(int asteroidsCount) {
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < asteroidsCount; i++) {
            asteroids.add(getRandomAsteroid());
        }
        return asteroids;
    }

    // Создать "случайный" астероид
    private static Asteroid getRandomAsteroid() {
        float x = getRandomFloatInRange(100, 1100);
        float y = getRandomFloatInRange(100, 600);
        float vx = getRandomFloatInRange(-100, 100);
        float vy = getRandomFloatInRange(-100, 100);
        float angle = getRandomFloatInRange(0, 360);
        float omega = getRandomFloatInRange(0, 30);
        boolean rotateDir = getRandomBoolean();
        float scale = getRandomFloatInRange(0.3f, 1.0f);

        return new Asteroid(x, y, vx, vy, angle, omega, rotateDir, scale);
    }
}

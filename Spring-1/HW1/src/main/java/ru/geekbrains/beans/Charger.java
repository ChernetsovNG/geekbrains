package ru.geekbrains.beans;

import java.util.Collection;
import java.util.Optional;
import java.util.Stack;

/**
 * Магазин, патронная обойма
 */
public class Charger {
    /**
     * Ёмкость магазина
     */
    private int maxSize;

    private final Stack<Bullet> bullets = new Stack<>();

    /**
     * Зарядить магазин
     *
     * @param bullets - коллекция патронов для заряжания
     */
    public void charge(Collection<Bullet> bullets) {
        System.out.println("Charge rifle by bullets: " + bullets);
        for (Bullet bullet : bullets) {
            addBullet(bullet);
        }
    }

    public void addBullet(Bullet bullet) {
        if (bullets.size() < maxSize) {
            bullets.push(bullet);
        }
    }

    public Optional<Bullet> removeBullet() {
        if (bullets.size() > 0) {
            return Optional.of(bullets.pop());
        } else {
            return Optional.empty();
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}

package ru.geekbrains.beans;

import java.util.Optional;

/**
 * Ружьё, винтовка
 */
public class Rifle {
    private Charger charger;

    /**
     * Произвести выстрел
     */
    public void fire() {
        Optional<Bullet> bulletOptional = charger.removeBullet();
        if (bulletOptional.isPresent()) {
            System.out.println("Fire from rifle! Bullet is: " + bulletOptional.get());
        } else {
            System.out.println("Charger is empty!");
        }
    }

    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

}

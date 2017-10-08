package com.geek.spaceshooter.game;

/**
 * Created by FlameXander on 07.10.2017.
 */

public enum BulletType {
    FIREBALL(0), GREENRAY(1);

    private int index;

    public int getIndex() {
        return index;
    }

    BulletType(int index) {
        this.index = index;
    }
}

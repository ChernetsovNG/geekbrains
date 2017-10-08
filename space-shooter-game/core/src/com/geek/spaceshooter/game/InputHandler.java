package com.geek.spaceshooter.game;

import com.badlogic.gdx.Gdx;

/**
 * Created by FlameXander on 16.09.2017.
 */

public class InputHandler {
    public static boolean isTouched() {
        return Gdx.input.isTouched();
    }

    public static int getX() {
        return Gdx.input.getX();
    }

    public static int getY() {
        return 720 - Gdx.input.getY();
    }
}

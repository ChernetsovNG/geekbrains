package com.geek.spaceshooter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.sun.org.apache.bcel.internal.generic.LASTORE;

/**
 * Created by FlameXander on 30.09.2017.
 */

public abstract class Ship extends SpaceObject {
    protected float enginePower;

    protected float currentFire;
    protected float fireRate;

    protected Vector2 weaponDirection;
    protected boolean isPlayer;

    public void pressFire(float dt) {
        currentFire += dt;
        if (currentFire > fireRate) {
            currentFire -= fireRate;
            fire();
        }
    }

    public void fire() {
        BulletType bt = BulletType.FIREBALL;
        if (!isPlayer) bt = BulletType.GREENRAY;
        game.getBulletEmitter().setup(bt, isPlayer, position.x + 24.0f, position.y + 0.0f, weaponDirection.x * 640, weaponDirection.y * 640);
    }
}

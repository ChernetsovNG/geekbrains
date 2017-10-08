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

/**
 * Created by FlameXander on 30.09.2017.
 */

public abstract class SpaceObject {
    protected GameScreen game;

    protected TextureRegion texture;
    protected Vector2 position;
    protected Vector2 velocity;

    protected int hp;
    protected int hpMax;
    protected float damageReaction;

    protected Circle hitArea;

    protected boolean active;

    public Circle getHitArea() {
        return hitArea;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getHpMax() {
        return hpMax;
    }

    public boolean isActive() {
        return active;
    }

    public abstract void render(SpriteBatch batch);

    public abstract void update(float dt);

    public abstract void onDestroy();

    public boolean takeDamage(int dmg) {
        hp -= dmg;
        damageReaction += 0.2f;
        if (damageReaction > 1.0f) damageReaction = 1.0f;
        if (hp <= 0) {
            onDestroy();
            return true;
        }
        return false;
    }

    public void deactivate() {
        this.active = false;
    }
}

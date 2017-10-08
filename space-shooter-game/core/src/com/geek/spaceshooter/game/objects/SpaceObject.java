package com.geek.spaceshooter.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.geek.spaceshooter.game.screen.GameScreen;

public abstract class SpaceObject {
    protected GameScreen game;

    TextureRegion texture;
    protected Vector2 position;
    Vector2 velocity;

    int hp;
    int hpMax;
    float damageReaction;

    Circle hitArea;

    boolean active;

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

    void deactivate() {
        this.active = false;
    }
}

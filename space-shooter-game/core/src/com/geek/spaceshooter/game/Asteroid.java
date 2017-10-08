package com.geek.spaceshooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by FlameXander on 16.09.2017.
 */

public class Asteroid extends SpaceObject implements Poolable {
    private float scale;
    private float angle;
    private float angularSpeed;

    public Asteroid(TextureRegion texture) {
        this.texture = texture;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.scale = 0;
        this.angle = 0;
        this.angularSpeed = 0;
        this.hpMax = 0;
        this.hp = 0;
        this.hitArea = new Circle(position.x, position.y, 28 * scale);
        this.damageReaction = 0.0f;
        this.active = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (damageReaction > 0.01f) {
            batch.setColor(1.0f, 1.0f - damageReaction, 1.0f - damageReaction, 1.0f);
        }
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, scale, scale, angle);
        if (damageReaction > 0.01f) {
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public void update(float dt) {
        position.mulAdd(velocity, dt);
        angle += angularSpeed * dt;
        if (position.x < -100.0f) {
            deactivate();
        }
        hitArea.setPosition(position);
        damageReaction -= dt * 2.0f;
        if (damageReaction < 0.0f) damageReaction = 0.0f;
    }

    @Override
    public void onDestroy() {
        deactivate();
    }

    public void activate(float x, float y, float vx, float vy, int hpMax, float r) {
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.angle = MathUtils.random(0.0f, 360.0f);
        this.hpMax = hpMax;
        this.hp = this.hpMax;
        this.active = true;
        this.scale = r;
        this.hitArea.radius = 28.0f * this.scale;
        this.damageReaction = 0.0f;
    }
}

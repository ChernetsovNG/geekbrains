package com.geek.spaceshooter.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by FlameXander on 30.09.2017.
 */

public class Bot extends Ship implements Poolable {
    private TextureRegion[] frames;
    private int maxFrames;
    private float time;
    private float timePerFrame;
    private Route route;

    public Bot(GameScreen game, TextureRegion texture) {
        this.game = game;
        this.texture = texture;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.enginePower = 0;
        this.currentFire = 0.0f;
        this.fireRate = 0;
        this.hpMax = 0;
        this.hp = this.hpMax;
        this.hitArea = new Circle(position, 28);
        this.weaponDirection = new Vector2(-1.0f, 0.0f);
        this.isPlayer = false;
        this.maxFrames = 4;
        this.timePerFrame = 0.1f;
        this.time = MathUtils.random(0.0f, maxFrames * timePerFrame);
        this.frames = new TextureRegion[maxFrames];
        for (int i = 0; i < maxFrames; i++) {
            frames[i] = new TextureRegion(texture, i * 64, 0, 64, 64);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (damageReaction > 0.01f) {
            batch.setColor(1.0f, 1.0f - damageReaction, 1.0f - damageReaction, 1.0f);
        }
        int k = (int) (time / timePerFrame);
        if (k > maxFrames - 1) k = maxFrames - 1;
        batch.draw(frames[k], position.x - 32, position.y - 32);
        if (damageReaction > 0.01f) {
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public void update(float dt) {
        time += dt;
        if (time > maxFrames * timePerFrame) {
            time = 0.0f;
        }
        pressFire(dt);
        damageReaction -= dt * 2.0f;
        if (damageReaction < 0.0f) damageReaction = 0.0f;
        if (position.x < -100) {
            deactivate();
        }
        //velocity.set(-120, 0);
        velocity.set(route.getMyVelocity((int)position.x));
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
        velocity.scl(0.95f);
    }

    @Override
    public void onDestroy() {
        deactivate();
    }

    public void activate(Route route) {
        position.set(route.getInitialPosition());
        fireRate = 1.0f;
        hitArea.setRadius(28);
        hpMax = 15;
        active = true;
        hp = hpMax;
        this.route = route;
    }
}

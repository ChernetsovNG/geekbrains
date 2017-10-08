package com.geek.spaceshooter.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by FlameXander on 27.09.2017.
 */

public class AsteroidEmitter extends ObjectPool<Asteroid> {
    private GameScreen gameScreen;
    private TextureRegion asteroidTexture;
    private float generationTime;
    private float innerTimer;

    public void setGenerationTime(float generationTime) {
        this.generationTime = generationTime;
    }

    @Override
    protected Asteroid newObject() {
        return new Asteroid(asteroidTexture);
    }

    public AsteroidEmitter(GameScreen gameScreen, TextureRegion asteroidTexture, int size, float generationTime) {
        super();
        this.gameScreen = gameScreen;
        this.asteroidTexture = asteroidTexture;
        for (int i = 0; i < size; i++) {
            freeList.add(newObject());
        }
        this.generationTime = generationTime;
        this.innerTimer = 0.0f;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    public void update(float dt) {
        innerTimer += dt;
        if (innerTimer > generationTime) {
            innerTimer -= generationTime;
            setup();
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
    }

    public void setup() {
        LevelInfo info = gameScreen.getCurrentLevelInfo();
        Asteroid a = getActiveElement();
        float x = MathUtils.random(1400.0f, 2200.0f);
        float y = MathUtils.random(0, 720.0f);
        float vx = -MathUtils.random(info.getAsteroidSpeedMin(), info.getAsteroidSpeedMax());
        float vy = 0;
        int hpMax = MathUtils.random(info.getAsteroidHpMin(), info.getAsteroidHpMax());
        int delta = info.getAsteroidHpMax() - info.getAsteroidHpMin();
        int delta2 = hpMax - info.getAsteroidHpMin();
        float r = 0.5f + 1.5f * ((float)delta2 / (float)delta);
        a.activate(x, y, vx, vy, hpMax, r);
    }
}

package com.geek.spaceshooter.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by FlameXander on 27.09.2017.
 */

public class PowerUpsEmitter {
    private PowerUp[] powerUps;
    private TextureRegion[][] textureRegion;

    public PowerUp[] getPowerUps() {
        return powerUps;
    }

    public PowerUpsEmitter(TextureRegion textureRegion) {
        this.textureRegion = textureRegion.split(32, 32);
        this.powerUps = new PowerUp[50];
        for (int i = 0; i < powerUps.length; i++) {
            powerUps[i] = new PowerUp();
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < powerUps.length; i++) {
            if (powerUps[i].isActive()) {
                batch.draw(textureRegion[0][powerUps[i].getType().getNumber()], powerUps[i].getPosition().x - 16, powerUps[i].getPosition().y - 16);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < powerUps.length; i++) {
            if (powerUps[i].isActive()) {
                powerUps[i].update(dt);
            }
        }
    }

    public void makePower(float x, float y) {
        if (Math.random() < 0.2) {
            for (int i = 0; i < powerUps.length; i++) {
                if (!powerUps[i].isActive()) {
                    PowerUp.Type t = PowerUp.Type.values()[(int) (Math.random() * 4)];
                    powerUps[i].activate(x, y, t);
                    break;
                }
            }
        }
    }
}

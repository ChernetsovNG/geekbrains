package com.geek.spaceshooter.game.emitter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geek.spaceshooter.game.objects.PowerUp;

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
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                batch.draw(textureRegion[0][powerUp.getType().getNumber()], powerUp.getPosition().x - 16, powerUp.getPosition().y - 16);
            }
        }
    }

    public void update(float dt) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive()) {
                powerUp.update(dt);
            }
        }
    }

    public void makePower(float x, float y) {
        if (Math.random() < 0.2) {
            for (PowerUp powerUp : powerUps) {
                if (!powerUp.isActive()) {
                    PowerUp.Type t = PowerUp.Type.values()[(int) (Math.random() * 4)];
                    powerUp.activate(x, y, t);
                    break;
                }
            }
        }
    }
}

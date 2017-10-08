package com.geek.spaceshooter.game.emitter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.geek.spaceshooter.game.objects.Boom;

public class BoomEmitter {
    private Boom[] booms;
    private Sound boomSound;

    public BoomEmitter(TextureRegion texture, Sound boomSound) {
        booms = new Boom[50];
        TextureRegion[][] regions = texture.split(64, 64);
        TextureRegion[] result = new TextureRegion[regions[0].length * regions.length];
        this.boomSound = boomSound;
        for (int i = 0, n = 0; i < regions.length; i++) {
            for (int j = 0; j < regions[0].length; j++, n++) {
                result[n] = regions[i][j];
            }
        }
        for (int i = 0; i < booms.length; i++) {
            booms[i] = new Boom(result);
        }
    }

    public void update(float dt) {
        for (Boom boom : booms) {
            if (boom.isActive()) {
                boom.update(dt);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Boom boom : booms) {
            if (boom.isActive()) {
                boom.render(batch);
            }
        }
    }

    public void setup(Vector2 position) {
        for (Boom boom : booms) {
            if (!boom.isActive()) {
                boom.activate(position);
                boomSound.play();
                break;
            }
        }
    }

    public void dispose() {
        boomSound.dispose();
    }
}

package com.geek.spaceshooter.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by FlameXander on 20.09.2017.
 */

public class Background {
    private class Star {
        private Vector2 position;
        private float speed;

        public Star() {
            this.position = new Vector2((float) Math.random() * SpaceGame.SCREEN_WIDTH, (float) Math.random() * SpaceGame.SCREEN_HEIGHT);
            this.speed = MathUtils.random(20.0f, 100.0f);
        }

        public void update(float dt, Vector2 v) {
            position.x -= speed * dt;
            position.mulAdd(v, -dt * 0.1f);
            if (position.y < -20) {
                position.y = SpaceGame.SCREEN_HEIGHT;
            }
            if (position.y > SpaceGame.SCREEN_HEIGHT) {
                position.y = -20;
            }
            if (position.x < -40) {
                position.x = SpaceGame.SCREEN_WIDTH;
                speed = MathUtils.random(20.0f, 100.0f);
            }
        }
    }

    private Texture texture;
    private TextureRegion textureStar;
    private Star[] stars;

    public Background(TextureRegion textureStar) {
        texture = new Texture("bg.png");
        this.textureStar = textureStar;
        stars = new Star[250];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, 0, 0);
        for (Star o : stars) {
            float scale = o.speed / 200.0f + 0.1f;
            if (Math.random() < 0.02) {
                scale *= 1.5f;
            }
            batch.draw(textureStar, o.position.x, o.position.y, 8, 8, 16, 16, scale, scale, 0);
        }
    }

    public void update(float dt, Vector2 v) {
        for (Star o : stars) {
            o.update(dt, v);
        }
    }

    // public void dispose() {
        // texture.dispose();
    // }
}

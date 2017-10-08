package com.geek.spaceshooter.game.emitter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geek.spaceshooter.game.objects.Bullet;

public class BulletEmitter extends ObjectPool<Bullet> {
    private TextureRegion[] bulletsTexture;

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }

    public BulletEmitter(TextureRegion bulletsTexture, int size) {
        super(size);
        this.bulletsTexture = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            this.bulletsTexture[i] = new TextureRegion(bulletsTexture, i * 36, 0, 36, 36);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            batch.draw(bulletsTexture[activeList.get(i).getType().getIndex()], activeList.get(i).getPosition().x - 24, activeList.get(i).getPosition().y - 24, 24, 24, 48, 48, 0.7f, 0.7f, 0.0f);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
    }

    public void setup(Bullet.BulletType type, boolean isPlayersBullet, float x, float y, float vx, float vy) {
        Bullet b = getActiveElement();
        b.activate(type, isPlayersBullet, x, y, vx, vy);
    }
}

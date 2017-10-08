package com.geek.spaceshooter.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.geek.spaceshooter.game.SpaceGame;

public class Bullet implements Poolable {
    public enum BulletType {
        FIREBALL(0), GREENRAY(1);
        private int index;
        public int getIndex() {
            return index;
        }
        BulletType(int index) {
            this.index = index;
        }
    }

    private boolean isPlayersBullet;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private BulletType type;

    public boolean isPlayersBullet() {
        return isPlayersBullet;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public BulletType getType() {
        return type;
    }

    public Bullet() {
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.active = false;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate(BulletType type, boolean isPlayersBullet, float x, float y, float vx, float vy) {
        this.type = type;
        this.isPlayersBullet = isPlayersBullet;
        position.set(x, y);
        velocity.set(vx, vy);
        active = true;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if(position.x > SpaceGame.SCREEN_WIDTH) {
            deactivate();
        }
    }
}

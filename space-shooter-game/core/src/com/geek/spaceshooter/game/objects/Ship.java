package com.geek.spaceshooter.game.objects;

import com.badlogic.gdx.math.Vector2;

public abstract class Ship extends SpaceObject {
    float enginePower;

    float currentFire;
    float fireRate;

    Vector2 weaponDirection;
    boolean isPlayer;

    public void pressFire(float dt) {
        currentFire += dt;
        if (currentFire > fireRate) {
            currentFire -= fireRate;
            fire();
        }
    }

    public void fire() {
        Bullet.BulletType bt = Bullet.BulletType.FIREBALL;
        if (!isPlayer) bt = Bullet.BulletType.GREENRAY;
        game.getBulletEmitter().setup(bt, isPlayer, position.x + 24.0f, position.y + 0.0f, weaponDirection.x * 640, weaponDirection.y * 640);
    }
}

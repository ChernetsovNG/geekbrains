package com.geek.spaceshooter.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.geek.spaceshooter.game.SpaceGame;

public class Player extends Ship {
    private TextureRegion redHpRegion;
    private TextureRegion greenHpRegion;
    private int lives;

    private com.geek.spaceshooter.game.control.Joystick joystick;

    private int score;
    private int money;

    private Sound fireSound;

    // private float time;

    private StringBuilder hudStringHelper;

    public int getLives() {
        return lives;
    }

    public void addScore(int amount) {
        score += amount;
    }

    void addMoney(int amount) {
        money += amount;
    }

    public Player(com.geek.spaceshooter.game.screen.GameScreen game, TextureRegion texture, TextureRegion textureHP, TextureRegion textureJoystick, Sound fireSound, Vector2 position, Vector2 velocity, float engine) {
        this.game = game;
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.enginePower = engine;
        this.currentFire = 0.0f;
        this.fireRate = 0.1f;
        this.hpMax = 40;
        this.hp = this.hpMax;
        this.lives = 3;
        this.redHpRegion = new TextureRegion(textureHP, 0, 32, 224, 32);
        this.greenHpRegion = new TextureRegion(textureHP, 0, 0, 224, 32);
        this.hitArea = new Circle(position, 24);
        this.hudStringHelper = new StringBuilder(50);
        this.score = 0;
        this.money = 0;
        this.weaponDirection = new Vector2(1.0f, 0.0f);
        this.isPlayer = true;
        this.joystick = new com.geek.spaceshooter.game.control.Joystick(this, textureJoystick);
        this.fireSound = fireSound;
        // this.time = 0.0f;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1, 1, velocity.y / 30.0f);
    }

    public void renderHUD(SpriteBatch batch, BitmapFont fnt, float x, float y) {
        // batch.draw(redHpRegion, x, y, 224, 32);
        // batch.draw(greenHpRegion, x, y, 224 * ((float)hp / (float)hpMax), 32);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(redHpRegion, x + (int) (Math.random() * damageReaction * 10), y + (int) (Math.random() * damageReaction * 10));
        batch.draw(greenHpRegion, x + (int) (Math.random() * damageReaction * 10), y + (int) (Math.random() * damageReaction * 10), (int) ((float) hp / hpMax * 224), 32);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.setColor(1, 1, 0, damageReaction);
        batch.draw(redHpRegion, x + (int) (Math.random() * damageReaction * 25), y + (int) (Math.random() * damageReaction * 25));
        batch.draw(greenHpRegion, x + (int) (Math.random() * damageReaction * 25), y + (int) (Math.random() * damageReaction * 25), (int) ((float) hp / hpMax * 224), 32);
        batch.setColor(1, 1, 1, 1);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        hudStringHelper.setLength(0);
        hudStringHelper.append("x").append(lives);
        fnt.draw(batch, hudStringHelper, x + 224, y + 22);
        hudStringHelper.setLength(0);
        hudStringHelper.append("Score: ").append(score);
        fnt.draw(batch, hudStringHelper, x + 4, y - 4);
        joystick.render(batch);
    }

    public void fullRepair() {
        this.hp = this.hpMax;
    }

    @Override
    public void update(float dt) {
        joystick.update(dt);
        // time += dt;
        // for (int i = 0; i < 10; i++) {
        //     game.getParticleEmitter().setup(position.x + 32 * (float) Math.cos(time * 5 + i * 0.628f), position.y + 32 * (float) Math.sin(time * 5 + i * 0.628f), MathUtils.random(-10, 10), MathUtils.random(-10, 10), 0.2f, 0.8f, 1.1f, 0, 0, 1, 1, 1, 1, 1, 1);
        // }

        if (joystick.getPower() > 0.02f) {
            velocity.x += enginePower * dt * joystick.getNormal().x * joystick.getPower();
            velocity.y += enginePower * dt * joystick.getNormal().y * joystick.getPower();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x += enginePower * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x -= enginePower * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y += enginePower * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y -= enginePower * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            pressFire(dt);
        }
        damageReaction -= dt * 2.0f;
        if (damageReaction < 0.0f) damageReaction = 0.0f;
        if (position.x < 32) {
            position.x = 32;
            if (velocity.x < 0) velocity.x = 0;
        }
        if (position.x > SpaceGame.SCREEN_WIDTH - 32) {
            position.x = SpaceGame.SCREEN_WIDTH - 32;
            if (velocity.x > 0) velocity.x = 0;
        }
        if (position.y < 32) {
            position.y = 32;
            if (velocity.y < 0) velocity.y = 0;
        }
        if (position.y > SpaceGame.SCREEN_HEIGHT - 32) {
            position.y = SpaceGame.SCREEN_HEIGHT - 32;
            if (velocity.y > 0) velocity.y = 0;
        }
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
        velocity.scl(0.95f);
        if (velocity.x > 20.0f) {
            float size = velocity.len() / 120.0f;
            game.getParticleEmitter().setup(position.x - 24, position.y, -MathUtils.random(5.0f, 20.0f), MathUtils.random(-24.0f, 24.0f), 0.5f, size, 0.6f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        }
    }

    @Override
    public void onDestroy() {
        hp = hpMax;
        lives--;
        damageReaction = 0.0f;
    }

    @Override
    public void fire() {
        super.fire();
        fireSound.play();
    }

    public void dispose() {
        fireSound.dispose();
    }
}

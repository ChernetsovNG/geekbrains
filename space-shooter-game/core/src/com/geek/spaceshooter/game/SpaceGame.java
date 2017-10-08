package com.geek.spaceshooter.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Таблица результатов во внешнем файле
// + AssetManager
// + Bots Routes + AI
// Save Game
//
// WeaponsShop
// PowerUps
// <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

public class SpaceGame extends Game {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    private SpriteBatch batch;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;

    private Viewport viewport;
    private Camera camera;

    public Camera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
        viewport.update(SCREEN_WIDTH, SCREEN_HEIGHT, true);
        viewport.apply();
        MyInputProcessor mip = new MyInputProcessor(this);
        Gdx.input.setInputProcessor(mip);
        gameScreen = new GameScreen(this, batch);
        menuScreen = new MenuScreen(this, batch);
        setScreen(menuScreen);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
        camera.update();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
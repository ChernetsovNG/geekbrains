package com.geek.spaceshooter.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.geek.spaceshooter.game.control.Assets;
import com.geek.spaceshooter.game.objects.Asteroid;
import com.geek.spaceshooter.game.emitter.AsteroidEmitter;
import com.geek.spaceshooter.game.objects.Background;
import com.geek.spaceshooter.game.emitter.BoomEmitter;
import com.geek.spaceshooter.game.objects.Bot;
import com.geek.spaceshooter.game.emitter.BotEmitter;
import com.geek.spaceshooter.game.objects.Bullet;
import com.geek.spaceshooter.game.emitter.BulletEmitter;
import com.geek.spaceshooter.game.objects.LevelInfo;
import com.geek.spaceshooter.game.emitter.ParticleEmitter;
import com.geek.spaceshooter.game.objects.Player;
import com.geek.spaceshooter.game.objects.PowerUp;
import com.geek.spaceshooter.game.emitter.PowerUpsEmitter;
import com.geek.spaceshooter.game.SpaceGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameXander on 30.09.2017.
 */

public class GameScreen implements Screen {
    private SpaceGame game;

    private SpriteBatch batch;
    private BitmapFont fnt;
    private Background background;
    private Player player;
    private AsteroidEmitter asteroidEmitter;
    private BulletEmitter bulletEmitter;
    private PowerUpsEmitter powerUpsEmitter;
    private ParticleEmitter particleEmitter;
    private BoomEmitter boomEmitter;
    private BotEmitter botEmitter;
    private TextureAtlas atlas;
    private Music music;
    private int level;
    private int maxLevels;
    private float timePerLevel;
    private float currentLevelTime;

    List<LevelInfo> levels;

    public LevelInfo getCurrentLevelInfo() {
        return levels.get(level - 1);
    }

    public BulletEmitter getBulletEmitter() {
        return bulletEmitter;
    }

    public ParticleEmitter getParticleEmitter() {
        return particleEmitter;
    }

    public int getLevel() {
        return level;
    }

    public void loadFullGameInfo() {
        levels = new ArrayList<LevelInfo>();
        BufferedReader br = null;
        try {
            br = Gdx.files.internal("leveldata.csv").reader(8192);
            br.readLine();
            String str;
            while ((str = br.readLine()) != null) {
                String[] arr = str.split("\\t");
                LevelInfo levelInfo = new LevelInfo(Integer.parseInt(arr[0]), Float.parseFloat(arr[1]),
                        Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Float.parseFloat(arr[4])
                        , Float.parseFloat(arr[5]));
                levels.add(levelInfo);
            }
            maxLevels = levels.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameScreen(SpaceGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
    }

    @Override
    public void show() {
        Assets.getInstance().loadAssets(ScreenType.GAME);
        atlas = Assets.getInstance().getMainAtlas();
        background = new Background(atlas.findRegion("star16"));
        fnt = Assets.getInstance().getAssetManager().get("font2.fnt", BitmapFont.class);
        player = new Player(this, atlas.findRegion("ship64"), atlas.findRegion("hpBar"), atlas.findRegion("joystick"), Assets.getInstance().getAssetManager().get("laser.wav", Sound.class), new Vector2(100, 328), new Vector2(0.0f, 0.0f), 800.0f);
        asteroidEmitter = new AsteroidEmitter(this, atlas.findRegion("asteroid64"), 20, 0.4f);
        bulletEmitter = new BulletEmitter(atlas.findRegion("bullets36"), 100);
        powerUpsEmitter = new PowerUpsEmitter(atlas.findRegion("powerUps"));
        particleEmitter = new ParticleEmitter(atlas.findRegion("star16"));
        boomEmitter = new BoomEmitter(atlas.findRegion("explosion64"), Assets.getInstance().getAssetManager().get("CollapseNorm.wav", Sound.class));
        botEmitter = new BotEmitter(this, atlas.findRegion("ufo"), 10, 1.0f);
        music = Assets.getInstance().getAssetManager().get("music.mp3", Music.class);
        music.setLooping(true);
        loadFullGameInfo();
        level = 1;
        currentLevelTime = 0.0f;
        timePerLevel = 60.0f;
        music.play();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Matrix4 m4 = new Matrix4();
        // m4.setToTranslationAndScaling(-1f, -1f, 0, 2.0f / 1280.0f, 2.0f / 720.0f, 0.0f);
        batch.setProjectionMatrix(game.getCamera().combined);
        // batch.setProjectionMatrix(m4);
        batch.begin();
        background.render(batch);
        player.render(batch);
        asteroidEmitter.render(batch);
        botEmitter.render(batch);
        bulletEmitter.render(batch);
        powerUpsEmitter.render(batch);
        boomEmitter.render(batch);
        particleEmitter.render(batch);
        player.renderHUD(batch, fnt, 20, 668);
        fnt.draw(batch, "LEVEL: " + level, 600, 680);
        batch.end();
    }

    public void updateLevel(float dt) {
        currentLevelTime += dt;
        if (currentLevelTime > timePerLevel) {
            currentLevelTime = 0.0f;
            level++;
            if (level > maxLevels) level = maxLevels;
            asteroidEmitter.setGenerationTime(getCurrentLevelInfo().getAsteroidGenerationTime());
        }
    }

    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.getMenuScreen());
        }
        updateLevel(dt);
        background.update(dt, player.getVelocity());
        player.update(dt);
        asteroidEmitter.update(dt);
        botEmitter.update(dt);
        bulletEmitter.update(dt);
        powerUpsEmitter.update(dt);
        particleEmitter.update(dt);
        checkCollision();
        boomEmitter.update(dt);
        asteroidEmitter.checkPool();
        botEmitter.checkPool();
        bulletEmitter.checkPool();
        particleEmitter.checkPool();
    }

    private final Vector2 collisionHelper = new Vector2(0, 0);

    public void checkCollision() {
        for (int i = 0; i < bulletEmitter.getActiveList().size(); i++) {
            Bullet b = bulletEmitter.getActiveList().get(i);
            if (b.isPlayersBullet()) {
                for (int j = 0; j < asteroidEmitter.getActiveList().size(); j++) {
                    Asteroid a = asteroidEmitter.getActiveList().get(j);
                    if (a.getHitArea().contains(b.getPosition())) {
                        if (a.takeDamage(1)) {
                            player.addScore(a.getHpMax() * 10);
                            powerUpsEmitter.makePower(a.getPosition().x, a.getPosition().y);
                            boomEmitter.setup(a.getPosition());
                        }
                        b.deactivate();
                        break;
                    }
                }
                for (int j = 0; j < botEmitter.getActiveList().size(); j++) {
                    Bot bot = botEmitter.getActiveList().get(j);
                    if (bot.getHitArea().contains(b.getPosition())) {
                        if (bot.takeDamage(1)) {
                            player.addScore(bot.getHpMax() * 100);
                            powerUpsEmitter.makePower(bot.getPosition().x, bot.getPosition().y);
                            boomEmitter.setup(bot.getPosition());
                        }
                        b.deactivate();
                        break;
                    }
                }
            } else {
                if (player.getHitArea().contains(b.getPosition())) {
                    player.takeDamage(5);
                    b.deactivate();
                    break;
                }
            }
        }

        for (int i = 0; i < asteroidEmitter.getActiveList().size(); i++) {
            Asteroid a = asteroidEmitter.getActiveList().get(i);
            if (player.getHitArea().overlaps(a.getHitArea())) {
                float len = player.getPosition().dst(a.getPosition());
                float interLen = (player.getHitArea().radius + a.getHitArea().radius) - len;
                collisionHelper.set(a.getPosition()).sub(player.getPosition()).nor();
                a.getPosition().mulAdd(collisionHelper, interLen);
                player.getPosition().mulAdd(collisionHelper, -interLen);
                a.getVelocity().mulAdd(collisionHelper, interLen * 4);
                player.getVelocity().mulAdd(collisionHelper, -interLen * 4);
                a.takeDamage(1);
                player.takeDamage(1);
            }
        }

        for (int i = 0; i < powerUpsEmitter.getPowerUps().length; i++) {
            PowerUp p = powerUpsEmitter.getPowerUps()[i];
            if (p.isActive()) {
                if (player.getHitArea().contains(p.getPosition())) {
                    p.use(player);
                    p.deactivate();
                }
            }
        }

        if (player.getLives() < 0) {
            game.setScreen(game.getMenuScreen());
        }
    }

    @Override
    public void resize(int width, int height) {
        game.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Assets.getInstance().clear();
    }

    @Override
    public void dispose() {
        Assets.getInstance().clear();
    }
}

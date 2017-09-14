package ru.geekbrains.stargame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import static ru.geekbrains.stargame.Asteroid.getRandomAsteroids;

public class StarGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private int srcSize;
	private int asteroidCount = 10;
	private List<Asteroid> asteroids;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("Asteroid Huge Minerals.png");
		srcSize = img.getWidth();
		// Создаём астероиды
		asteroids = getRandomAsteroids(asteroidCount);
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// Рисуем астероиды
		for (int i = 0; i < asteroidCount; i++) {
			Asteroid asteroid = asteroids.get(i);
			batch.draw(img, asteroid.x, asteroid.y, srcSize/2, srcSize/2, srcSize,srcSize,
				asteroid.scale,asteroid.scale,asteroid.angle,0,0,srcSize,srcSize,false,false);
		}
		batch.end();
	}

	public void update(float dt) {
		for (int i = 0; i < asteroidCount; i++) {
			Asteroid asteroid = asteroids.get(i);

			asteroid.x += asteroid.vx*dt;
			asteroid.y += asteroid.vy*dt;

			if (asteroid.rotateDir) {
				asteroid.angle += asteroid.omega*dt;
			} else {
				asteroid.angle -= asteroid.omega*dt;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

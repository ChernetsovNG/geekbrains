package ru.geekbrains.stargame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class StarGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private int srcSize;
	private int asteroidCount = 3;
	private List<Float> asteroidX;
	private List<Float> asteroidY;
	private List<Float> asteroidVx;
	private List<Float> asteroidVy;
	private List<Float> asteroidRot;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("Asteroid Huge Minerals.png");
		srcSize = img.getWidth();
		// Создаём 3 астероида
		asteroidX = new ArrayList<>();
		asteroidY = new ArrayList<>();
		asteroidVx = new ArrayList<>();
		asteroidVy = new ArrayList<>();
		asteroidRot = new ArrayList<>();

		asteroidX.add(100f);
		asteroidX.add(200f);
		asteroidX.add(300f);

		asteroidY.add(100f);
		asteroidY.add(200f);
		asteroidY.add(300f);

		asteroidRot.add(0f);
		asteroidRot.add(15f);
		asteroidRot.add(-17f);

		asteroidVx.add(50f);
		asteroidVx.add(150f);
		asteroidVx.add(90f);

		asteroidVy.add(20f);
		asteroidVy.add(-100f);
		asteroidVy.add(70f);
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// Рисуем 3 астероида
		batch.draw(img, asteroidX.get(0), asteroidY.get(0), srcSize/2, srcSize/2, srcSize,srcSize, 0.5f,0.5f,asteroidRot.get(0),0,0,srcSize,srcSize,false,false);
		batch.draw(img, asteroidX.get(1), asteroidY.get(1), srcSize/2, srcSize/2, srcSize,srcSize, 0.75f,0.75f,asteroidRot.get(1),0,0,srcSize,srcSize,false,false);
		batch.draw(img, asteroidX.get(2), asteroidY.get(2), srcSize/2, srcSize/2, srcSize,srcSize, 0.75f,0.75f,asteroidRot.get(2),0,0,srcSize,srcSize,false,false);
		batch.end();
	}

	public void update(float dt) {
		for (int i = 0; i < asteroidCount; i++) {
			float x = asteroidX.get(i);
			x += asteroidVx.get(i)*dt;
			asteroidX.set(i, x);

			float y = asteroidY.get(i);
			y += asteroidY.get(i)*dt;
			asteroidY.set(i, x);
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

package com.life;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JustAnotherLife extends Game {
	public SpriteBatch batch;

	//Screens
	public GameScreen gameScreen;

	//Pixels to meters conversion
	public static final float PPM = 32;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gameScreen = new GameScreen(this);

		this.setScreen(gameScreen);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose(){
		gameScreen.dispose();
		batch.dispose();
		System.out.println("JustAnotherLife Disposed");
	}
}

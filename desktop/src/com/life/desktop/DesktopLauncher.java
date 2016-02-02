package com.life.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.life.JustAnotherLife;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Just Another Life";
		//config.addIcon("icon/life.png", Files.FileType.Internal);
		new LwjglApplication(new JustAnotherLife(), config);
	}
}

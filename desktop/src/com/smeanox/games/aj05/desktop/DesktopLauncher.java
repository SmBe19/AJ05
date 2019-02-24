package com.smeanox.games.aj05.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.smeanox.games.aj05.AJ05;
import com.smeanox.games.aj05.Consts;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.title = Consts.GAME_NAME;
		config.width = Consts.WND_WIDTH;
		config.height = Consts.WND_HEIGHT;
		new LwjglApplication(new AJ05(), config);
	}
}

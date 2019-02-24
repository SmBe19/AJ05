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

		for (int i = 0; i < arg.length; i++) {
			if ("-aa".equals(arg[i])) {
				if (i + 1 < arg.length) {
					try{
						Consts.ANTIALIASING = Integer.parseInt(arg[i+1]);
					} catch (NumberFormatException e) {
						System.out.println("Could not parse '" + arg[i+1] + "'");
						return;
					}
					i++;
				} else {
					System.out.println("Missing antialiasing");
					return;
				}
			}
			if ("-mouse".equals(arg[i])) {
				if (i + 2 < arg.length) {
					try{
						Consts.MOUSE_SENSITIVITY_X = Float.parseFloat(arg[i+1]);
						Consts.MOUSE_SENSITIVITY_Y = Float.parseFloat(arg[i+2]);
					} catch (NumberFormatException e) {
						System.out.println("Could not parse '" + arg[i+1] + "' or '" + arg[i+2] + "'");
						return;
					}
					i += 2;
				} else {
					System.out.println("Missing sensitivity");
					return;
				}
			}
			if ("-resolution".equals(arg[i])) {
				if (i + 2 < arg.length) {
					try{
						config.width = Integer.parseInt(arg[i+1]);
						config.height = Integer.parseInt(arg[i+2]);
					} catch (NumberFormatException e) {
						System.out.println("Could not parse '" + arg[i+1] + "' or '" + arg[i+2] + "'");
						return;
					}
					i += 2;
				} else {
					System.out.println("Missing resolution");
					return;
				}
			}
		}

		new LwjglApplication(new AJ05(), config);
	}
}

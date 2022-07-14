package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import com.tutorial.main.Game.STATE;

public class HUD {
	public int bounds = 0;
	public static float HEALTH = 100;
	private float greenValue = 255;
	private int score = 0;
	private int level = 1;
	Handler handler;
	Random r = new Random();
	public HUD(Handler handler) {
		this.handler = handler;
	}
	public void tick() {
		HEALTH = (int) Game.clamp(HEALTH, 0, 100);
		greenValue = (int) Game.clamp(greenValue, 0, 255);
		greenValue = (HEALTH*2);
		score++;
		if(HEALTH <= 0) {
			handler.clearEnemies();
			for(int i = 0; i < handler.object.size(); i++) {
				GameObject tempObject = handler.object.get(i);
				if(tempObject.getid() == ID.Player) handler.removeObject(tempObject);
			}
			HEALTH = 100;
			for(int i = 0; i < 20; i++) {
				handler.addObject(new menuParticle(r.nextInt(Game.WIDTH), r.nextInt(Game.HEIGHT), ID.menuParticle, handler));
			}
			Game.GameState = STATE.End;
		}
	}
	public void render(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(15, 15, 200, 32);
		g.setColor(new Color(100, (int) greenValue, 0));
		g.fillRect(15, 15, (int) HEALTH*2, 32);
		g.setColor(Color.white);
		g.drawRect(15, 15, 200, 32);
		g.drawString("Score: " + score, 15, 64);
		g.drawString("Level: " + level, 15, 80);
		g.drawString("Press space for shop", 15, 96);
	}
	public void score(int score) {
		this.score = score;
	}
	public int getScore() {
		return score;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return level;
	}
}

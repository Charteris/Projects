package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Player extends GameObject {
	Random r = new Random();
	private Handler handler;
	public Player(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
	}
	public void tick() {
		x += velx;
		y += vely;
		x = Game.clamp((int) x, 0, Game.WIDTH-48);
		y = Game.clamp((int) y, 0, Game.HEIGHT-80);
		collision();
	}
	public void collision() {
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getid() == ID.BasicEnemy || tempObject.getid() == ID.FastEnemy || tempObject.getid() == ID.SmartEnemy) {
				if(getBounds().intersects(tempObject.getBounds())) HUD.HEALTH -= 2;
			}else if(tempObject.getid() == ID.BossOne) {
				if(getBounds().intersects(tempObject.getBounds())) HUD.HEALTH -= 5;
			}
		}
	}
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect((int) x, (int) y, 32, 32);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}
}

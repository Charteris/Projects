package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class BossOneBullet extends GameObject {
	private Handler handler;
	Random r = new Random();
	public BossOneBullet(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		velx = (r.nextInt(5 - -5) + -5);
		vely = 5;
	}
	public void tick() {
		x += velx;
		y += vely;
		//if(y <= 0 || y >= Game.HEIGHT-64) vely *= -1;
		//if(x <= 0 || x >= Game.WIDTH-32) velx *= -1;
		if(y >= Game.HEIGHT) handler.removeObject(this);
		handler.addObject(new Trail(x, y, ID.Trail, Color.orange, 16, 16, (float) 0.05, handler));
	}
	public void render(Graphics g) {
		g.setColor(Color.orange);
		g.fillRect((int) x, (int) y, 16, 16);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 16, 16);
	}
}

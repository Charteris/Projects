package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class FastEnemy extends GameObject {
	private Handler handler;
	public FastEnemy(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		velx = 2;
		vely = 9;
	}
	public void tick() {
		x += velx;
		y += vely;
		if(y <= 0 || y >= Game.HEIGHT-64) vely *= -1;
		if(x <= 0 || x >= Game.WIDTH-32) velx *= -1;
		handler.addObject(new Trail(x, y, ID.Trail, Color.cyan, 16, 16, (float) 0.05, handler));
	}
	public void render(Graphics g) {
		g.setColor(Color.cyan);
		g.fillRect((int) x, (int) y, 16, 16);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 16, 16);
	}
}

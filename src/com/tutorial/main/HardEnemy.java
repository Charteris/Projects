package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class HardEnemy extends GameObject {
	private Handler handler;
	private Random r = new Random();
	public HardEnemy(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		velx = 5;
		vely = 5;
	}
	public void tick() {
		x += velx;
		y += vely;
		if(y <= 0 || y >= Game.HEIGHT-64) {
			if(vely < 0) {
				vely = -(r.nextInt(7) + 1) * -1;
			}else {
				vely = (r.nextInt(7) + 1) * -1;
			}
		}
		if(x <= 0 || x >= Game.WIDTH-32) {
			if(velx < 0) {
				velx = -(r.nextInt(7) + 1) * -1;
			}else {
				velx = (r.nextInt(7) + 1) * -1;
			}
		}
		handler.addObject(new Trail(x, y, ID.Trail, Color.yellow, 16, 16, (float) 0.05, handler));
	}
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillRect((int) x, (int) y, 16, 16);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 16, 16);
	}
}

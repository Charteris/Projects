package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class BossOne extends GameObject {
	private Handler handler;
	private int timer = 50;
	private int timer2 = 50;
	Random r = new Random();
	public BossOne(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		velx = 0;
		vely = 2;
	}
	public void tick() {
		x += velx;
		y += vely;
		if(timer <= 0) {
			vely = 0;
		}else {
			timer--;
		}
		if(timer <= 0) timer2--;
		if(timer2 <= 0) {
			if(velx == 0) velx = 2;
			int spawn = r.nextInt(10);
			if(spawn == 0) handler.addObject(new BossOneBullet((int) x + 48, (int) y + 48, ID.BasicEnemy, handler));
			if(velx < 0) {
				velx += -0.01;
			}else if(velx > 0) {
				velx += 0.01;
			}
		}
		if(x <= 0 || x >= Game.WIDTH-108) velx *= -1;
	}
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect((int) x, (int) y, 96, 96);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 96, 96);
	}
}

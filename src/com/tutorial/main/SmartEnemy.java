package com.tutorial.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class SmartEnemy extends GameObject {
	private Handler handler;
	private GameObject player;
	public SmartEnemy(int x, int y, ID id, Handler handler) {
		super(x, y, id);
		this.handler = handler;
		for(int i = 0; i < handler.object.size(); i++) {
			if(handler.object.get(i).getid() == ID.Player) player = handler.object.get(i);
		}
		velx = 5;
		vely = 5;
	}
	public void tick() {
		x += velx;
		y += vely;
		float diffx = x - player.getx() - 8;
		float diffy = y - player.gety() - 8;
		float distance = (float) Math.sqrt(((x - player.getx()) * (x - player.getx())) + ((y - player.gety()) * (y - player.gety())));
		velx = (float) ((-1.0/distance) * diffx);
		vely = (float) ((-1.0/distance) * diffy);
		if(y <= 0 || y >= Game.HEIGHT-64) vely *= -1;
		if(x <= 0 || x >= Game.WIDTH-32) velx *= -1;
		handler.addObject(new Trail(x, y, ID.Trail, Color.green, 16, 16, (float) 0.05, handler));
	}
	public void render(Graphics g) {
		g.setColor(Color.green);
		g.fillRect((int) x, (int) y, 16, 16);
	}
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 16, 16);
	}
}
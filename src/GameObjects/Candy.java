package GameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;

public class Candy extends GameObject {
	
	long killTimer = System.currentTimeMillis();
	int killTime = 1000;
	int r = (int) (50 + Math.random() * 150), g = (int) (50 + Math.random() * 150), b = (int) (50 + Math.random() * 150);
	Color col = new Color(r, g, b);
	public float velx = 0, vely = 0, rotation = 0;
	
	public Candy(int x, int y, float dx, float dy, Handler handler, ID id) {
		super(x, y, id, handler);
		
		width = 8;
		height = 4;

		float tx = dx - x;
		float ty = dy - y;
		float dis = (int) Math.hypot(tx,  ty);
		rotation = (float) Math.atan2(ty, tx);
		velx = 10 * tx / dis;
		vely = 10 * ty / dis;
				
	}

	public void tick() {
		x += velx;
		y += vely;
		
		if(System.currentTimeMillis() - killTimer >= killTime)
			handler.removeObject(this);
		collision();
	}
	
	public void collision() {
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject temp = handler.object.get(i);
			
			if(temp != null && temp.id == ID.Wall) {
				//ricochet off always and generate sound
				
			}
		}
	}

	public void render(Graphics2D g) {
		AffineTransform old = g.getTransform();
		g.rotate(rotation, x, y);
		g.setColor(col.brighter());
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		g.setTransform(old);
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), width, height);
	}

}

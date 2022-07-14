package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class Visionary extends GameObject {

	public int shield = 1000, regen = 2000, barW = 4, barH = 12;
	public long shielding = (long) shield, regenTimer = (long) regen;
	
	public Visionary(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 16;
		height = 24;
		rotation = 0;
		
		name = "Visionary";
		
		enemy = true;
		maxhp = 200;
		hp = maxhp;
		dmg = 5;
		range = Game.HEIGHT / 2;
		score = 100;
		
		spd = 0;
		dodge = 5;
		rotation = 0;
		
		color = new Color(163, 119, 237);
	}

	public void tick() {

		targetEnemy();
		if(target != null) {
			if(System.currentTimeMillis() - shielding < shield) {
				regenTimer = System.currentTimeMillis();
				
			}else if(System.currentTimeMillis() - regenTimer < regen) {
				shielding = System.currentTimeMillis();
				rotation = (float) Math.atan2(target.y - y, target.x - x);
			}
			
			if(System.currentTimeMillis() - shielding >= shield) {
				Line2D barrier = new Line2D.Float(0, 0, 0, 0);
			}
		}
		
		//create particles
		int xx = (int) ((x - width / 2) + (Math.random() * width));
		int yy = (int) ((y + height / 2) - (Math.random() * (height / 2)));
		system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy + 1, (float) 0.1, (int) 4, 2, color, system, handler, false));
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.PI / 4, x, y - height / 2 + width / 2);
		g.setColor(color.darker());
		g.fillRect((int) (x - width / 4), (int) (y - height / 2 + width / 4), (int) width / 2, (int) width / 2);
		g2d.setTransform(old);
		
		if(System.currentTimeMillis() - shielding <= shield) {
			g2d.rotate(rotation, x, y);
			g.fillRect((int) (x + width), (int) (y - barH / 2), barW, barH);
			g2d.setTransform(old);
		}
		
		drawHealth(g);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

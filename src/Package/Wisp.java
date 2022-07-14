package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Wisp extends GameObject {

	public Wisp(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 12;
		height = 12;
		rotation = 0;
		
		name = "Wisp";
		
		enemy = true;
		maxhp = 50;
		hp = maxhp;
		dmg = 5;
		range = Game.HEIGHT / 4;
		score = 100;
		explodes = true;
		
		spd = 3;
		dodge = 5;
		rotation = 0;
		
		color = Color.magenta.darker();
	}

	public void tick() {

		//track player
		x += velx;
		y += vely;
		
		targetEnemy();
		if(target != null) {
			rotation = (float) Math.atan2(y - target.y, x - target.x);
			
			if(target.x != x || target.y != y) {
				float dx = target.x - x;
				float dy = target.y - y;
		
				float dis = (int) Math.hypot(dx,  dy);
				velx = spd * dx / dis;
				vely = spd * dy / dis;
			
			}else {
				x = target.x;
				y = target.y;
			}
		}

		if(velx != 0 || vely != 0) {//create particles
			int xx = (int) ((x - width / 2) + (Math.random() * width));
			int yy = (int) ((y - height / 2) + (Math.random() * (height / 2)));
			system.addPart(new Pixel(xx, yy, rotation, xx - velx, yy - vely, (float) 0.1, (int) width / 3, 2, color, system, handler, false));
			
		}else {
			//create particles
			int xx = (int) ((x - width / 2) + (Math.random() * width));
			int yy = (int) ((y - height / 2) + (Math.random() * (height / 2)));
			system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy - 1, (float) 0.1, (int) 4, -2, color, system, handler, false));
		}
		
		Collision();

	}

	public void Collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {

					int r = (int) (5 + Math.random() * 5);
					for(int n = 0; n < r; n ++) {
						int xx = (int) (x - 12 + (Math.random() * 24));
						int yy = (int) (y - 12 + (Math.random() * 24));
						system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx - velx, (int) yy - vely, (float) 0.2, 4, -2, color, system, handler, true));
					}
					
				}
				
			}else if(tempObject != null && tempObject.getid() == ID.Player) {

				if(getBounds().intersects(tempObject.getBounds())) {

					int r = (int) (5 + Math.random() * 5);
					for(int n = 0; n < r; n ++) {
						int xx = (int) (x - 12 + (Math.random() * 24));
						int yy = (int) (y - 12 + (Math.random() * 24));
						system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx - velx, (int) yy - vely, (float) 0.2, 4, -2, Color.yellow.brighter(), system, handler, true));
					}
					
					tempObject.hp -= dmg;
					Game.sound.playSound("\\Hurt.wav");
					handler.addObject(new Explosion((int) x, (int) y, 32, 32, ID.Explosion, handler, system));
					handler.removeObject(this);
					
				}
				
			}
			
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		AffineTransform old = g2d.getTransform();
		g2d.rotate(rotation, x, y);
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		g2d.setTransform(old);
		
		drawHealth(g);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

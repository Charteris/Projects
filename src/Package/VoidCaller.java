package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class VoidCaller extends GameObject {

	public float yFac = 0;
	public boolean invert = false;
	
	public int fireTime = 1000;
	public long fireTimer = (long) fireTime;
	
	public VoidCaller(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 16;
		height = 24;
		rotation = 0;
		
		name = "Void Caller";
		
		enemy = true;
		maxhp = 200;
		hp = maxhp;
		dmg = 5;
		range = Game.HEIGHT / 2;
		score = 100;
		
		spd = 0;
		dodge = 5;
		rotation = 0;
		
		color = new Color(140, 22, 183);
	}

	public void tick() {
		//teleport if enemies too close
		targetEnemy();
		if(target != null) {
			rotation = (float) Math.atan2(target.y - y, target.x - x);
			
			if(target.getBounds().intersects(voidBounds())) {
				//inverts location
				float dis = x - Game.WIDTH / 2;
				x = Game.WIDTH / 2 + dis * -1;
				
				//create projectile nova
				int i = 0;
				float step = (float) (360 / 20);
				
				while(i < 20) {
					
					float rot = step * i;

					//calculate radial directions
					int _x = (int) (x + Math.cos(rot) * width / 2);
					int _y = (int) (y + Math.sin(rot) * height / 2);
					float tx = (float) (x + Math.cos(rot) * width * 8);
					float ty = (float) (y + Math.sin(rot) * height * 8);
					
					handler.addObject(new Projectile(_x, _y, tx, ty, (float) dmg, (float) width / 2, (float) 128, 500, color, 
							ID.Projectile, handler, system, null, enemy, ID.VoidCaller));
					i ++;
				}
			}
			
			//create arc of projectiles
			if(System.currentTimeMillis() - fireTimer >= fireTime) {
				for(int i = 0; i < 5; i ++) {
					float rot = (float) (Math.toDegrees(rotation) - 10 + 4 * i);
					int xx = (int) (x + Math.cos(Math.toRadians(rot)) * (width * 1.5));
					int yy = (int) (y + Math.sin(Math.toRadians(rot)) * (height * 1.5));
					
					handler.addObject(new Projectile(xx, yy, target.x, target.y, (float) dmg, (float) width / 2, (float) 128, 500, color, 
							ID.Projectile, handler, system, null, enemy, ID.VoidCaller));
				}
				
				fireTimer = System.currentTimeMillis();
			}
		}
		
		//create levitation effect
		if(yFac >= 2) invert = true;
		else if(yFac <= 0) invert = false;
		
		if(invert) {
			y -= 0.01f;
			yFac -= 0.01f;
		}else {
			y += 0.01f;
			yFac += 0.01f;			
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
		
		//direction indicator
		AffineTransform old = g2d.getTransform();
		
		int _x = (int) (x + Math.cos(rotation) * width);
		int _y = (int) (y + Math.sin(rotation) * height);
		
		g2d.rotate(Math.PI / 4, _x, _y);
		g.setColor(color);
		g.fillRect((int) (_x - width / 4), (int) (_y - width / 4), (int) width / 2, (int) width / 2);
		g2d.setTransform(old);
		
		drawHealth(g);
		
	}

	public Rectangle voidBounds() {
		return new Rectangle((int) (x - width * 4), (int) (y - height * 4), (int) (width * 8), (int) (height * 8));
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

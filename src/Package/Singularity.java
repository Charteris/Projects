package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Singularity extends GameObject{

	private long timer = System.currentTimeMillis();
	private int time = (int) (1000 + Math.random() * 4000), capacity = 0;
	public Player player = null;
	
	public Singularity(int x, int y, Player player, boolean enemy, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.enemy = enemy;
		this.player = player;
		
		width = 10;
		height = 10;
		range = width * 10;
		dodge = 100;
		
		timer = System.currentTimeMillis();
		
		color = Color.black;
	}

	public void tick() {
		rotation += 0.2f;

		if(Chance(50)) {
			//create particles
			int xx = (int) (x - width + Math.random() * (width * 2));
			int yy = (int) (y - height + Math.random() * (height * 2));
			float life = (float) (Math.random() * 0.8);
			system.addPart(new Pixel(xx, yy, (float) yy, (float) xx, rotation, (float) 0.1, (int) 4, -2, color, system, handler, false));
		}
			
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.enemy != enemy && tempObject.getid() != ID.Powerup && tempObject.getid() != ID.Wall
					&& tempObject.getid() != ID.Portal && !tempObject.boss && tempObject != this) {
				//drag into void
				float dx = x - tempObject.x;
				float dy = y - tempObject.y;
				float d = (float) Math.hypot(dx, dy);
				
				if(d <= range) {
					tempObject.x += (2 * (1 + range / d)) * dx / d;
					tempObject.y += (2 * (1 + range / d)) * dy / d;
				}
				
				//destroy upon contact
				if(getBounds().intersects(tempObject.getBounds())) {
					if(player != null) {
						player.points += tempObject.score;
						player.totalP += tempObject.score;
					}
					
					handler.removeObject(tempObject);
					
					if(tempObject.id == ID.Firefly || tempObject.id == ID.Projectile) {
						capacity ++;
						
					}else {
						capacity = 5;
						int xx = (int) ((x - width) + (Math.random() * (width * 2)));
						int yy = (int) ((y - height) + (Math.random() * (height * 2)));
						handler.addObject(new Explosion(xx, yy, 32, 32, ID.Explosion, handler, system));
						
						handler.removeObject(this);
					}
				}
			}
		}

		//destroy singularity
		if(System.currentTimeMillis() - timer >= time || capacity >= 5) {
			int xx = (int) ((x - width) + (Math.random() * (width * 2)));
			int yy = (int) ((y - height) + (Math.random() * (height * 2)));
			handler.addObject(new Explosion(xx, yy, 32, 32, ID.Explosion, handler, system));
			
			handler.removeObject(this);
		}
		
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		AffineTransform old = g2d.getTransform();
		g2d.rotate(rotation, x, y);
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		g2d.setTransform(old);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

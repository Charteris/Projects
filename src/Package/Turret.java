package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Turret extends GameObject {

	private long fireTimer = System.currentTimeMillis();
	private int fireTime = 750;

	private long Timer = System.currentTimeMillis();
	private int time = 200;
	
	public Turret(int x, int y, int time, float rotation, boolean enemy, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.time = time;
		this.enemy = enemy;
		
		name = "Turret (Basic)";
		
		maxhp = 80;
		hp = maxhp;
		dmg = 2;
		range = Game.WIDTH;
		score = 25;
		explodes = true;
		dodge = 0;
		
		height = 20;
		width = 25;
		color = new Color(135, 144, 178);
			
		this.rotation = (float) Math.toRadians(rotation - 180);
		
	}

	public void tick() {
		
		if(System.currentTimeMillis() - fireTimer >= fireTime) {
			
			//System.out.println("x (" + (float) (Math.cos(rotation)) + "), y (" + (float) (Math.sin(rotation)) + ")");
			handler.addObject(new Projectile((int) x, (int) y, (float) (x + Math.cos(rotation)), (float) (y + Math.sin(rotation)), 
					dmg, width / 3, 0, range, color, ID.Projectile, handler, system, null, enemy, id));
			
			fireTimer = System.currentTimeMillis();
		}

		if(hp >= maxhp) hp = maxhp;

		//destroy wall if not permanent
		if(time != 0) {
			
			if(System.currentTimeMillis() - Timer >= time) {
				handler.addObject(new Explosion((int) x, (int) y, 32, 32, ID.Explosion, handler, system));
				handler.removeObject(this);
			}
			
		}
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(rotation, x, y);
		g.setColor(color);
		g.fillRect((int) (x - height / 2), (int) (y - height / 2), (int) height, (int) height);
		g.fillRect((int) (x + height / 2), (int) (y - height / 4), (int) (width - height), (int) (height / 2));
		g2d.setTransform(old);
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - height / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

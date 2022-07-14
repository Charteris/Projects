package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class MagicTurret extends GameObject {

	private long fireTimer = System.currentTimeMillis();
	private int fireTime = 1000;

	private long Timer = System.currentTimeMillis();
	private int time = 100;
	
	public MagicTurret(int x, int y, int time, boolean enemy, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.time = time;
		this.enemy = enemy;

		name = "Turret (Totem)";
		
		maxhp = 100;
		hp = maxhp;
		dmg = 3;
		range = Game.HEIGHT / 3 * 2;
		score = 50;
		dodge = 0;
		
		width = 16;
		height = width * 2;
		
		color = new Color(160, 78, 11);
		
	}

	public void tick() {

		//fire at target
		targetEnemy();
		if(target != null && System.currentTimeMillis() - fireTimer >= fireTime) {
				
			handler.addObject(new Projectile((int) x, (int) (y - height / 4), (float) target.x, (float) target.y, dmg, width / 2, (float) 64, range, color,
					ID.Projectile, handler, system, null, enemy, id));
			
			fireTimer = System.currentTimeMillis();
		}

		if(hp >= maxhp) hp = maxhp;

		move();
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.PI / 4, x - width, y - height / 4);
		g.fillRect((int) (x - width), (int) (y - height), (int) width, (int) (height / 2));
		g2d.setTransform(old);
		
		g.fillRect((int) (x - width / 2), (int) y, (int) width, (int) height / 2);
		
		drawHealth(g);

	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

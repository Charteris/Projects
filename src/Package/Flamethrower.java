package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Flamethrower extends GameObject{

	private long Timer = System.currentTimeMillis();
	private int time = 150;
	
	public Flamethrower(int x, int y, int time, boolean enemy, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.time = time;
		this.enemy = enemy;

		name = "Flamethrower";
		
		maxhp = 100;
		hp = maxhp;
		dmg = 4;
		range = Game.HEIGHT / 3;
		score = 60;
		explodes = true;
		dodge = 0;
		
		height = 20;
		width = 25;
		color = new Color(219, 194, 184);
			
		this.rotation = 0;
	}

	public void tick() {
		
		targetEnemy();
		if(target != null) {
			//slowly rotate towards target
			float rot = (float) Math.atan2(target.y - y, target.x - x);
			if(rotation < rot) rotation += 0.05f;
			else if(rotation > rot) rotation -= 0.05f;
			
			//fire
			if(Chance(40)) {
				handler.addObject(new Projectile((int) x, (int) y, (float) (x + Math.cos(rotation)), (float) (y + Math.sin(rotation)), dmg, width / 3, 
						0, range, color, ID.Projectile, handler, system, null, enemy, id));
			}
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

		g.setColor(color.darker());
		g.fillRect((int) (x - height / 3), (int) (y - height / 3), (int) (height / 1.5), (int) (height / 1.5));
		g.fillRect((int) (x - height / 2), (int) (y - height / 2), (int) (height / 3), (int) (height / 3));
		g.fillRect((int) (x + height / 2 - height / 3), (int) (y - height / 2), (int) (height / 3), (int) (height / 3));
		g.fillRect((int) (x - height / 2), (int) (y + height / 2 - height / 3), (int) (height / 3), (int) (height / 3));
		g.fillRect((int) (x + height / 2 - height / 3), (int) (y + height / 2 - height / 3), (int) (height / 3), (int) (height / 3));
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(rotation, x, y);
		g.fillRect((int) (x + height / 3), (int) (y - height / 4), (int) (width - height) * 2, (int) (height / 2));
		g2d.setTransform(old);
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - height / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

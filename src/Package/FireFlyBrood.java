package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class FireFlyBrood extends GameObject{

	public int randTime = (int) (Math.random() * 1800) + 200, fireUpTime = 500;
	public long randTimer = (long) randTime, fireUpTimer = (long) fireUpTime;
	public int spd = 2;
	public double mx = 0, my = 0;
	public int r, g, b;
	
	public boolean fire = false;
	
	public FireFlyBrood(int x, int y, ID id, Handler handler, PartSystem system, boolean enemy) {
		super(x, y, id, handler, system);
		this.enemy = enemy;
		
		width = 16;
		height = 16;
		rotation = 0;
		
		name = "Firefly Brood";
		
		enemy = true;
		maxhp = 75;
		hp = maxhp;
		dmg = 0.5f;
		range = 240;
		score = 20;
		
		dodge = 5;
		
		color = Color.yellow.brighter();
		
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
		
	}

	public void tick() {
		
		//fire at target
		targetEnemy();
		if(fire) {
			if(target != null) {
				
				if(Chance(30)) {
					float xx = (float) (x - width / 2 + Math.random() * width), yy = (float) (y - height / 2 + Math.random() * height);
					handler.addObject(new Projectile((int) x, (int) y, xx, yy, dmg, width / 3, (float) 64, range, new Color(r, 200, b),
							ID.Projectile, handler, system, null, enemy, id));
				}
			}
		}
		
		if(!fire) {
			
			if(target != null && System.currentTimeMillis() - fireUpTimer >= fireUpTime) {
				g --;
				
				if(g <= 50) {
					g = 50;
					fire = true;
				}
			}
		}else {
			
			g ++;
			
			if(g >= 200) {
				g = 200;
				fireUpTimer = System.currentTimeMillis();
				fire = false;
			}
		}
		
		color = new Color(r, g, b);
		
		if(hp >= maxhp) hp = maxhp;
		//shoot twice as fast below half health
		if(hp <= maxhp / 2) fireUpTime = 250;
		
		x += velx;
		y += vely;
		
		if(velx != 0 || vely != 0) {
			system.addPart(new Pixel((int) x, (int) y, rotation, (int) x, (int) y, (float) 0.2, (int) width, 1, color, system, handler, true));
		}
		
		//randomise movement
		if(System.currentTimeMillis() - randTimer >= randTime) {
			
			if(velx == 0 && vely == 0) {
				velx = (float) (Math.random() * spd) - (spd / 2);
				vely = (float) (Math.random() * spd) - (spd / 2);
				
			}else {
				velx = 0;
				vely = 0;
				
			}

			randTime = (int) (Math.random() * 1800) + 200;
		}
		
		Collision();

	}
	
	public void Collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = null;
			if(i < handler.object.size()) tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {

					x -= velx;
					y -= vely;
					
				}
				
			}
			
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		drawHealth(g);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

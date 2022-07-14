package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;

public class FireFlyQueen extends GameObject{

	public int randTime = (int) (Math.random() * 1800) + 200, spawnTime = 1500, fireTime = 1000, auraTime = 1200;
	public long randTimer = (long) randTime, spawnTimer = (long) spawnTime, fireTimer = (long) fireTime, auraTimer = (long) auraTime;
	public int spd = 2;
	public double mx = 0, my = 0;
	
	public boolean fire = false;
	
	public FireFlyQueen(int x, int y, ID id, Handler handler, PartSystem system, boolean enemy) {
		super(x, y, id, handler, system);
		this.system = system;
		this.enemy = enemy;
		
		name = "Firefly Queen";
		
		width = 32;
		height = 32;
		rotation = 0;
		
		enemy = true;
		maxhp = 500;
		hp = maxhp;
		dmg = 10;
		range = Game.HEIGHT / 3 * 2;
		score = 100;
		boss = true;
		
		dodge = 5;
		
		color = new Color(255, 156, 0);
		
	}

	public void tick() {
		
		if(dead) {
			bossDeath();
		
		}else {
			
			//fire at target
			targetEnemy();
			if(System.currentTimeMillis() - fireTimer >= fireTime) {
	
				//shoot at target
				if(target != null) {
					handler.addObject(new Projectile((int) x, (int) y, (float) target.x, (float) target.y, dmg, width / 2, 0, range, color,
							ID.Projectile, handler, system, null, enemy, id));
				}
				
				fireTimer = System.currentTimeMillis();
			}
	
			//SPECIAL EFFECT - Fire aura
			if(System.currentTimeMillis() - auraTimer >= auraTime) {
	
				//emit fire aura	
				for(int i = 0; i < 2 + (Math.random() * 4); i ++) {
					int rot = (int) (Math.random() * 360);
					handler.addObject(new Projectile((int) x, (int) y, (float) (x + Math.cos(rot)), (float) (y + Math.sin(rot)), dmg, width / 3, (float) 32, 
							range / 8, color, ID.Projectile, handler, system, null, enemy, ID.Firefly));
				}
				
				if(Chance(10)) auraTimer = System.currentTimeMillis();
			}
			
			//SPECIAL EFFECT - Spawn fireflies
			if(System.currentTimeMillis() - spawnTimer >= spawnTime) {

				//spawn fireflies
				for(int i = 0; i < (Math.random() * 4); i ++) {
					int _x = (int) (x - width * 2 + (Math.random() * (width * 4)));
					int _y = (int) (y - height * 2 + (Math.random() * (height * 4)));
						
					if(Chance(95)) handler.addObject(new FireFlies(_x, _y, ID.Firefly, handler, system, true));
					else if(Chance(95)) handler.addObject(new FireFlyBrood(_x, _y, ID.Firefly, handler, system, true));
					else handler.addObject(new FireFlySpawn(_x, _y, ID.Firefly, handler, system, true));
				}
				
				spawnTimer = System.currentTimeMillis();
			}
				
			if(hp >= maxhp) hp = maxhp;
			
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

	}
	
	public void Collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {

					x -= velx;
					y -= vely;
					
				}

			//SPECIAL EFFECTS - Sacrifice fireflies (health restoration)
			}else if(tempObject != null && tempObject.getid() == ID.Firefly) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					
					hp += tempObject.hp;
					handler.removeObject(tempObject);
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

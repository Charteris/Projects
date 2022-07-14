package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;

public class FireFlies extends GameObject{

	public int randTime = (int) (Math.random() * 1800) + 200, attackTime = 1000;
	public long randTimer = (long) randTime, atkTimer = System.currentTimeMillis();
	public double mx = 0, my = 0;
	public boolean targeting = false;
	
	public FireFlies(int x, int y, ID id, Handler handler, PartSystem system, boolean enemy) {
		super(x, y, id, handler, system);
		this.system = system;
		this.enemy = enemy;
		
		name = "Firefly";
		
		width = 5;
		height = 5;
		rotation = 0;

		enemy = true;
		maxhp = 2;
		hp = maxhp;
		score = 2;
		spd = 0.75f;
		range = Game.HEIGHT / 2;
		
		dodge = 2;
		
		color = Color.yellow.brighter();
		
		if(Chance(50)) {
			targeting = true;
		}
		
		if(Chance(2)) {
			color = Color.yellow.darker();
			width = 6;
			height = 6;
			score *= 5;
		}
		
	}

	public void tick() {
		
		if(targeting) {
			targetEnemy();
			if(target != null) {
				
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
	
	public void Collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {

					int r = (int) (10 + Math.random() * 10);
					for(int n = 0; n < r; n ++) {
						int xx = (int) (x - 32 + (Math.random() * 64));
						int yy = (int) (y - 32 + (Math.random() * 64));
						system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx - velx, (int) yy - vely, (float) 0.2, 4, -2, Color.yellow.brighter(), system, handler, true));
					}
					
					Game.sound.playSound("/Hurt.wav");
					handler.removeObject(this);
					
				}
				
			}else if(tempObject != null && tempObject.getid() == ID.Player) {

				if(getBounds().intersects(tempObject.getBounds())) {

					int r = (int) (10 + Math.random() * 10);
					for(int n = 0; n < r; n ++) {
						int xx = (int) (x - 32 + (Math.random() * 64));
						int yy = (int) (y - 32 + (Math.random() * 64));
						system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx - velx, (int) yy - vely, (float) 0.2, 4, -2, Color.yellow.brighter(), system, handler, true));
					}
					
					tempObject.hp --;
					Game.sound.playSound("/Hurt.wav");
					handler.removeObject(this);
					
				}
				
			}
			
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}


}

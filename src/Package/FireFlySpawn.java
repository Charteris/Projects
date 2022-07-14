package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class FireFlySpawn extends GameObject{

	public int randTime = (int) (Math.random() * 1800) + 200, chargeTime = 250, recoverTime = 1000;
	public long randTimer = (long) randTime, chargeTimer = (long) chargeTime, recoverTimer = (long) recoverTime;
	public double mx = 0, my = 0;
	public int r, g, b;
	public boolean charging = false, recover = false;
	
	public FireFlySpawn(int x, int y, ID id, Handler handler, PartSystem system, boolean enemy) {
		super(x, y, id, handler, system);
		this.enemy = enemy;
		
		width = 16;
		height = 16;
		rotation = 0;
		
		name = "Firefly Spawn";
		
		enemy = true;
		maxhp = 50;
		hp = maxhp;
		dmg = 3;
		range = 240;
		spd = (float) 1.5;
		score = 15;
		
		dodge = 1;
		
		color = new Color(214, 153, 113);
	}

	public void tick() {
		
		//move towards target
		targetEnemy();
		if(target != null) {
			float dx = target.x - x;
			float dy = target.y - y;
			float dis = (float) Math.hypot(dy, dx);
			
			if(System.currentTimeMillis() - recoverTimer >= recoverTime) {
				//charge at enemy
				if(target.getBounds().intersects(chargeBounds())) {
					velx = (spd * 5) *  dx / dis;
					vely = (spd * 5) * dy / dis;
					charging = true;
					chargeTimer = System.currentTimeMillis();
					recoverTimer = System.currentTimeMillis();
								
				}else {
					velx = spd * dx / dis;
					vely = spd * dy / dis;
						
				}
			}else if(System.currentTimeMillis() - chargeTimer >= chargeTime) {
				velx = 0;
				vely = 0;
			}
				
		}else {

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
			
		}
				
		x += velx;
		y += vely;
		
		if(velx != 0 || vely != 0) {
			system.addPart(new Pixel((int) x, (int) y, rotation, (int) x, (int) y, (float) 0.2, (int) width, 1, color, system, handler, true));
		}
		
		Collision();

	}
	
	public void Collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {

					x -= velx;
					y -= vely;
					
				}
				
			}else if(tempObject != null && tempObject.enemy != enemy && tempObject.id == ID.Player) {
				
				if(getBounds().intersects(tempObject.getBounds()) && charging) {
					float dx = tempObject.x - x;
					float dy = tempObject.y - y;
					float dis = (int) Math.hypot(dx,  dy);
						
					tempObject.x += dis;
					tempObject.y += dis;
						
					tempObject.hp -= dmg;
					
					charging = false;
					recoverTimer = System.currentTimeMillis();
					recover = true;
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

	public Rectangle chargeBounds() {
		return new Rectangle((int) (x - range / 2), (int) (y - range / 2), (int) (range), (int) (range));
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

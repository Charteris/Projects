package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Charger extends GameObject{

	public int delayTime = 1000, chargeTime = 750, moveTime = (int) (200 + Math.random() * 1000);
	public long delayTimer = System.currentTimeMillis(), chargeTimer = System.currentTimeMillis(), moveTimer = System.currentTimeMillis();
	public boolean charge = false;
	public float prevHp;
	
	public Charger(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		
		color = new Color(188, 128, 83);
		grounded = false;
		
		enemy = true;
		maxhp = 100;
		hp = maxhp;
		prevHp = hp;
		dmg = 5;
		range = Game.HEIGHT;
		score = 75;
		
		dodge = 5;
		spd = 5;
		
		width = 24;
		height = 24;
	}

	public void tick() {
		
		x += velx;
		y += vely;

		targetEnemy();
		if(!charge && System.currentTimeMillis() - delayTimer >= delayTime) {
			chargeTimer = System.currentTimeMillis();
			charge = true;

			if(target != null) {
				if(target.x > x) {
					velx = spd;
				}else {
					velx = -spd;
				}
			}
			
		}
		
		if(charge && System.currentTimeMillis() - chargeTimer >= chargeTime) {
			delayTimer = System.currentTimeMillis();
			charge = false;
			velx = 0;
		}
		
		//charge at enemy
		if(charge && target != null) {
			
			collision();
			
			if(prevHp != hp) {
				dmgTimer = System.currentTimeMillis() + dmgTimer;
				hp = prevHp;
			}
			
			//create particles
			int xx = (int) (x - ((width / 2) * (velx / spd)) );
			int yy = (int) (y + height / 2);
			float xFac = (float) (Math.random() * width), yFac = (float) (Math.random() * height);
			system.addPart(new Pixel(xx, yy, rotation, (int) (xx - (xFac * (velx / spd))), (int) (yy - yFac), (float) 0.1, (int) 4, -2, color, system, handler, false));
			
		//roam
		}else if(target == null){
			
			if(System.currentTimeMillis() - moveTimer >= moveTime) {
				if(velx == 0) {
					velx = (float) (Math.random() * (spd * 2) - spd);
				}else {
					velx = 0;
				}
				
				moveTime = (int) (200 + Math.random() * 1000);
				moveTimer = System.currentTimeMillis();
			}
			
		}else if(!charge) {
			prevHp = hp;
		}
		
		move();
	}

	public void collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.id == ID.Player) {
				if(getBounds().intersects(tempObject.getBounds())) {
					
					//apply damage and kockback
					tempObject.hp -= dmg;
					tempObject.velx = velx * 2;
					tempObject.vely = (float) (-Math.random() * tempObject.spd);
					
					tempObject.stun = true;
					tempObject.stunTimer = System.currentTimeMillis();
				}
				
			//collide with walls
			}else if(tempObject.id == ID.Wall) {
				if(getBounds().intersects(tempObject.getBounds()) && y > tempObject.y) {
					
					x += (velx * -1);
					
					delayTimer = System.currentTimeMillis();
					charge = false;
					velx = 0;
					
				}else if(getBounds().intersects(tempObject.getBounds())) {
					x += (velx * -1);
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

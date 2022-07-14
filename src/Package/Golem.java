package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Golem extends GameObject {

	public ArrayList<Wall> orbitals = new ArrayList<Wall>();
	public float rotFac = 0.02f;
	public int revolveTime = 1000;
	public long revolveTimer = (long) revolveTime;
	
	public Golem(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 16;
		height = 16;
		rotation = 0;
		
		name = "Golem";
		
		enemy = true;
		maxhp = 150;
		hp = maxhp;
		dmg = 7;
		range = Game.HEIGHT / 2;
		score = 100;
		explodes = true;
		
		spd = 1;
		dodge = 5;
		
		color = new Color(188, 128, 83);

		//define orbital walls
		for(int i = 0; i < 3; i ++) {
			float rot = (float) ((Math.PI * 2) / 3 * i - Math.PI / 2);
			
			orbitals.add(new Wall((int) (x + Math.cos(rot) * (width * 2)), (int) (y + Math.sin(rot) * (height * 2)), (int) (width * 1.5), 
					(int) (height * 1.5), 0, ID.Wall, handler, system));
			orbitals.get(i).color = color;
		}
	}
	
	public void update() {
		collision();
		rotation += rotFac;
		
		for(int i = 0; i < orbitals.size(); i ++) {
			float rot = (float) ((Math.PI * 2) / orbitals.size() * i - Math.PI / 2) + rotation;
			
			orbitals.get(i).x = (int) (x + Math.cos(rot) * (width * 2));
			orbitals.get(i).y = (int) (y + Math.sin(rot) * (height * 2));
			
			orbitals.get(i).tick();
		}
	}

	public void tick() {

		//periodically increase revolution speed
		if(Chance(1)) revolveTimer = System.currentTimeMillis();
		if(System.currentTimeMillis() - revolveTimer <= revolveTime) {
			rotFac = 0.5f;
		}else {
			rotFac = 0.02f;
		}
		
		//track player
		x += velx;
		y += vely;
		
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
		
		update();
	}

	public void collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);

			for(int j = 0; j < orbitals.size(); j ++) {
				if(tempObject != null && orbitals.get(j) != null && tempObject != this) {
					
					if(tempObject.id == ID.Player) {

						if(orbitals.get(j).getBounds().intersects(tempObject.getBounds())) {
							
							//apply damage and kockback
							tempObject.hp -= dmg;
							tempObject.velx = velx * 15;
							tempObject.vely = (float) (-Math.random() * tempObject.spd);
							
							tempObject.stun = true;
							tempObject.stunTimer = System.currentTimeMillis();
						}
						
					//destroy projectiles against walls
					}else if(tempObject.id == ID.Projectile) {
						
						//ricochet projectiles
						if(orbitals.get(j).getBounds().intersects(tempObject.getBounds())) {
							
							//calculate projection
							float dx = x - tempObject.x;
							float dy = y - tempObject.y;
							
							tempObject.rotation = (float) (Math.atan2(dy, dx) - Math.PI);
							
							float dis = (float) Math.hypot(dx, dy);
							tempObject.velx = (float) (((8 * dx) / dis) * -1);
							tempObject.vely = (float) (((8 * dy) / dis) * -1);
							
							//invert enemy
							tempObject.enemy = enemy;
							tempObject.color = color;
						}
					}
					
					
				}				
			}
			
			if(tempObject.id == ID.Player) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					
					float rot = (float) Math.atan2(tempObject.y - y, tempObject.x - x);
					tempObject.x += (float) (Math.cos(rot) * (width * 6));
					tempObject.y += (float) (Math.sin(rot) * (height * 6));
				}
			}
		}
		
	}

	public void render(Graphics g) {
		
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		for(int i = 0; i < orbitals.size(); i ++) {
			orbitals.get(i).render(g);
		}
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

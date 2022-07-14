package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Mimic extends GameObject {

	public boolean noticed = false;
	public Color altColor = Color.gray;
	
	public Mimic(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 32;
		height = 32;
		rotation = 0;
		
		name = "Mimic";
		
		enemy = true;
		maxhp = 100;
		hp = maxhp;
		dmg = 8;
		range = Game.HEIGHT / 4;
		score = 75;
		explodes = true;
		
		spd = 2;
		dodge = 5;
		
		color = new Color(181, 151, 110);
	}
	
	public void tick() {
		
		//track player
		x += velx;
		y += vely;
		
		targetEnemy();
		if(target != null) {
			noticed = true;
			
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
			
		}else {
			noticed = false;
			
			velx = 0;
			vely = 0;
		}

		collision();
	}

	public void collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);

			if(tempObject.id == ID.Player) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					float dx = tempObject.x - x;
					float dy = tempObject.y - y;
					float dis = (int) Math.hypot(dx,  dy);
						
					tempObject.x += dis;
					tempObject.y += dis;
						
					tempObject.hp -= dmg;
					
				}
			}
		}
		
	}

	public void render(Graphics g) {
		
		if(noticed) g.setColor(color);
		else g.setColor(altColor);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		if(!noticed) dmgTimer = (long) dmgTime;
		drawHealth(g);
	}

	public Rectangle getBounds() {
		if(halo < 0) {
			return new Rectangle((int) (x - width / 2 - 4), (int) ( y - height / 2 - 4), (int) (width + 8), (int) (height + 8));
		}else {
			return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		}
	}

}

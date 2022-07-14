package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Golemite  extends GameObject {

	public int shieldTime = 2500;
	public long shieldTimer = (long) shieldTime;
	
	public Golemite(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 16;
		height = 16;
		rotation = 0;
		
		name = "Golemite";
		
		enemy = true;
		maxhp = 125;
		hp = maxhp;
		maxHalo = 25;
		halo = maxHalo;
		dmg = 6;
		range = Game.HEIGHT / 2;
		score = 75;
		explodes = true;
		
		spd = (float) 1.5;
		dodge = 5;
		
		color = new Color(186, 132, 130);
	}
	
	public void tick() {
		
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

		//shield
		if(halo > 0 && hp < maxhp) {
			int dif = (int) (maxhp - hp);
			
			if(dif < halo) {
				hp = maxhp;
				halo -= dif;
				
			}else {
				hp += halo;
				halo = 0;
				shieldTimer = System.currentTimeMillis();
				
			}
			
		//regenerate shield
		}else if(halo == 0) {
			if(System.currentTimeMillis() - shieldTimer >= shieldTime) {
				halo = maxHalo;
			}
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
		
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		//draw shield
		if(halo > 0) {
			for(int i = 0; i < 3; i ++) {
				g.drawRect((int) (x - width / 2 - 4 + i), (int) (y - height / 2 - 4 + i), (int) (width + 8 - (i * 2)), (int) (height + 8 - (i * 2)));
			}
		}
		
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

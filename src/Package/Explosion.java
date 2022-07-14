package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Explosion extends GameObject {

	public PartSystem system;
	long timer = System.currentTimeMillis();
	int time = 100;
	float radius = 0.1f;
	
	public Explosion(int x, int y, int w, int h, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.width = w;
		this.height = h;
		this.handler = handler;
		this.system = system;
		
		Game.cam.minishake = true;
		Game.cam.minitimer = System.currentTimeMillis();
		
		enemy = false;
		
		int lvl = w / 8;
		time = 50 * lvl;
		radius = 0.11f - 0.01f * lvl;
		
		int r = (int) (5 + Math.random() * 15);
		for(int i = 0; i < r; i ++) {
			int xx = (int) (x - Game.WIDTH / 4 + (Math.random() * (Game.WIDTH / 2)));
			int yy = (int) (y - Game.HEIGHT / 4 + (Math.random() * (Game.HEIGHT / 2)));
			
			Color c = Color.lightGray.darker();
			if(Chance(20)) c = Color.orange;
			
			system.addPart(new Pixel((int) x, (int) y, xx, yy, 0, radius, 6, 1, c, system, handler, true));
		}
		
		Game.sound.playSound("/Explode.wav");
	}

	public void tick() {
		
		if(System.currentTimeMillis() - timer >= time) {
			handler.removeObject(this);
		}
		
		if(Chance(50)) {
			int xx = (int) (x - Game.WIDTH / 4 + (Math.random() * (Game.WIDTH / 2)));
			int yy = (int) (y - Game.HEIGHT / 4 + (Math.random() * (Game.HEIGHT / 2)));
			
			Color c = Color.lightGray.darker();
			if(Chance(20)) c = Color.orange;
			
			system.addPart(new Pixel((int) x, (int) y, xx, yy, 0, radius, 6, 1, c, system, handler, true));
		}

		//Damage entity
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			//if inflictable entity
			if(tempObject.getBounds() != null && tempObject != this) {	
				if(getBounds().intersects(tempObject.getBounds())) {
					
					if(tempObject.enemy != enemy) {
						
						tempObject.hp --;

						if(tempObject.id != ID.Wall && !tempObject.dead) {
							int xx = (int) (tempObject.x - tempObject.width / 2 + (Math.random() * tempObject.width));
							int yy = (int) (tempObject.y - tempObject.height / 2 + (Math.random() * tempObject.height));
							
							handler.addObject(new DmgIndicator(xx, yy, String.valueOf(1), false, handler, system, ID.Ind));
						}
						
					}
				}
			}
		}
		
	}

	public void render(Graphics g) {
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Reward extends GameObject {

	public Reward(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		width = 28;
		height = 16;
		dodge = 100;
		score = 100 * (int) (1 + Math.random() * 9);
		
		color = new Color(255, 0, 220);
	}

	public void tick() {

		//create particles
		int xx = (int) ((x - width / 2) + (Math.random() * width));
		int yy = (int) ((y - height / 2) + (Math.random() * (height / 2)));
		system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy - 1, (float) 0.1, (int) 4, -2, color, system, handler, false));
		
		//check player collision
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null & tempObject.id == ID.Player) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					
					int r = (int) (10 + Math.random() * 10);
					for(int n = 0; n < r; n ++) {
						int _x = (int) (x - 32 + (Math.random() * 64));
						int _y = (int) (y - 32 + (Math.random() * 64));
						system.addPart(new Pixel((int) _x, (int) _y, 0, (int) _x - velx, (int) _y - vely, (float) 0.2, 4, -2, Color.yellow.brighter(), system, handler, true));
					}
					
					Player player = (Player) tempObject;
					player.points += score;
					player.totalP += score;
					
					handler.removeObject(this);
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

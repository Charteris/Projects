package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Wall extends GameObject{

	private long Timer = System.currentTimeMillis();
	private int time = 500;
	
	public Wall(int x, int y, int width, int height, int time, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.width = width;
		this.height = height;
		this.time = time;
		this.handler = handler;
		
		color = Color.gray;
	}

	public void tick() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getBounds() != null) {
				if(getBounds().intersects(tempObject.getBounds())) {
					
					if(tempObject.id == ID.Powerup && tempObject != null) {
						handler.removeObject(tempObject);
					}
					
				}
			}
		}
		
		//destroy wall if not permanent
		if(width != height || time != 0) {
			
			if(System.currentTimeMillis() - Timer >= time) {
				handler.removeObject(this);
			}
			
		}
		
	}
	
	public void render(Graphics g) {
		
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

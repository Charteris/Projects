package Package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Pixel extends Particle {

	private Handler handler;
	boolean gravity = false;
	
	public Pixel(int x, int y, float dx, float dy, float rotation, float life, int size, int spd, Color color, PartSystem system, Handler handler, boolean gravity) {
		super(x, y, dx, dy, rotation, life, size, spd, system);
		this.color = color;
		this.handler = handler;
		this.gravity = gravity;
		
		if(!gravity) {
			if(y != dy) {
				vely = spd;
			}else {
				vely = 0;
			}
				
			if(x != dy) {
				velx = spd;
			}else {
				velx = 0;
			}
		}
		
	}

	public void tick() {
		
		if(gravity) {
			vely += 0.2f;
			rotation = (float) Math.atan2(vely, velx);
		}
		
		x += velx;
		y += vely; 
		
		if(alpha > life) {
			alpha -= life - 0.0001f;
		}else {
			system.removePart(this);
		}
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null) {
				if(tempObject.id == ID.Wall && getBounds().intersects(tempObject.getBounds())) {
					system.removePart(this);
					
				}
			}
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		if(alpha < 0) alpha = 0;
		else if(alpha > 1) alpha = 1;
		g2d.setComposite(makeTransparent(alpha));

		g.setColor(color);
		g.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
		
		g2d.setComposite(makeTransparent(1));
	}

	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
	}

}

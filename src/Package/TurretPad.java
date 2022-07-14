package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class TurretPad extends GameObject {

	public TurretArray parent;
	public boolean active = false;
	public int num = 0;
	
	public TurretPad(int x, int y, TurretArray parent, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		
		this.parent = parent;
		width = parent.width + 4;
		height = 8;
		
		color = parent.color;
	
	}

	public void tick() {
		
		if(active) {
			Color col = Color.black;
			
			if(num == 0) col = Color.red.darker();
			else if(num == 1) col = Color.white.darker();
			else if(num == 2) col = Color.blue.darker();
			else if(num == 3) col = Color.magenta.darker();
			else if(num == 4) col = Color.orange.darker();
			
			//create particles
			int xx = (int) ((x - width / 2) + (Math.random() * width));
			int yy = (int) ((y - height / 2) + (Math.random() * (height / 2)));
			system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy - 1, (float) 0.1, (int) 4, -2, col, system, handler, false));
		}
		
	}

	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect((int) (x - width / 2),  (int) (y - height / 2),  (int) width, (int) height);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2),  (int) (y - height / 2),  (int) width, (int) height);
	}

	
}

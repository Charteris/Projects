package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Powerup extends GameObject {

	public Powerup(int x, int y, int power, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.system = system;
		this.handler = handler;
		
		width = 12;
		height = 12;
		
		//Determine powerup							> All powerups last 3 seconds without external upgrades 
		int r = (int) (Math.random() * 4);
		if(power != -1) r = power;
		
		switch(r) {
		case 0:
			color = Color.green.darker();		// Rapid fire
			hp = 1;
			break;
		case 1:
			color = Color.red.darker();			// Damage boost
			hp = 2;
			break;
		case 2:
			color = Color.blue.darker();		// Regeneration
			hp = 3;
			break;
		case 3:
			color = Color.orange.darker();		// Accurate shots with increased range
			hp = 4;
			break;
		case 4:
			color = Color.magenta.darker();		// Immediately cools down all abilities (instant)
			hp = 5;
			break;
		case 5:
			color = Color.white;				// Become invulnerable to damage
			hp = 6;
			break;
			default:
				break;
		}	//		!!!ADD A 1% CHANCE OF A RAINBOW POWERUP WHICH GRANTS ALL POWERUPS FOR TWICE THE DURATION!!!

	}

	public void tick() {

		//create particles
		int xx = (int) ((x - width / 2) + (Math.random() * width));
		int yy = (int) ((y - height / 2) + (Math.random() * (height / 2)));
		system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy - 1, (float) 0.1, (int) 4, -2, color, system, handler, false));
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.PI / 4, x, y);
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		g2d.setTransform(old);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

package Package;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class DmgIndicator extends GameObject {

	String ind;
	boolean crit = false;
	
	long timer = System.currentTimeMillis();
	int time = 300;
	
	public DmgIndicator(int x, int y, String ind, boolean crit, Handler handler, PartSystem system, ID id) {
		super(x, y, id, handler, system);
		this.ind = ind;
		this.crit = crit;
		this.handler = handler;
	}

	public void tick() {
		y --;
		
		if(System.currentTimeMillis() - timer >= time) {
			handler.removeObject(this);
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.darkGray.darker());
		Font f = new Font("Arial", Font.PLAIN, 12);
		
		if(crit) f = new Font("Arial", Font.BOLD, 12);
		
		g.setFont(f);
		g.drawString(ind, (int) x - g.getFontMetrics(f).stringWidth(ind) / 2, (int) y);
	}
	
	public Rectangle getBounds() {
		return null;
	}
	
}

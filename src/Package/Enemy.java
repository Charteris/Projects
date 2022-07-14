package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Enemy extends GameObject {

	public PartSystem system;
	public boolean grounded = false;
	
	public Enemy(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.handler = handler;
		this.system = system;
		
		width = 20;
		height = 20;
		rotation = 0;
		
		maxhp = 20;
		hp = maxhp;
		
		dmg = 2;
		
		score = 50;
		
		color = Color.pink.darker();
	}

	public void tick() {

		x += velx;
		y += vely;

		move();
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}

package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Hive extends GameObject {

	public int spawnTime = (int) (1000 + Math.random() * 4000);
	public long spawnTimer = (long) spawnTime;
	
	public Hive(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		width = 16;
		height = 20;
		rotation = 0;
		
		name = "Hive";
		
		enemy = true;
		maxhp = 75;
		hp = maxhp;
		range = 480;
		score = 15;
		
		dodge = 0;
		
		color = new Color(212, 255, 127);
	}

	public void tick() {

		//spawn fireflies
		if(System.currentTimeMillis() - spawnTimer >= spawnTime) {
			int r = (int) (2 + Math.random() * 5);
				
			for(int i = 0; i < r; i ++) {
				int xx = (int) ((x - width * 2) + (Math.random() * (width * 4)) );
				int yy = (int) ((y - height * 2) + (Math.random() * (height * 4)) );
					
				handler.addObject(new FireFlies(xx, yy, ID.Firefly, handler, system, true));
			}
				
			spawnTime = (int) (1000 + Math.random() * 4000);
			spawnTimer = System.currentTimeMillis();
		}
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		drawHealth(g);
		
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

	
}

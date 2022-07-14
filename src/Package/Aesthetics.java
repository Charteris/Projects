package Package;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Aesthetics {

	private Handler handler;
	private PartSystem system;
	
	//platform coordinates
	int platX = Game.WIDTH / 4, platY = Game.HEIGHT * 4 / 6, platW = Game.WIDTH / 3, platH = Game.HEIGHT / 10, size = platW * 2 / 3;
	
	public Aesthetics(Handler handler, PartSystem system) {
		this.handler = handler;
		this.system = system;
	}

	public void tick() {
		
		//generate random particles
		int r = (int) (Math.random() * 5) + 5;
		for(int i = 0; i < r; i ++) {

			int xx = (int) ((platX - platW / 2) + (Math.random() * platW));
			int yy = platY;
			
			if(xx > (platX - size / 2) && xx < (platX + size / 2)) {
				yy -= size;
			}
			
			system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx, (int) yy - 1, (float) 0.1, (int) 4, -2, Color.lightGray, system, handler, false));
		}
		
	}

	public void render(Graphics g) {
	
		//draw platform
		g.setColor(Color.darkGray);
		g.fillOval(platX - platW / 2, platY - platH / 2, platW, platH);
		
		//draw player
		g.setColor(Color.cyan.darker());
		g.fillRect(platX - size / 2, platY - size, size, size);
		
	}
	
	public void drawAbilities(Graphics g, int ability, int x, int y) {
		
		int w = 20, h = 20;
		g.setColor(Color.gray.brighter());
		g.fillRoundRect(x - w, y - h, w * 2, h * 2, 4, 4);
		
		if(ability != 0) {
			g.setColor(Color.gray);
			g.fillRoundRect(x - w, y + h / 4 * 3, w * 2, h / 4, 4, 4);
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.drawString(String.valueOf(ability), x - w, y - h + 13);
		}

		g.setColor(Color.black);
		g.drawRoundRect(x - w, y - h, w * 2, h * 2, 4, 4);
				
		//no ability
		if(ability == 0) {
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString("N/A", x - g.getFontMetrics(g.getFont()).stringWidth("N/A") / 2, y + 8);
			
		//draw wall
		}else if(ability == 1) {	
			g.setColor(Color.cyan.darker());
			g.fillRect(x - 16, y - 1, 16, 16);
			g.setColor(Color.gray);
			g.fillRect(x + 6, y - 9, 10, 24);
			
		//draw nova
		}else if(ability == 2) {
			g.setColor(Color.cyan);
			g.fillRect(x - 10, y - 5, 20, 20);
			g.setColor(Color.cyan.darker());
			g.fillRect(x - 2, y + 7, 4, 4);
			g.fillRect(x - 9, y + 6, 4, 4);
			g.fillRect(x + 5, y + 6, 4, 4);
			g.fillRect(x - 15, y + 3, 4, 4);
			g.fillRect(x + 11, y + 3, 4, 4);
			
		//draw teleport
		}else if(ability == 3) {
			g.setColor(Color.cyan);
			g.fillRect(x - 18, y - 1, 16, 8);
			g.drawLine(x - 18, y + 10, x - 1, y + 10);
			g.drawLine(x - 18, y + 14, x - 1, y + 14);
			g.setColor(Color.cyan.darker());
			g.drawLine(x + 2, y - 1, x + 18, y - 1);
			g.drawLine(x + 2, y + 3, x + 18, y + 3);
			g.fillRect(x + 2, y + 7, 16, 8);
			
		//draw duplicate
		}else if(ability == 4) {
			g.setColor(Color.cyan);
			g.fillRect(x - 10, y - 5, 20, 20);
			g.setColor(Color.cyan.darker());
			g.fillRect(x - 16, y + 3, 12, 12);
			g.fillRect(x + 4, y + 3, 12, 12);
			
		//draw conjure
		}else if(ability == 5) {
			g.setColor(Color.cyan.darker());
			g.fillRect(x - 16, y - 1, 16, 16);
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform old = g2d.getTransform();
			g2d.rotate(Math.PI / 4, x + 10, y + 2);
			g.setColor(Color.yellow.darker());
			g.fillRect(x + 6, y - 2, 8, 8);
			g2d.setTransform(old);
			g.fillRect(x + 3, y + 10, 14, 5);
			
		}
		
	}

}
